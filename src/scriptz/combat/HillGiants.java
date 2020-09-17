package scriptz.combat;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.GameObjects;
import shared.enums.Items;
import shared.services.AntibanService;
import shared.services.BankService;
import shared.services.CombatService;
import shared.services.InteractService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ScriptManifest(author = "xpt", name = "Hill Giants Looter", category = Category.COMBAT, version = 1.0, description = "Kills hill giants and loot their bones and shit etc")
public class HillGiants extends RunescriptAbstractContext {

    private Areas hillGiantsArea = Areas.EdgevilleDungeonHillGiants;
    private Areas dungeonTopOutArea = Areas.EdgevilleDungeonVarrockTopOutside;
    private Areas dungeonTopArea = Areas.EdgevilleDungeonVarrockTop;
    private Areas dungeonLadderArea = Areas.EdgevilleDungeonHillGiantsLadder;
    private Areas bankArea = Areas.GrandExchange;

    enum State { GO_TO_BANK, BANK, GO_TO_DUNGEON, KILL_HILL_GIANT, KEEP_KILLIN, EAT, STOP }

    private final String[] targets = new String[]{GameObjects.HillGiant.name};
    private final String[] loot = new String[]{Items.GiantKey.name, Items.NatureRune.name, Items.NatureRune.name, Items.LawRune.name, Items.CosmicRune.name, Items.ChaosRune.name, Items.DeathRune.name};
    private final List<String> itemsToEat = Arrays.asList(Items.Salmon.name);

    private CombatService combatService;
    private AntibanService antibanService;
    private BankService bankService;
    private InteractService interactService;

    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.antibanService = AntibanService.getInstance();
        this.bankService = BankService.getInstance();
        this.interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.ATTACK, Skill.STRENGTH, Skill.HITPOINTS);
        logScript("Cow Killer starting - creditz to XpT º*º");
    }

    private State getState() {

        if (getLocalPlayer().getHealthPercent() < 20) {
            return State.STOP;
        }

        logScript("health % = " + getLocalPlayer().getHealthPercent());
        boolean inventoryContainsItemsToEat = false;
        for (Item item : getInventory().all()) {
            if (item != null && itemsToEat.contains(item.getName())) {
                inventoryContainsItemsToEat = true;
            }
        }

        if (inventoryContainsItemsToEat && getLocalPlayer().getHealthPercent() < 75) {
            return State.EAT;
        }

        if (!getInventory().contains(Items.BrassKey.name) || !getInventory().contains(i -> i!= null && itemsToEat.contains(i.getName()))) {
            if (bankArea.getArea().contains(getLocalPlayer())) {
                return State.BANK;
            }
            return State.GO_TO_BANK;
        }

        if (getLocalPlayer().isInCombat() || getLocalPlayer().isInteractedWith()) {
            return State.KEEP_KILLIN;
        }

        if (!hillGiantsArea.getArea().contains(getLocalPlayer())) {
            return State.GO_TO_DUNGEON;
        }

        if (getLocalPlayer().isInCombat()) {
            return State.KEEP_KILLIN;
        }

        return State.KILL_HILL_GIANT;
    }

    @Override
    public int onLoop() {

        super.onLoop();

        if (!getTabs().isOpen(Tab.INVENTORY)) {
            getTabs().open(Tab.INVENTORY);
        }

        if (getInventory().isItemSelected()) {
            getInventory().deselect();
        }

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {

            case GO_TO_BANK:
                if (hillGiantsArea.getArea().contains(getLocalPlayer())) {
                    sharedService.walkTo(dungeonLadderArea);
                }

                if (dungeonLadderArea.getArea().contains(getLocalPlayer())) {
                    if (interactService.interactWithGameObject("Ladder", "Climb-up")) {
                        Util.sleepUntil(() -> !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                        antibanService.antibanSleep(AntibanActionType.FastPace);
                        interactService.interactWithGameObject("Door", "Open");
                        Util.sleepUntil(() -> !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                        antibanService.antibanSleep(AntibanActionType.FastPace);
                    }
                }

                sharedService.walkTo(bankArea);

                break;

            case BANK:
                bankService.bankAllExcept(false, Items.BrassKey.name);

                if (!getInventory().contains(Items.BrassKey.name)) {
                    bankService.withdraw(Items.BrassKey.name, 1, false, false);
                }

                for (int i = 0; i < itemsToEat.size(); i++) {
                    String foodName = itemsToEat.get(i);
                    if (getBank().count(foodName) > 0 && !getInventory().isFull()) {
                        bankService.withdraw(foodName, 24, false, false);
                        if (getInventory().count(foodName) >= 24) {
                            break;
                        }
                    }
                }

                bankService.closeBank();
                break;

            case GO_TO_DUNGEON:
                bankService.closeBank(); // just to be sure

                if (sharedService.walkTo(dungeonTopOutArea)) {
                    Util.sleepUntil(() -> !getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                    antibanService.antibanSleep(AntibanActionType.FastPace);
                    if (interactService.interactWithGameObject("Door", "Open")) {
                        Util.sleepUntil(() -> !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                        antibanService.antibanSleep(AntibanActionType.FastPace);
                        interactService.interactWithGameObject("Ladder", "Climb-down");
                        Util.sleepUntil(() -> !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                        antibanService.antibanSleep(AntibanActionType.FastPace);
                    }
                }

                break;

            case KILL_HILL_GIANT:
                combatService.combatLoot(targets, loot,
                        hillGiantsArea.getArea(),
                        null, false, false);
                break;

            case KEEP_KILLIN:
                sleep(Calculations.random(300, 600));
                break;

            case EAT:
                List<Item> itemList = getInventory().all().stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(Item::getSlot)).collect(Collectors.toList());
                for (Item item : itemList) {
                    if (itemsToEat.contains(item.getName())) {
                        interactService.interactInventoryItem(item.getSlot(), "Eat");
                        break;
                    }
                }
                break;

            case STOP:
                if (!getWalking().isRunEnabled()) {
                    getWalking().toggleRun();
                }
                sharedService.walkTo(Areas.EdgevilleBank);
                sharedService.logout();
                stop();
                break;
        }

        return 0;
    }

}