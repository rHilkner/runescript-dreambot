package Shared.Services;

import Shared.Constants;
import Shared.Enums.AntibanActions;
import Shared.RunescriptAbstractContext;
import Shared.Util;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.items.GroundItem;

import static Shared.RunescriptAbstractContext.logScript;
import static Shared.Services.AntibanService.antibanSleep;
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
            antibanSleep(AntibanActions.SLOW_PACE);
        } else {
            if (all) {
                ctx.getBank().depositAllItems();
            } else {
                ctx.getBank().depositAll(item -> Util.isElementInList(item.getID(), itemIDs));
            }
            antibanSleep(AntibanActions.FAST_PACE);
            ctx.getBank().close();
            antibanSleep(AntibanActions.FAST_PACE);
        }
    }

    public void walkTo(Area area) {
        logScript("Walking to: " + area.toString());
        if (!area.contains(ctx.getLocalPlayer())) {
            ctx.getWalking().walk(area.getRandomTile());
            antibanSleep(AntibanActions.WALKING);
        }
    }

    public void getLoot(GroundItem loot) {
        logScript("Getting loot from the ground");
        loot.interact("Take");
        sleep(RunescriptAbstractContext.getLatency());
        sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
        antibanSleep(AntibanActions.FAST_PACE);
    }
    
}
