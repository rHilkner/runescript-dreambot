package shared.services;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Areas;

import java.util.Objects;

import static org.dreambot.api.methods.MethodProvider.sleep;
import static scriptz.RunescriptAbstractContext.logScript;

public class SharedService extends AbstractService {

    private static SharedService instance;

    private final AntibanService antibanService;
    private InteractService interactService = null;

    /** SINGLETON METHODS */

    private SharedService() {
        super();
        this.antibanService = AntibanService.getInstance();
        // It will cause a cyclic dependency if you add any other service here...
    }

    public static SharedService getInstance() {
        if (instance == null)
            instance = new SharedService();
        return instance;
    }

    private void setInteractService() {
        if (this.interactService == null) {
            this.interactService = InteractService.getInstance();
        }
    }

    /** STATIC FUNCTIONS */

    public boolean walkTo(Areas area) {

        if (area == null) {
            logScript("Area is null, not walking");
            return false;
        }

        return walkTo(area.getArea());
    }

    public boolean walkTo(Area area) {

        if (area == null) {
            logScript("Area is null, not walking");
            return false;
        }

        if (area.contains(ctx.getLocalPlayer())) {
            logScript("Player already in area on tile: " + ctx.getLocalPlayer().getTile());
        } else {
            Tile randomTile = area.getRandomTile();
            walkTo(randomTile);
        }

        return area.contains(ctx.getLocalPlayer());
    }

    public boolean walkTo(Tile tile) {

        if (tile == null) {
            logScript("Tile is null, not walking");
            return false;
        } else if (!ctx.getMap().canReach(tile)) {
            logScript("Tile " + tile + " is unreachable, not walking");
            return false;
        }

        // loop stops if player standing in the same position for more than 20 counts
        int counter = 0;
        Tile playerLastTile = ctx.getLocalPlayer().getTile();
        Tile playerTileBeforeLast = null;
        while (!Objects.equals(ctx.getLocalPlayer().getTile(), tile) && counter < 8) {
            ctx.getWalking().walk(tile);
            logScript("Walking to: " + tile);
            Util.sleepUntil(() -> ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.Walking);
            counter++;
            if (Objects.equals(ctx.getLocalPlayer().getTile(), playerTileBeforeLast)) {
                logScript("Player went to a tile that he already was... he looped. Breaking walk-to execution.");
                break;
            } else if (!Objects.equals(ctx.getLocalPlayer().getTile(), playerLastTile)) {
                counter = 0;
            }
            playerTileBeforeLast = new Tile(playerLastTile.getX(), playerLastTile.getY(), playerLastTile.getZ());
            playerLastTile = ctx.getLocalPlayer().getTile();
        }

        return Objects.equals(ctx.getLocalPlayer().getTile(), tile);
    }

    public boolean walkToClosest(Area area) {

        if (area == null) {
            logScript("Area is null, not walking");
            return false;
        }

        if (area.contains(ctx.getLocalPlayer())) {
            logScript("Player already in area on tile: " + ctx.getLocalPlayer().getTile());
        } else {
            Tile nearestTile = area.getNearestTile(ctx.getLocalPlayer());
            walkTo(nearestTile);
        }

        return area.contains(ctx.getLocalPlayer());
    }

    public void takeLoot(GroundItem loot) {
        logScript("Getting loot from the ground: " + loot.getName() + " on " + loot.getTile());
        if (loot.interact("Take")) {
            sleep(RunescriptAbstractContext.getLatency());
            Util.sleepUntil(() -> !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
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

    public boolean openInventory() {
        if (!ctx.getTabs().isOpen(Tab.INVENTORY)) {
            ctx.getTabs().open(Tab.INVENTORY);
        }
        return ctx.getTabs().isOpen(Tab.INVENTORY);
    }

    public boolean deselectAnyItem() {
        if (ctx.getInventory().isItemSelected()) {
            ctx.getInventory().deselect();
        }
        return ctx.getInventory().isItemSelected();
    }

    public void eatAnything() {
        openInventory();
        deselectAnyItem();
        // setting interact service to be able to use it below
        setInteractService();

        for (Item item : ctx.getInventory().all()) {
            if (item != null && item.hasAction("Eat")) {
                interactService.interactInventoryItem(item.getName(), "Eat");
                Util.sleepUntil(() -> ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                break;
            }
        }
    }
}
