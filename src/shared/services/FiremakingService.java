package shared.services;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.items.Item;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.enums.Items;

public class FiremakingService extends AbstractService {

    private static FiremakingService instance;

    private final AntibanService antibanService;
    private final SharedService sharedService;

    private FiremakingService() {
        super();
        this.antibanService = AntibanService.getInstance();
        this.sharedService = SharedService.getInstance();
    }

    public static FiremakingService getInstance() {
        if (instance == null)
            instance = new FiremakingService();
        return instance;
    }

    public void fireLogs(String logsName, boolean walkIfFail) {
        while (ctx.getInventory().contains(logsName)) {
            if (ctx.getLocalPlayer().isAnimating()) {
                continue;
            }

            Item tinderbox = ctx.getInventory().get(Items.Tinderbox.name);
            Item logs = ctx.getInventory().get(logsName);
            if (tinderbox == null || logs == null) {
                continue;
            }

            if (tinderbox.useOn(logs)) {
                ctx.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.Latency);
            } else if (walkIfFail) {
                // Trying to walk 1 tile to the north or south because can't light fire in current tile
                if (!ctx.getWalking().walk(new Tile(ctx.getLocalPlayer().getTile().getX(), ctx.getLocalPlayer().getTile().getY()+1))) {
                    ctx.getWalking().walk(new Tile(ctx.getLocalPlayer().getTile().getX(), ctx.getLocalPlayer().getTile().getY()-1));
                }
                antibanService.antibanSleep(AntibanActionType.FastPace);
            } else {
                antibanService.antibanSleep(AntibanActionType.Latency);
            }
        }
    }

}
