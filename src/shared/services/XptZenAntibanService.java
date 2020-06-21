package shared.services;

import org.dreambot.api.methods.Calculations;
import shared.enums.ActionType;
import shared.enums.DistractionType;
import shared.enums.GameStyle;

import java.util.Date;

import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static shared.RunescriptAbstractContext.logScript;

public class XptZenAntibanService extends  AbstractService {

    private static XptZenAntibanService instance;

    private ZenAntibanAdapted zenAntibanAdapted;

    /** SINGLETON METHODS */

    private XptZenAntibanService() {
        super();
        zenAntibanAdapted = new ZenAntibanAdapted(ctx);
    }

    public static XptZenAntibanService getInstance() {
        if (instance == null)
            logScript("d");
            instance = new XptZenAntibanService();
        logScript("def");
        return instance;
    }

    /** ANTIBAN METHODS */

    public void antiban() {
        logScript("soz");
//        antibanRandomAction();
//        antibanDistraction();
    }

    public void antibanRandomAction() {
        int sleepTimeMillis = zenAntibanAdapted.antiban();
        sleep(sleepTimeMillis);
    }

    public void antibanDistraction() {
        Date currentDate = new Date();

        for (DistractionType distraction : ctx.getDistractions()) {
            if (distraction.getNextDistractionDate().after(currentDate)) {
                sleep(distraction.getDistractionSleep(ctx.getGameStyle()));
            }
        }
    }

    public void antibanSleep(ActionType action) {

        int sleepTime;

        switch (action) {
            case Spam:
                sleepTime = getSleepDuration(ActionType.Spam);
                logScript("antibanRandomAction Spam: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case FastPace:
                sleepTime = getSleepDuration(ActionType.FastPace);
                logScript("antibanRandomAction FastPace: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case SlowPace:
                sleepTime = getSleepDuration(ActionType.SlowPace);
                logScript("antibanRandomAction SlowPace: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case Walking:
                sleepTime = getSleepDuration(ActionType.Walking);
                logScript("antibanRandomAction Walking: sleepUntil(!localPlayer.isMoving(), " + sleepTime + ")");
                sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), sleepTime);
                break;
        }
    }

    private int getSleepDuration(ActionType actionType) {

        int time;
        int reaction = Calculations.random(200, 400);
        double walkingPatience = Calculations.random(1500, 3000);

        GameStyle gameStyle = ctx.getGameStyle();

        double realBaseReaction = gameStyle.getBaseReactionTime() / 2; // divide by the mean value of the shape (1.5) since for gamma distribution: E(X) = shape * scale
        double minScale = realBaseReaction - 0.15 * realBaseReaction;
        double maxScale = realBaseReaction + 0.15 * realBaseReaction;

        int shape = Calculations.random(1, 4);
        int scale = (int) Calculations.random(minScale, maxScale);

        switch (gameStyle) {
            case HardCore:
                shape = Calculations.random(1, 3);
                break;
            case Normal:
                shape = Calculations.random(1, 4);
                break;
            case Lazy:
                shape = Calculations.random(2, 4);
                break;
            case VeryLazy:
                shape = Calculations.random(2, 5);
                break;
        }

        switch (actionType) {
            case Spam:
                scale = (int) Calculations.random(minScale * 0.6, maxScale * 0.6); // P(X < 3s) = 0.5 - shape = 2; scale = 1.8k
                break;
            case FastPace:
                scale = (int) Calculations.random(minScale * 0.8, maxScale * 0.8); // P(X < 3s) = 0.5 - shape = 2; scale = 1.8k
                break;
            case SlowPace:
                scale = (int) Calculations.random(minScale * 1.2, maxScale * 1.2); // P(X < 3s) = 0.5 - shape = 2; scale = 1.8k
                break;
            case Walking:
                if (!ctx.getWalking().isRunEnabled()) {
                    walkingPatience *= 2;
                }
                scale = (int) Calculations.random(minScale, maxScale); // P(X < 3s) = 0.5 - shape = 2; scale = 1.8k
                break;
        }

        if (actionType == ActionType.Walking) {
            time = (int) (Calculations.nextGammaRandom(shape, scale) + walkingPatience);
        } else {
            time = (int) (Calculations.nextGammaRandom(shape, scale)) + reaction;
        }

        return time;
    }
}
