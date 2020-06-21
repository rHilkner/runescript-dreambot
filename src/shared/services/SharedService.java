package shared.services;

import org.dreambot.api.wrappers.interactive.NPC;
import shared.Constants;
import shared.enums.ActionType;
import shared.RunescriptAbstractContext;
import shared.Util;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.items.GroundItem;

import static shared.RunescriptAbstractContext.logScript;
import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class SharedService extends AbstractService {

    private static SharedService instance;

    /** SINGLETON METHODS */

    private SharedService() {
        super();
    }

    public static SharedService getInstance() {
        if (instance == null)
            instance = new SharedService();
        return instance;
    }

    /** STATIC FUNCTIONS */

    public void walkTo(Area area) {
        logScript("Walking to: " + area.toString());
        if (!area.contains(ctx.getLocalPlayer())) {
            ctx.getWalking().walk(area.getRandomTile());
            antibanService.antibanSleep(ActionType.Walking);
        }
    }

    public void getLoot(GroundItem loot) {
        logScript("Getting loot from the ground");
        loot.interact("Take");
        sleep(RunescriptAbstractContext.getLatency());
        sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
        antibanService.antibanSleep(ActionType.FastPace);
    }
    
}
