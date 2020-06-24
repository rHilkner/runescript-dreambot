package shared.services;

import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.wrappers.interactive.GameObject;
import shared.Constants;
import shared.Util;
import shared.enums.ActionType;
import shared.enums.Trees;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class WoodcuttingService extends AbstractService {

    private static WoodcuttingService instance;

    private XptZenAntibanService antibanService;

    private WoodcuttingService() {
        super();
        this.antibanService = XptZenAntibanService.getInstance();
    }

    public static WoodcuttingService getInstance() {
        if (instance == null)
            instance = new WoodcuttingService();
        return instance;
    }

    public void chopTree(Trees tree) {
        GameObject treeObject = ctx.getGameObjects().closest(tree.getTreeName());
        if (treeObject != null && treeObject.interact("Chop down")) {
            ctx.logScript("Chopping down " + tree.getTreeName());
            antibanService.antibanSleep(ActionType.SlowPace);
            if (ctx.getLocalPlayer().isAnimating()) {
                sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(ActionType.FastPace);
            }
        }
    }

}
