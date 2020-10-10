package shared.services;

import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.items.Item;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;

import java.util.Comparator;
import java.util.List;

import static scriptz.RunescriptAbstractContext.logScript;

public class InventoryService extends AbstractService {

    private static InventoryService instance;

    private final AntibanService antibanService;
    private final SharedService sharedService;

    private InventoryService() {
        super();
        this.antibanService = AntibanService.getInstance();
        this.sharedService = SharedService.getInstance();
    }

    public static InventoryService getInstance() {
        if (instance == null)
            instance = new InventoryService();
        return instance;
    }

    public void buryBones(String specificBonesName) {

        logScript("Burying bones");

        String bonesName;

        if (specificBonesName == null) {
            bonesName = "Bones";
        } else {
            bonesName = specificBonesName;
        }

        sharedService.openInventory();

        while (ctx.getInventory().contains(bonesName)) {
            Item bones = ctx.getInventory().get(bonesName);
            int initialBonesCount = ctx.getInventory().count(bonesName);
            bones.interact("Bury");
            Util.sleepUntil(() -> ctx.getInventory().count(bonesName) != initialBonesCount, Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
        antibanService.antibanSleep(AntibanActionType.FastPace);
    }

    Item getLastItem(String itemName) {
        List<Item> allItems = ctx.getInventory()
                .all(i -> i != null && i.getName() != null && i.getName().equals(itemName));

        if (allItems != null && !allItems.isEmpty()) {
            return allItems.stream().max(Comparator.comparingInt(Item::getSlot)).orElse(null);
        }
        return null;
    }

}
