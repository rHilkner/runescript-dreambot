package shared.services;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class CombatService extends AbstractService {

    private static CombatService instance;

    private final AntibanService antibanService;
    private final SharedService sharedService;
    private final InventoryService inventoryService;

    private CombatService() {
        super();
        sharedService = SharedService.getInstance();
        inventoryService = InventoryService.getInstance();
        antibanService = AntibanService.getInstance();
    }

    public static CombatService getInstance() {
        if (instance == null)
            instance = new CombatService();
        return instance;
    }

    public void combatLoot(String[] targets, String[] lootItems, Area area, boolean prioritizeLoot, boolean buryBones) {

        if (ctx.getLocalPlayer().isInCombat()) {
            // Sleep until player is not in combat
            sleepUntil(() -> !ctx.getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
            return;
        }

        if (prioritizeLoot) {
            takeLootLoop(lootItems);
        } else {
            takeLootIfCloseEnough(targets, lootItems);
        }

        if (buryBones) {
            inventoryService.buryBones(null);
        }

        NPC target = closestTargetNotInCombat(targets);
        if (target == null || !ctx.getMap().canReach(target)) {
            ctx.getWalking().walk(area.getRandomTile());
        } else if (!ctx.getLocalPlayer().isInCombat()) {
            // If nearest target exists and player is not in combat (double check here)
            attackTarget(target);
        }
    }

    private NPC closestTargetNotInCombat(String[] targets) {
        return ctx.getNpcs().closest(t -> t != null && !t.isInCombat() && Util.isElementInArray(t.getName(), targets));
    }

    private void takeLootIfCloseEnough(String[] targets, String[] lootItems) {
        // Getting nearest target that is not in a combat
        NPC target = closestTargetNotInCombat(targets);
        // Getting nearest loots in the ground
        GroundItem loot = ctx.getGroundItems().closest(lootItems);

        Double lootDistance = loot != null ? loot.distance(ctx.getLocalPlayer()) : null;
        Double targetDistance = target != null ? target.distance(ctx.getLocalPlayer()) : null;

        // while loot is closer than target, take loot
        while (loot != null && ctx.getMap().canReach(loot) && (target == null || lootDistance < targetDistance)) {
            sharedService.takeLoot(loot);
            antibanService.antibanSleep(AntibanActionType.FastPace);

            loot = ctx.getGroundItems().closest(lootItems);
            target = closestTargetNotInCombat(targets);
            targetDistance = target != null ? target.distance(ctx.getLocalPlayer()) : null;
        }
    }

    private void takeLootLoop(String[] lootItems) {
        GroundItem loot = ctx.getGroundItems().closest(lootItems);

        while (loot != null && ctx.getMap().canReach(loot)) {
            sharedService.takeLoot(loot);
            loot = ctx.getGroundItems().closest(lootItems);
        }
    }

    private void attackTarget(NPC target) {

        while (ctx.getDialogues().inDialogue() && ctx.getDialogues().canContinue()) {
            ctx.getDialogues().continueDialogue();
        }

        if (target.interact("Attack")) {
            logScript("Attacking target: " + target.getName() + " on " + target.getTile());
            sleepUntil(() -> ctx.getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> !ctx.getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        } else {
            logScript("Walking to attack target: " + target.getName() + " on " + target.getTile());
            sharedService.walkToTile(target.getTile());
        }
    }


}
