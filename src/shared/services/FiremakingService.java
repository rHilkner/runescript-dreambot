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


    public boolean lightFire(String logsName, boolean walkIfFail) {
        if (ctx.getLocalPlayer().isAnimating()) {
            return false;
        }

        Item tinderbox = ctx.getInventory().get(Items.Tinderbox.name);
        Item logs = ctx.getInventory().get(logsName);
        if (tinderbox == null || logs == null) {
            ctx.logScript("No tinderbox or logs found");
            return false;
        }

        int logsOriginalSlot = logs.getSlot();
        Tile playerOriginalTile = ctx.getLocalPlayer().getTile();
        int counter = 0;
        do {
            tinderbox.useOn(logs);
            ctx.sleepUntil(() -> ctx.getInventory().isSlotEmpty(logsOriginalSlot), 5000);
            antibanService.antibanSleep(AntibanActionType.Latency);

            if (ctx.getInventory().isSlotEmpty(logsOriginalSlot)) {
                ctx.logScript("Fireing logs: " + logsName);
                ctx.sleepUntil(() -> ctx.getLocalPlayer().getTile() != playerOriginalTile && !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.Latency);
            } else {
                ctx.logScript("Couldn't fire logs (" + logsName + ") - walking 1 tile north (or south if walking north fails)");
                // Trying to walk 1 tile to the north or south because can't light fire in current tile
                if (!ctx.getWalking().walk(new Tile(ctx.getLocalPlayer().getTile().getX(), ctx.getLocalPlayer().getTile().getY()+1))) {
                    ctx.getWalking().walk(new Tile(ctx.getLocalPlayer().getTile().getX(), ctx.getLocalPlayer().getTile().getY()-1));
                }
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }

            counter++;
        } while (!ctx.getInventory().isSlotEmpty(logsOriginalSlot) && counter < 20);

        return ctx.getInventory().isSlotEmpty(logsOriginalSlot);
    }

    public void fireLogs(String logsName, boolean walkIfFail) {
        while (ctx.getInventory().contains(logsName)) {
            if (ctx.getLocalPlayer().isAnimating()) {
                continue;
            }

            Item tinderbox = ctx.getInventory().get(Items.Tinderbox.name);
            Item logs = ctx.getInventory().get(logsName);
            if (tinderbox == null || logs == null) {
                ctx.logScript("No tinderbox or logs found");
                continue;
            }

            int logsOriginalSlot = logs.getSlot();
            Tile playerOriginalTile = ctx.getLocalPlayer().getTile();
            tinderbox.useOn(logs);
            ctx.sleepUntil(() -> ctx.getInventory().isSlotEmpty(logsOriginalSlot), 5000);
            antibanService.antibanSleep(AntibanActionType.Latency);

            if (ctx.getInventory().isSlotEmpty(logsOriginalSlot)) {
                ctx.logScript("Fireing logs: " + logsName);
                ctx.sleepUntil(() -> ctx.getLocalPlayer().getTile() != playerOriginalTile && !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.Latency);
            } else if (walkIfFail) {
                ctx.logScript("Couldn't fire logs (" + logsName + ") - walking 1 tile north (or south if walking north fails)");
                // Trying to walk 1 tile to the north or south because can't light fire in current tile
                if (!ctx.getWalking().walk(new Tile(ctx.getLocalPlayer().getTile().getX(), ctx.getLocalPlayer().getTile().getY()+1))) {
                    ctx.getWalking().walk(new Tile(ctx.getLocalPlayer().getTile().getX(), ctx.getLocalPlayer().getTile().getY()-1));
                }
                antibanService.antibanSleep(AntibanActionType.FastPace);
            } else {
                ctx.logScript("Couldn't fire logs (" + logsName + ")");
                antibanService.antibanSleep(AntibanActionType.SlowPace);
            }
        }
    }

}
