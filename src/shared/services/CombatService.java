package shared.services;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import shared.Constants;
import shared.Util;
import shared.enums.ActionType;
import shared.enums.Items;

import java.util.List;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static shared.RunescriptAbstractContext.logScript;

public class CombatService extends AbstractService {

    private static CombatService instance;

    private final SharedService sharedService;
    private final InventoryService inventoryService;

    private CombatService() {
        super();
        this.sharedService = SharedService.getInstance();
        this.inventoryService = InventoryService.getInstance();
    }

    public static CombatService getInstance() {
        if (instance == null)
            instance = new CombatService();
        return instance;
    }

    public void combatLoot(List<String> targets, List<Items> lootIds, Area area, boolean buryBones) {

        if (!area.contains(ctx.getLocalPlayer())) {
            // Go to given area
            sharedService.walkTo(area);
            return;
        }

        if (ctx.getLocalPlayer().isInCombat()) {
            // Sleep until player is not in combat
            sleepUntil(() -> !ctx.getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
            return;
        }

        // Getting nearest chicken (target) that is not in a combat
        NPC target = ctx.getNpcs().closest(t -> t != null && !t.isInCombat() && Util.isElementInList(t.getName(), targets.toArray()));
        // Getting nearest loots in the ground
        GroundItem loot = ctx.getGroundItems().closest(Util.getItemIds(lootIds));

        Double lootDistance = loot != null ? loot.distance(ctx.getLocalPlayer()) : null;
        Double targetDistance = target != null ? target.distance(ctx.getLocalPlayer()) : null;

        while (loot != null && ctx.getMap().canReach(loot) && target != null && lootDistance < targetDistance) {
            sharedService.getLoot(loot);
            loot = ctx.getGroundItems().closest(Util.getItemIds(lootIds));
            targetDistance = target.distance(ctx.getLocalPlayer());
        }

        if (buryBones)
            inventoryService.buryBones();

        // If nearest target exists and player is not in combat (double check here)
        if (target != null && ctx.getMap().canReach(target) && !ctx.getLocalPlayer().isInCombat()) {
            attackTarget(target);
        }
    }

    private void attackTarget(NPC target) {
        logScript("Attacking target: " + target.getName());
        if (target.interact("Attack")) {
            sleepUntil(() -> ctx.getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> !ctx.getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
        }
        antibanService.antibanSleep(ActionType.SlowPace);
    }


}
