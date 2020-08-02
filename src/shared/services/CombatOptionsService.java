package shared.services;

import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import shared.enums.AntibanActionType;
import shared.enums.AttackStyles;

public class CombatOptionsService extends AbstractService {


    private static CombatOptionsService instance;

    private final AntibanService antibanService;
    private final SharedService sharedService;
    private final InventoryService inventoryService;

    private CombatOptionsService() {
        super();
        sharedService = SharedService.getInstance();
        inventoryService = InventoryService.getInstance();
        antibanService = AntibanService.getInstance();
    }

    public static CombatOptionsService getInstance() {
        if (instance == null)
            instance = new CombatOptionsService();
        return instance;
    }

    public boolean toggleAttackStyle(AttackStyles attackStyle, boolean openInventoryAfter) {
        if (!ctx.getTabs().isOpen(Tab.COMBAT)) {
            ctx.getTabs().open(Tab.COMBAT);
        }

        WidgetChild attackStyleWidget = null;
        switch (attackStyle) {
            case CHOP:
                attackStyleWidget = ctx.getWidgets().getWidgetChild(593, 5);
                break;
            case SLASH:
                attackStyleWidget = ctx.getWidgets().getWidgetChild(593, 9);
                break;
            case LUNGE:
                attackStyleWidget = ctx.getWidgets().getWidgetChild(593, 13);
                break;
            case BLOCK:
                attackStyleWidget = ctx.getWidgets().getWidgetChild(593, 17);
                break;
        }

        if (attackStyleWidget == null) {
            return false;
        }

        boolean didInteract = attackStyleWidget.interact();
        antibanService.antibanSleep(AntibanActionType.SlowPace);

        if (openInventoryAfter) {
            ctx.getTabs().open(Tab.INVENTORY);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }

        return didInteract;
    }

}
