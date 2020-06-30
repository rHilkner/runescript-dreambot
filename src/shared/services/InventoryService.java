package shared.services;

import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.items.Item;
import shared.Constants;
import shared.enums.ActionType;
import shared.enums.Items;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class InventoryService extends AbstractService {

    private static InventoryService instance;

    private final XptZenAntibanService antibanService;

    private InventoryService() {
        super();
        this.antibanService = XptZenAntibanService.getInstance();
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
