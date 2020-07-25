package shared.services;

import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import shared.Constants;
import shared.enums.AntibanActionType;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class InteractService extends AbstractService {

    private static InteractService instance;

    private final XptZenAntibanService antibanService;
    private final InventoryService inventoryService;

    private InteractService() {
        super();
        antibanService = XptZenAntibanService.getInstance();
        inventoryService = InventoryService.getInstance();
    }

    public static InteractService getInstance() {
        if (instance == null)
            instance = new InteractService();
        return instance;
    }

    public void interactInventoryItems(String itemName1, String itemName2, boolean spam, boolean interactWithLast) {
        Item item1 = ctx.getInventory().get(itemName1);
        Item item2 = interactWithLast ? inventoryService.getLastItem(itemName2) : ctx.getInventory().get(itemName2);

        if (spam) {
            while (item1 != null && item2 != null) {
                Item finalItem = item1;
                Item finalItem1 = item2;
                sleepUntil(() -> finalItem.useOn(finalItem1), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
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
}
