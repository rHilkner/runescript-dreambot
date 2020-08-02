package shared.services;

import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import shared.Constants;
import shared.enums.AntibanActionType;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class InteractService extends AbstractService {

    private static InteractService instance;

    private final AntibanService antibanService;
    private final InventoryService inventoryService;

    private InteractService() {
        super();
        antibanService = AntibanService.getInstance();
        inventoryService = InventoryService.getInstance();
    }

    public static InteractService getInstance() {
        if (instance == null)
            instance = new InteractService();
        return instance;
    }

    public boolean takeClosestGroundItem(String itemName) {
        int counter = 0;
        GroundItem item = ctx.getGroundItems().closest(itemName);
        while (item != null && item.exists() && counter < 20) {
            logScript("Taking item from the ground: " + itemName);
            if (item.interact("Take")) {
                return true;
            }
            sleepUntil(() -> ctx.getInventory().contains(itemName), Constants.MAX_SLEEP_UNTIL);
            counter++;
        }
        return false;
    }

    public boolean interactGameObjectWithInventoryItem(String itemName, String gameObjectName, boolean itemFirst) {
        Item item = ctx.getInventory().get(itemName);
        GameObject gameObject = ctx.getGameObjects().closest(gameObjectName);

        ctx.logScript("Trying to interact item " + itemName + " with object " + gameObjectName);

        if (item != null && gameObject != null && gameObject.exists()) {
            logScript("Interacting item " + itemName + " with game-object " + gameObjectName);
            if (itemFirst) {
                item.useOn(gameObject);
            } else {
                gameObject.interact();
                item.interact();
            }
            antibanService.antibanSleep(AntibanActionType.FastPace);
            return true;
        }

        return false;
    }

    public boolean interactWithGameObject(String gameObjectName, String action) {
        GameObject gameObject = ctx.getGameObjects().closest(gameObjectName);

        if (gameObject != null && gameObject.exists() && gameObject.hasAction(action)) {
            logScript("Interacting with game-object " + gameObjectName + " with action " + action);
            gameObject.interact(action);
            sleepUntil(() -> !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            return true;
        }

        return false;
    }

    public boolean interactWithGameObject(String gameObjectName) {
        GameObject gameObject = ctx.getGameObjects().closest(gameObjectName);

        if (gameObject != null && gameObject.exists()) {
            logScript("Interacting with game-object " + gameObjectName);
            gameObject.interact();
            sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            return true;
        }

        return false;
    }

    public void interactInventoryItem(int slot, String action) {
        Item item = ctx.getInventory().get(i -> i != null && i.getSlot() == slot);
        if (item != null) {
            logScript("Interacting with item in inventory: " + item.getName() + " on slot " + slot);
            if (item.interact(action)) {
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        } else {
            logScript("item fucking null?");
        }
    }

    public void interactInventoryItems(String itemName1, String itemName2, boolean spam, boolean interactWithLast) {
        Item item1 = ctx.getInventory().get(itemName1);
        Item item2 = interactWithLast ? inventoryService.getLastItem(itemName2) : ctx.getInventory().get(itemName2);

        logScript("Interacting inventory item " + itemName1 + " with item " + itemName2);
        if (spam) {
            while (item1 != null && item2 != null) {
                Item finalItem = item1;
                Item finalItem1 = item2;
                finalItem.useOn(finalItem1);
                antibanService.antibanSleep(AntibanActionType.Latency);
                item1 = ctx.getInventory().get(itemName1);
                item2 = interactWithLast ? inventoryService.getLastItem(itemName2) : ctx.getInventory().get(itemName2);
            }
        } else {

            if (item1 != null && item2 != null) {
                Item finalItem = item1;
                Item finalItem1 = item2;
                sleepUntil(() -> finalItem.useOn(finalItem1), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }

            sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void interactWithWidget(WidgetChild widgetChild) {
        logScript("Interacting with widget: " + widgetChild.getText());
        sleepUntil(widgetChild::interact, Constants.MAX_SLEEP_UNTIL);
        antibanService.antibanSleep(AntibanActionType.FastPace);
    }

    public boolean interactClosestNpc(String npcName, String action) {
        NPC npc = ctx.getNpcs().closest(npcName);
        if (npc != null && npc.hasAction(action)) {
            logScript("Interacting with closest NPC " + npcName + " with action " + action);
            boolean didInteract = npc.interact(action);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            return didInteract;
        }
        return false;
    }
}
