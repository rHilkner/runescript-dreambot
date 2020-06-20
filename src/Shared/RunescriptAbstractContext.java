package Shared;

import Shared.Enums.AntibanActions;
import Shared.Services.AntibanService;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class RunescriptAbstractContext extends AbstractScript {

    public static RunescriptAbstractContext ctx;
    public Date startDate;
    private Date nextAfkSleepDate;
    private Date nextLogoutSleepDate;

    public static void logScript(String str) {
        Date currentDate = new Date();
        DateFormat df = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
        log(df.format(currentDate) + str);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.startDate = new Date();
        this.resetSleepDates();
        ctx = this;
    }

    @Override
    public int onLoop() {
        loopCounter++;
        logScript("Iteration: " + loopCounter);

        if (loopCounter % 1000 == 0) {
            // every 1000 iterations logout + sleep 15-30min
            getTabs().logout();
            int randomSleep = Calculations.random(15*60*1000, 30*60*1000);
            logScript("1000 iterations: sleep(" + randomSleep + ")");
            sleep(randomSleep);
            logScript("Awake from 1000 iterations sleep - going to login");
        } else if (loopCounter == 40 || loopCounter % 100 == 0) {
            // every 100 iterations sleep 30-120s
            int randomSleep = Calculations.random(30000, 120000);
            logScript("100 iterations: sleep(" + randomSleep + ")");
            sleep(randomSleep);
            logScript("Awake from 100 iterations sleep");
        }

        return 0;
    }

    @Override
    public void onExit() {
        logScript("Exiting script");
    }

    /** How long does it take the game to register your actions? change this depending on CPU/RAM/lag */
    public static int getLatency() {
        return Calculations.random(50, 110);
    }

    private void resetSleepDates() {
        this.nextAfkSleepDate = AntibanService.getNextSleepDate(AntibanActions.AFK);
        this.nextLogoutSleepDate = AntibanService.getNextSleepDate(AntibanActions.LOGOUT);
    }

}
