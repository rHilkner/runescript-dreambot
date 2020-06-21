package shared.services;

import shared.Constants;
import shared.enums.ActionType;
import shared.enums.Items;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.items.Item;

import static shared.RunescriptAbstractContext.logScript;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class InventoryService extends AbstractService {

    private static InventoryService instance;

    private InventoryService() {
        super();
    }

    public static InventoryService getInstance() {
        if (instance == null)
            instance = new InventoryService();
        return instance;
    }

    public void buryBones() {
        logScript("Burying bones");
        if (!ctx.getTabs().isOpen(Tab.INVENTORY))
            ctx.getTabs().open(Tab.INVENTORY);

        while (ctx.getInventory().contains(Items.BONES.id)) {
            Item bones = ctx.getInventory().get(Items.BONES.id);
            bones.interact("Bury");
            sleepUntil(() -> !ctx.getLocalPlayer().isStandingStill(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(ActionType.FastPace);
        }
        antibanService.antibanSleep(ActionType.FastPace);
    }

}