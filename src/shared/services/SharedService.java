package shared.services;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.enums.Areas;

import java.util.Objects;

import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class SharedService extends AbstractService {

    private static SharedService instance;

    private final AntibanService antibanService;

    /** SINGLETON METHODS */

    private SharedService() {
        super();
        this.antibanService = AntibanService.getInstance();
    }

    public static SharedService getInstance() {
        if (instance == null)
            instance = new SharedService();
        return instance;
    }

    /** STATIC FUNCTIONS */

    public boolean walkTo(Areas area) {

        if (area == null) return false;

        return walkTo(area.getArea());
    }

    public boolean walkTo(Area area) {

        if (area == null) return false;

        Tile randomTile = area.getRandomTile();

        logScript("Walking to: " + randomTile);
        if (!area.contains(ctx.getLocalPlayer())) {
            if (ctx.getWalking().walk(randomTile)) {
                antibanService.antibanSleep(AntibanActionType.Walking);
            }
        }

        return area.contains(ctx.getLocalPlayer());
    }

    public void walkToRandomTile(Area area) {

        if (area == null) return;

        Tile randomTile = area.getRandomTile();

        if (ctx.getMap().canReach(randomTile) && ctx.getWalking().walk(randomTile)) {
            logScript("Walking to: " + randomTile);
            sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.Walking);
        }
    }

    public void walkToTile(Tile tile) {

        if (tile == null) return;

        if (ctx.getMap().canReach(tile) && ctx.getWalking().walk(tile)) {
            logScript("Walking to: " + tile);
            sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.Walking);
        }
    }

    public void takeLoot(GroundItem loot) {
        logScript("Getting loot from the ground: " + loot.getName() + " on " + loot.getTile());
        if (loot.interact("Take")) {
            sleep(RunescriptAbstractContext.getLatency());
            sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        } else {
            ctx.getWalking().walk(loot.getTile());
        }
    }

    public GameObject getObjectOnTileWithName(Tile tile, String objectName) {

        if (tile == null || objectName == null) {
            return null;
        }

        GameObject[] gameObjects = ctx.getGameObjects().getObjectsOnTile(tile);

        if (gameObjects == null || gameObjects.length == 0) {
            return null;
        }

        for (GameObject object : gameObjects) {
            if (Objects.equals(objectName, object.getName())) {
                return object;
            }
        }

        return null;
    }

    public void enableLoginSolver() {
        ctx.getRandomManager().enableSolver(RandomEvent.LOGIN);
    }

    public void disableLoginSolver() {
        ctx.getRandomManager().disableSolver(RandomEvent.LOGIN);
    }

    public void logout() {
        disableLoginSolver();
        ctx.getTabs().logout();
    }
}
