package shared.services;

import org.dreambot.api.methods.world.World;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldHopService extends AbstractService {

    private static WorldHopService instance;

    private final AntibanService antibanService;
    private final SharedService sharedService;
    private final InventoryService inventoryService;

    private WorldHopService() {
        super();
        sharedService = SharedService.getInstance();
        inventoryService = InventoryService.getInstance();
        antibanService = AntibanService.getInstance();
    }

    public static WorldHopService getInstance() {
        if (instance == null)
            instance = new WorldHopService();
        return instance;
    }

    public boolean open() {
        if (!ctx.getWorldHopper().isWorldHopperOpen()) {
            ctx.getWorldHopper().openWorldHopper();
            antibanService.antibanSleep(AntibanActionType.SlowPace);
        }

        return ctx.getWorldHopper().isWorldHopperOpen();
    }

    public boolean hopNext(boolean normalized, boolean f2pOnly, boolean noMininumLevel) {
        ctx.logScript("Trying to hop to next world");

        if (!open()) {
            return false;
        }

        List<World> worldList = getFilteredWorldList(normalized, f2pOnly, noMininumLevel);

        worldList.sort(Comparator.comparingInt(World::getWorld));

        int currentWorld = ctx.getWorlds().getMyWorld().getWorld();
        World maxWorldPossible = worldList.stream().max(Comparator.comparingInt(World::getWorld)).orElse(null);
        World minWorldPossible = worldList.stream().min(Comparator.comparingInt(World::getWorld)).orElse(null);

        if (maxWorldPossible == null || minWorldPossible == null) {
            return false;
        }

        int nextWorld = minWorldPossible.getWorld();
        if (currentWorld == maxWorldPossible.getWorld()) {
            // go back to first world
            nextWorld = minWorldPossible.getWorld();
        } else {
            // find first world higher
            for (World world : worldList) {
                if (world.getWorld() > currentWorld) {
                    nextWorld = world.getWorld();
                    break;
                }
            }
        }

        if (ctx.getWorldHopper().hopWorld(nextWorld) && ctx.getWorlds().getMyWorld().getWorld() == nextWorld) {
            ctx.logScript("Hopped to world " + ctx.getWorlds().getMyWorld().getWorld());
            Util.sleepUntil(() -> ctx.getLocalPlayer().isOnScreen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.SlowPace);
            return true;
        }

        ctx.logScript("Couldn't hop to world " + nextWorld);
        return false;
    }

    public List<World> getFilteredWorldList(boolean normalized, boolean f2pOnly, boolean noMininumLevel) {
        List<World> worldList;

        if (normalized) {
            worldList = ctx.getWorlds().getNormalizedWorlds();
        } else {
            worldList = ctx.getWorlds().all();
        }

        if (f2pOnly) {
            worldList = worldList.stream().filter(World::isF2P).collect(Collectors.toList());
        }

        if (noMininumLevel) {
            worldList = worldList.stream().filter(w -> w.getMinimumLevel() == 0).collect(Collectors.toList());
        }
        return worldList;
    }

    public boolean hopRandom(boolean normalized, boolean f2pOnly, boolean noMininumLevel) {
        List<World> worldList = getFilteredWorldList(normalized, f2pOnly, noMininumLevel);
        int goToWorld = worldList.get(new Random().nextInt(worldList.size())).getWorld();

        if (ctx.getWorldHopper().hopWorld(goToWorld)) {
            antibanService.antibanSleep(AntibanActionType.SlowPace);
        }

        return ctx.getWorlds().getMyWorld().getWorld() == goToWorld;
    }

}
