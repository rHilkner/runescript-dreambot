package shared.services;

import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.items.Item;
import shared.Constants;
import shared.enums.AntibanActionType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    void buryBones(String specificBonesName) {

        logScript("Burying bones");

        String bonesName;

        if (specificBonesName == null) {
            bonesName = "Bones";
        } else {
            bonesName = specificBonesName;
        }

        if (!ctx.getTabs().isOpen(Tab.INVENTORY))
            ctx.getTabs().open(Tab.INVENTORY);

        while (ctx.getInventory().contains(bonesName)) {
            Item bones = ctx.getInventory().get(bonesName);
            bones.interact("Bury");
            sleepUntil(() -> !ctx.getLocalPlayer().isStandingStill(), Constants.MAX_SLEEP_UNTIL);
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
