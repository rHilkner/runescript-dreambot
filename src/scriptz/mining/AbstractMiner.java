package scriptz.mining;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.enums.RockTypes;
import shared.services.BankService;
import shared.services.InteractService;
import shared.services.MiningService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public abstract class AbstractMiner extends RunescriptAbstractContext {

    enum State { GO_MINE, KEEP_MINING, BANK, STOP }

    private RockTypes rock;
    private Areas mineArea;
    private Areas bankArea;

    private MiningService miningService;
    private InteractService interactService;
    private BankService bankService;

    public void onStart(RockTypes rock, Areas mineArea, Areas bankArea) {
        super.onStart();
        logScript("Starting AbstractMiner for mining " + rock.rockName + " at " + mineArea.name());
        this.rock = rock;
        this.mineArea = mineArea;
        this.bankArea = bankArea;
        miningService = MiningService.getInstance();
        interactService = InteractService.getInstance();
        bankService = BankService.getInstance();
        antibanService.setSkillsToHover(Skill.MINING);
    }

    private State getState() {

        // if script running for more than 8 hours, stop
        if (new Date().after(Util.dateAddSeconds(this.getStartDate(), 60*60*60*8))) {
            return State.STOP;
        }

        boolean hasPickaxeEquiped = getEquipment().contains(i -> i.getName().endsWith("pickaxe"));

        if (getInventory().isFull() || !hasPickaxeEquiped) {
            return State.BANK;
        }

        // by here we know for sure that pickaxe is equiped and player's inventory is not full

        if (getLocalPlayer().isAnimating()) {
            return State.KEEP_MINING;
        }

        return State.GO_MINE;

    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {

            case GO_MINE:
                if (sharedService.walkTo(mineArea)) {
                    if (miningService.mineRockWithDistanceLimit(rock, 5)) {
                        sleep(Calculations.random(1000, 2000));
                        sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                    }
                }
                break;

            case KEEP_MINING:
                sleep(300);
                break;

            case BANK:

                Item playersPickaxe = getEquipment().get(i -> i != null && i.getName() != null && i.getName().endsWith("pickaxe"));

                if (!sharedService.walkTo(bankArea)) {
                    break;
                }

                bankService.bankAll(false);

                // Getting best pickaxe possible in bank
                int miningLvl = getSkills().getRealLevel(Skill.MINING);
                String playersBestPickaxe = null;

                if (miningLvl >= 51 && (getBank().contains(Items.DragonPickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.DragonPickaxe.name)))) {
                    playersBestPickaxe = Items.DragonPickaxe.name;
                } else if (miningLvl >= 41 && (getBank().contains(Items.RunePickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.RunePickaxe.name)))) {
                    playersBestPickaxe = Items.RunePickaxe.name;
                } else if (miningLvl >= 31 && (getBank().contains(Items.AdamantPickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.AdamantPickaxe.name)))) {
                    playersBestPickaxe = Items.AdamantPickaxe.name;
                } else if (miningLvl >= 21 && (getBank().contains(Items.MithrilPickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.MithrilPickaxe.name)))) {
                    playersBestPickaxe = Items.MithrilPickaxe.name;
                } else if (miningLvl >= 11 && (getBank().contains(Items.BlackPickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.BlackPickaxe.name)))) {
                    playersBestPickaxe = Items.BlackPickaxe.name;
                } else if (miningLvl >= 6 && (getBank().contains(Items.SteelPickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.SteelPickaxe.name)))) {
                    playersBestPickaxe = Items.SteelPickaxe.name;
                } else if (miningLvl >= 1 && (getBank().contains(Items.IronPickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.IronPickaxe.name)))) {
                    playersBestPickaxe = Items.IronPickaxe.name;
                } else if (getBank().contains(Items.BronzePickaxe.name) || (playersPickaxe != null && Objects.equals(playersPickaxe.getName(), Items.BronzePickaxe.name))) {
                    playersBestPickaxe = Items.BronzePickaxe.name;
                }

                logScript("Player's best pickaxe [" + playersBestPickaxe + "]");

                if (playersPickaxe == null && playersBestPickaxe == null) {
                    logScript("Player don't have any pickaxes, stopping script");
                    stop();
                }

                if (playersPickaxe == null || !Objects.equals(playersPickaxe.getName(), playersBestPickaxe)) {
                    logScript("Going to equip pickaxe [" + playersBestPickaxe + "]");
                    bankService.withdraw(playersBestPickaxe, 1, true, false);
                    if (getTabs().open(Tab.INVENTORY)) {
                        interactService.interactInventoryItem(playersBestPickaxe, "Wield");
                    }
                    bankService.bankAll(true);
                }

                break;

            case STOP:
                sharedService.logout();
                stop();
                break;
        }

        return 0;
    }

}
