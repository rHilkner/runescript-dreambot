package scriptz;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import shared.enums.DistractionType;
import shared.enums.GameStyle;
import shared.services.SharedService;
import shared.services.XptZenAntibanService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class RunescriptAbstractContext extends AbstractScript {

    public static RunescriptAbstractContext ctx;
    private GameStyle gameStyle;
    private List<DistractionType> distractions;

    protected XptZenAntibanService antibanService;
    protected SharedService sharedService;
    private Date startDate;

    public static void logScript(String str) {
        Date currentDate = new Date();
        DateFormat df = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
        log(df.format(currentDate) + str);
    }

    @Override
    public void onStart() {
        super.onStart();
        RunescriptAbstractContext.ctx = this;
        setGameStyle(GameStyle.Normal);
        this.antibanService = XptZenAntibanService.getInstance();
        this.sharedService = SharedService.getInstance();
        this.startDate = new Date();
        this.distractions = Arrays.asList(DistractionType.PhoneNotification, DistractionType.TalkingToSomeone, DistractionType.LittleLogout);
    }

    @Override
    public int onLoop() {

        if (startDate != null) {
            long currentDateTime = new Date().getTime();
            long secondsSinceBeginning = (currentDateTime - startDate.getTime()) / 1000;
            int hours = (int) (secondsSinceBeginning / 3600);
            int minutes = (int) ((secondsSinceBeginning % 3600) / 60);
            int seconds = (int) ((secondsSinceBeginning % 3600) % 60);
            String time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
            logScript("Running for " + time);
        }

        antibanService.antiban();

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

    public GameStyle getGameStyle() {
        return gameStyle;
    }

    public void setGameStyle(GameStyle gameStyle) {
        if (this.gameStyle != gameStyle) {
            logScript("Setting GameStyle to: " + gameStyle);
            this.gameStyle = gameStyle;
        }
    }

    public List<DistractionType> getDistractions() {
        return distractions;
    }

    public Date getStartDate() {
        return startDate;
    }
}
