package shared.services;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.enums.ActionType;
import shared.enums.Areas;

import java.util.Objects;

import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

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

    public void walkTo(Areas area) {

        if (area == null) return;

        walkTo(area.getArea());
    }

    public void walkTo(Area area) {

        if (area == null) return;

        Tile randomTile = area.getRandomTile();

        logScript("Walking to: " + randomTile);
        if (!area.contains(ctx.getLocalPlayer())) {
            ctx.getWalking().walk(randomTile);
            antibanService.antibanSleep(ActionType.Walking);
        }
    }

    public void takeLoot(GroundItem loot) {
        logScript("Getting loot from the ground");
        loot.interact("Take");
        sleep(RunescriptAbstractContext.getLatency());
        sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
        antibanService.antibanSleep(ActionType.FastPace);
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
    
}
