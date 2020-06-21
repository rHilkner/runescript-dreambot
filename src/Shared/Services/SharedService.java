package Shared.Services;

import Shared.Constants;
import Shared.Enums.ActionType;
import Shared.RunescriptAbstractContext;
import Shared.Util;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.items.GroundItem;

import static Shared.RunescriptAbstractContext.logScript;
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

    public void bankItems(Integer[] itemIDs, boolean all) {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().open(ctx.getBank().getClosestBankLocation());
            antibanService.antibanSleep(ActionType.SlowPace);
        } else {
            if (all) {
                ctx.getBank().depositAllItems();
            } else {
                ctx.getBank().depositAll(item -> Util.isElementInList(item.getID(), itemIDs));
            }
            antibanService.antibanSleep(ActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(ActionType.FastPace);
        }
    }

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
