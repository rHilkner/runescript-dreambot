package shared.services;

import org.dreambot.api.wrappers.items.GroundItem;
import shared.Constants;
import shared.RunescriptAbstractContext;
import shared.enums.ActionType;
import shared.enums.Areas;

import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static shared.RunescriptAbstractContext.logScript;

public class SharedService extends AbstractService {

    private static SharedService instance;
    private XptZenAntibanService antibanService;

    /** SINGLETON METHODS */

    private SharedService() {
        super();
        antibanService = XptZenAntibanService.getInstance();
    }

    public static SharedService getInstance() {
        if (instance == null)
            instance = new SharedService();
        return instance;
    }

    /** STATIC FUNCTIONS */

    public void walkTo(Areas area) {
        logScript("Walking to: " + area.getArea().getRandomTile().getX());
        if (!area.getArea().contains(ctx.getLocalPlayer())) {
            ctx.getWalking().walk(area.getArea().getRandomTile());
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
