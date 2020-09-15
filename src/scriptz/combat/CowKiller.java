package scriptz.combat;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
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

@ScriptManifest(author = "xpt", name = "Cow Killer", category = Category.COMBAT, version = 1.0, description = "Kills cow and loot their bones and ")
public class CowKiller extends RunescriptAbstractContext {

    enum State { BANK, GO_TO_COW_PEN, KILL_COW, KEEP_KILLING_COW, EAT, STOP }

    private int initialHitpointsXp = -1;
    private int initialAttackXp = -1;
    private int initialStrengthXp = -1;
    private int initialDefenceXp = -1;
    private int initialMagicXp = -1;
    private int initialRangedXp = -1;
    private int initialPrayerXp = -1;

    private final String[] targets = new String[]{GameObjects.Cow.name};
    private final String[] loot = new String[]{Items.IronArrow.name};
    private final List<String> itemsToEat = Arrays.asList(Items.Shrimps.name, Items.Trout.name);

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

        if (getLocalPlayer().getHealthPercent() < 15) {
            return State.STOP;
        }

        logScript("health % = " + getLocalPlayer().getHealthPercent());
        boolean inventoryContainsItemsToEat = false;
        for (Item item : getInventory().all()) {
            if (item != null && itemsToEat.contains(item.getName())) {
                inventoryContainsItemsToEat = true;
            }
        }

        if (inventoryContainsItemsToEat && getLocalPlayer().getHealthPercent() < 60) {
            return State.EAT;
        }

        if (!getInventory().contains(i -> i!= null && itemsToEat.contains(i.getName()))) {
            return State.BANK;
        }

        if (!Areas.LumbridgeEastCowPen.getArea().contains(getLocalPlayer())) {
            return State.GO_TO_COW_PEN;
        }

        if (getLocalPlayer().isInCombat()) {
            return State.KEEP_KILLING_COW;
        }

        return State.KILL_COW;
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

        printPlayerStats();

        switch (currentState) {

            case BANK:
//                if (sharedService.walkTo(Areas.LumbridgeBank)) {
                bankService.bankAll(false);

                for (int i = 0; i < itemsToEat.size(); i++) {
                    String fishName = itemsToEat.get(i);
                    if (getInventory().isEmpty() && getBank().count(fishName) > 0) {
                        bankService.withdraw(fishName, 24, false, false);
                        break;
                    }
                }

                bankService.closeBank();
//                }
                break;

            case GO_TO_COW_PEN:
                bankService.closeBank(); // just to be sure
                sharedService.walkTo(Areas.LumbridgeEastCowPen);
                break;

            case KILL_COW:
                equipIronArrows();
                combatService.combatLoot(targets, loot,
                        Areas.LumbridgeEastCowPen.getArea(),
                        null, false, false);
                equipIronArrows();
                break;

            case KEEP_KILLING_COW:
                int counter = 0;
                while (getLocalPlayer().isInCombat() || counter < 4) {
                    antibanService.antibanSleep(AntibanActionType.FastPace);
                    if (getLocalPlayer().isAnimating() || getLocalPlayer().isInCombat()) {
                        counter = 0;
                    } else {
                        counter++;
                    }
                }
                break;

            case EAT:
                List<Item> itemList = getInventory().all().stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(Item::getSlot)).collect(Collectors.toList());
                for (Item item : itemList) {
                    logScript("Item name: " + item.getName());
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
                sharedService.walkTo(Areas.LumbridgeBank);
                sharedService.logout();
                stop();
                break;
        }

        return 0;
    }

    public void equipIronArrows() {
        if (getInventory().contains(i -> i != null && Items.IronArrow.name.equals(i.getName()))) {
            logScript("Equiping iron arrows");
            Item ironArrow = getInventory().get(Items.IronArrow.name);
            ironArrow.interact();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    private void printPlayerStats() {

        if (initialAttackXp == -1) {
            initialAttackXp = getSkills().getExperience(Skill.ATTACK);
        }

        if (initialStrengthXp == -1) {
            initialStrengthXp = getSkills().getExperience(Skill.STRENGTH);
        }

        if (initialDefenceXp == -1) {
            initialDefenceXp = getSkills().getExperience(Skill.DEFENCE);
        }

        int attackXpGained = getSkills().getExperience(Skill.ATTACK) - initialAttackXp;
        int strengthXpGained = getSkills().getExperience(Skill.ATTACK) - initialAttackXp;
        int defenceXpGained = getSkills().getExperience(Skill.ATTACK) - initialAttackXp;
    }
}