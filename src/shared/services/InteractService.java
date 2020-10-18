package shared.services;

import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;

import java.util.Objects;

import static scriptz.RunescriptAbstractContext.logScript;

public class InteractService extends AbstractService {

    private static InteractService instance;

    private final AntibanService antibanService;
    private final SharedService sharedService;
    private final InventoryService inventoryService;

    private InteractService() {
        super();
        antibanService = AntibanService.getInstance();
        sharedService = SharedService.getInstance();
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
        while (item != null && item.exists() && counter < 8) {
            logScript("Taking item from the ground: " + itemName);
            int initialLootCount = ctx.getInventory().count(itemName);
            if (item.interact("Take")) {
                Util.sleepUntil(() -> ctx.getInventory().count(itemName) != initialLootCount, Constants.MAX_SLEEP_UNTIL);
                return true;
            }
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
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
            return true;
        }

        return false;
    }

    public boolean interactWithGameObject(GameObject gameObject) {

        if (gameObject != null && gameObject.exists()) {
            logScript("Interacting with game-object [" + gameObject.getName() + "]");
            gameObject.interact();
            Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            return true;
        }

        return false;
    }

    public boolean interactWithGameObject(String gameObjectName, String action) {
        GameObject gameObject = ctx.getGameObjects().closest(o -> o != null && Objects.equals(o.getName(), gameObjectName) && o.hasAction(action));

        if (gameObject == null) {
            logScript("Game-object " + gameObjectName + " doesn't exist or doesn't have the action " + action);
            return false;
        }

        logScript("Interacting with game-object [" + gameObjectName + "] with action [" + action + "]");
        gameObject.interact(action);
        Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
        antibanService.antibanSleep(AntibanActionType.FastPace);

        return false;
    }

    public boolean interactWithGameObject(String gameObjectName) {
        GameObject gameObject = ctx.getGameObjects().closest(gameObjectName);

        if (gameObject != null && gameObject.exists()) {
            logScript("Interacting with game-object [" + gameObjectName + "]");
            gameObject.interact();
            Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            return true;
        }

        return false;
    }

    public void interactInventoryItem(String name, boolean spam) {
        Item item = ctx.getInventory().get(i -> i != null && Objects.equals(i.getName(), name));
        if (spam) {
            while (ctx.getInventory().contains(name)) {
                int itemSlot = item.getSlot();
                logScript("Interacting with item in inventory: " + item.getName());
                item.interact();
                antibanService.antibanSleep(AntibanActionType.Tick);
//                antibanService.antibanSleep(AntibanActionType.Latency);
//                Util.sleepUntil(() -> ctx.getInventory().getItemInSlot(itemSlot) == null || !Objects.equals(ctx.getInventory().getItemInSlot(itemSlot).getName(), name), Util.getGaussianBetween(1000, 2000));
                item = ctx.getInventory().get(i -> i != null && Objects.equals(i.getName(), name));
            }
        } else if (item != null) {
            logScript("Interacting with item in inventory: " + item.getName());
            if (item.interact()) {
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        } else {
            logScript("item fucking null?");
        }
    }

    public void interactInventoryItem(String name, String action) {
        Item item = ctx.getInventory().get(i -> i != null && Objects.equals(i.getName(), name));
        if (item != null) {
            logScript("Interacting with item in inventory: " + item.getName());
            if (item.interact(action)) {
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        } else {
            logScript("item fucking null?");
        }
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
                item1 = ctx.getInventory().get(itemName1);
                item2 = interactWithLast ? inventoryService.getLastItem(itemName2) : ctx.getInventory().get(itemName2);
            }
        } else if (item1 != null && item2 != null) {
            item1.useOn(item2);
        }
        antibanService.antibanSleep(AntibanActionType.FastPace);
    }

    public void interactFullInventory(String itemName) {
        for (int i = 0; i <= 27; i++) {
            Item itemInSlot = ctx.getInventory().getItemInSlot(i);
            if (itemInSlot != null && Objects.equals(itemInSlot.getName(), itemName)) {
                itemInSlot.interact();
//                antibanService.antibanSleep(AntibanActionType.Latency);
            }
        }
    }

    public void interactWithWidget(WidgetChild widgetChild) {
        logScript("Interacting with widget: " + widgetChild.getText());
        Util.sleepUntil(widgetChild::interact, Constants.MAX_SLEEP_UNTIL);
        antibanService.antibanSleep(AntibanActionType.FastPace);
    }

    public boolean interactClosestNpc(String npcName, String action, boolean quickly) {
        NPC npc = ctx.getNpcs().closest(npcName);
        if (npc != null && (action == null || npc.hasAction(action))) {
            logScript("Interacting with closest NPC " + npcName + " with action " + action);
            boolean didInteract = action == null ? npc.interact() : npc.interact(action);
            if (!quickly) {
                Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
            return didInteract;
        }
        return false;
    }

    public boolean useItemOnNpc(String itemName, String npcName) {
        Item item = ctx.getInventory().get(itemName);
        NPC npc = ctx.getNpcs().closest(npcName);
        if (item != null && npc != null) {
            logScript("Using item " + itemName + " on NPC " + npcName);
            sharedService.openInventory();
            boolean didInteract = item.useOn(npc);
            Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating() && !ctx.getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            return didInteract;
        }
        return false;
    }
}
