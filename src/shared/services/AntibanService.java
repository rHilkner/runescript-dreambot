package shared.services;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import shared.enums.AntibanActionType;
import shared.enums.AntibanDistractionType;
import shared.enums.GameStyle;
import shared.services.providers.ZenAntibanAdapted;

import java.util.Date;

import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class AntibanService extends  AbstractService {

    private static AntibanService instance;

    private final ZenAntibanAdapted zenAntibanAdapted;
    private SharedService sharedService;

    /** SINGLETON METHODS */

    private AntibanService() {
        super();
        zenAntibanAdapted = new ZenAntibanAdapted(ctx);
    }

    public static AntibanService getInstance() {
        if (instance == null)
            instance = new AntibanService();
        return instance;
    }

    /** GETTERS AND SETTERS */

    public void setSkillsToHover(Skill... skills) {
        zenAntibanAdapted.setStatsToCheck(skills);
    }

    /** ANTIBAN METHODS */

    public void antiban() {
        if (sharedService == null) {
            sharedService = SharedService.getInstance();
        }
        sharedService.disableLoginSolver();
        if (!ctx.getLocalPlayer().isMoving()) {
            antibanRandomAction();
            antibanDistraction();
        }
        sharedService.enableLoginSolver();
    }

    public void antibanRandomAction() {
        int sleepTimeMillis = zenAntibanAdapted.antiban();
        if (sleepTimeMillis > 0) {
            logScript("antiban Random-action [" + zenAntibanAdapted.getStatus() + "] sleep(" + sleepTimeMillis + ")");
            sleep(sleepTimeMillis);
        }
    }

    public void antibanDistraction() {
        Date currentDate = new Date();

        for (AntibanDistractionType distraction : ctx.getDistractions()) {
            if (distraction.getNextDistractionDate().before(currentDate)) {
                int sleepTimeMillis = distraction.getDistractionSleep(ctx.getGameStyle());
                logScript("antiban Distraction [" + distraction + "]: sleep(" + sleepTimeMillis + ")");
                sleep(sleepTimeMillis);
                distraction.resetNextDistractionDate();
            }
        }
    }

    public void antibanSleep(AntibanActionType action) {

        int sleepTime;

        switch (action) {
            case Spam:
                sleepTime = getSleepDuration(AntibanActionType.Spam);
                logScript("antiban Spam: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case FastPace:
                sleepTime = getSleepDuration(AntibanActionType.FastPace);
                logScript("antiban FastPace: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case SlowPace:
                sleepTime = getSleepDuration(AntibanActionType.SlowPace);
                logScript("antiban SlowPace: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case Walking:
                sleepTime = getSleepDuration(AntibanActionType.Walking);
                logScript("antiban Walking: sleepUntil(!localPlayer.isMoving(), " + sleepTime + ")");
                sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), sleepTime);
                break;
            case Latency:
                sleepTime = getSleepDuration(AntibanActionType.Latency);
                logScript("antiban SlowPace: sleep(" + sleepTime + ")");
                break;
        }
    }

    private int getSleepDuration(AntibanActionType antibanActionType) {

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

        switch (antibanActionType) {
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
            case Latency:
                return Calculations.random(8, 50);
        }

        if (antibanActionType == AntibanActionType.Walking) {
            time = (int) (Calculations.nextGammaRandom(shape, scale) + walkingPatience);
        } else {
            time = (int) (Calculations.nextGammaRandom(shape, scale)) + reaction;
        }

        return time;
    }
}
