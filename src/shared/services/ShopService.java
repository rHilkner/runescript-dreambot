package shared.services;

import shared.enums.AntibanActionType;

import static scriptz.RunescriptAbstractContext.logScript;

public class ShopService extends AbstractService {

    private static ShopService instance;

    private final AntibanService antibanService;
    private final SharedService sharedService;
    private final InventoryService inventoryService;

    private ShopService() {
        super();
        sharedService = SharedService.getInstance();
        inventoryService = InventoryService.getInstance();
        antibanService = AntibanService.getInstance();
    }

    public static ShopService getInstance() {
        if (instance == null)
            instance = new ShopService();
        return instance;
    }

    public boolean openShop() {
        logScript("Trying to open shop");

        if (ctx.getShop().open()) {
            antibanService.antibanSleep(AntibanActionType.SlowPace);
        }

        return ctx.getShop().isOpen();
    }

    public boolean closeShop() {
        logScript("Trying to close shop");

        if (ctx.getShop().close()) {
            antibanService.antibanSleep(AntibanActionType.SlowPace);
        }

        return !ctx.getShop().isOpen();

    }

    public boolean sell(String itemName, int quantity, boolean closeShop) {
        boolean sold = false;

        if (!openShop()) {
            return false;
        }

        if (ctx.getShop().sell(itemName, quantity)) {
            logScript("Sold " + quantity + " items " + itemName);
            antibanService.antibanSleep(AntibanActionType.SlowPace);
            sold = true;
        } else {
            logScript("Couldn't sell item " + itemName);
            sold = false;
        }

        if (closeShop) {
            ctx.getShop().close();
        }

        return sold;
    }

}
