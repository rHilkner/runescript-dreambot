package Shared.Services;

import Shared.Enums.AntibanActions;
import Shared.Util;
import org.dreambot.api.methods.Calculations;

import java.util.Date;

import static Shared.RunescriptAbstractContext.getLatency;
import static Shared.RunescriptAbstractContext.logScript;
import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class AntibanService extends  AbstractService {

    private static AntibanService instance;

    /** SINGLETON METHODS */

    private AntibanService() {
        super();
    }

    public static AntibanService getInstance() {
        if (instance == null)
            instance = new AntibanService();
        return instance;
    }

    public static Date getNextSleepDate(AntibanActions action) {
        switch (action) {
            case AFK:
                return Util.dateAddSeconds(new Date(), Calculations.random(12*60,25*60));
            case LOGOUT:
                return Util.dateAddSeconds(new Date(), (int) Calculations.random(3.5*60*60,5.5*60*60));
        }
        return null;
    }

    public static int getSleepDuration(AntibanActions action) {

        int time;

        // minimum wait between actions
        int reaction = Calculations.random(200, 400);
        // minimum wait between running to a new spot while still running. patience is doubled while walking
        int patience = Calculations.random(1500, 3000);
        int walkingShape = Calculations.random(2, 5);
        int runningShape = Calculations.random(2, 4);
        int fastPaceShape = Calculations.random(2, 4);
        int slowPaceShape = Calculations.random(3, 5);
        int spamShape = 1;
        // proxy for variables in your physical environment affecting your attention
        int scale = Calculations.random(200, 600);

        switch (action) {
            case LATENCY:
                return getLatency();
            case WALKING:
                if (ctx.getWalking().isRunEnabled()) {
                    time = (int) (Calculations.nextGammaRandom(runningShape, scale)) + patience;
                } else {
                    time = (int) (Calculations.nextGammaRandom(walkingShape, scale)) + patience * 2;
                }
                return time;
            case FAST_PACE:
                time = (int) (Calculations.nextGammaRandom(fastPaceShape, scale)) + reaction;
                return time;
            case SLOW_PACE:
                time = (int) (Calculations.nextGammaRandom(slowPaceShape, scale)) + reaction;
                return time;
            case SPAM:
                time = (int) (Calculations.nextGammaRandom(spamShape, scale)) + reaction;
                return time;
            case AFK:
                time = (int) (Calculations.nextGammaRandom(spamShape, scale)) + reaction;
            case LOGOUT:
        }

        return 0;
    }

    public static void antibanSleep(AntibanActions action) {

        int sleepTime;

        switch (action) {
            case WALKING:
                sleepTime = getSleepDuration(AntibanActions.LATENCY);
                int walkingPatience = getSleepDuration(AntibanActions.WALKING);
                logScript("antiban WALKING: sleepUntil(!localPlayer.isMoving(), " + sleepTime + ")");
                sleep(sleepTime);
                sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), walkingPatience);
                break;
            case FAST_PACE:
                sleepTime = getSleepDuration(AntibanActions.FAST_PACE);
                logScript("antiban FAST_PACE: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case SLOW_PACE:
                sleepTime = getSleepDuration(AntibanActions.SLOW_PACE);
                logScript("antiban SLOW_PACE: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
            case SPAM:
                sleepTime = getSleepDuration(AntibanActions.SPAM);
                logScript("antiban SPAM: sleep(" + sleepTime + ")");
                sleep(sleepTime);
                break;
        }
    }
}
