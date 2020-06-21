package shared;

import shared.enums.DistractionType;
import shared.enums.GameStyle;
import shared.services.XptZenAntibanService;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class RunescriptAbstractContext extends AbstractScript {

    public static RunescriptAbstractContext ctx;
    private GameStyle gameStyle = GameStyle.Normal;
    private List<DistractionType> distractions;

    private XptZenAntibanService antibanService;
    private Date startDate;

    public static void logScript(String str) {
        Date currentDate = new Date();
        DateFormat df = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
        log(df.format(currentDate) + str);
    }

    @Override
    public void onStart() {
        super.onStart();
        ctx = this;
        this.antibanService = XptZenAntibanService.getInstance();
        this.startDate = new Date();
        this.distractions = Arrays.asList(DistractionType.PhoneNotification, DistractionType.TalkingToSomeone);
    }

    @Override
    public int onLoop() {
        antibanService.antibanRandomAction();
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

    public List<DistractionType> getDistractions() {
        return distractions;
    }

}
