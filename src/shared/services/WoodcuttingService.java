package shared.services;

import org.dreambot.api.wrappers.interactive.GameObject;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Trees;

public class WoodcuttingService extends AbstractService {

    private static WoodcuttingService instance;

    private final AntibanService antibanService;

    private WoodcuttingService() {
        super();
        this.antibanService = AntibanService.getInstance();
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
            antibanService.antibanSleep(AntibanActionType.SlowPace);
            if (ctx.getLocalPlayer().isAnimating()) {
                Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        }
    }

}
