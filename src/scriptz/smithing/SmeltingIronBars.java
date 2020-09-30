package scriptz.smithing;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

import java.util.Objects;

@ScriptManifest(author = "Xpt", name = "Smelting Iron Bars", version = 1.0, description = "Smelting Iron Bars", category = Category.SMITHING)
public class SmeltingIronBars extends RunescriptAbstractContext {

    enum State { SMELT_BARS, BANK }

    private BankService bankService;
    private InteractService interactService;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.SMITHING);

        logScript("Starting smelting iron bars script!");
    }

    private State getState() {

        Item playersRingOfForging = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), Items.RingOfForging.name));
        if (getInventory().contains(Items.IronOre.name) && playersRingOfForging != null) {
            return State.SMELT_BARS;
        }

        return State.BANK;
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

            case SMELT_BARS:
                bankService.closeBank(false);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }

                if (sharedService.walkTo(Areas.EdgevilleFurnace)) {
                    logScript("Going to interact to furnace and start smelting bars");
                    interactService.interactWithGameObject("Furnace");
                    antibanService.antibanSleep(AntibanActionType.FastPace);
                    getKeyboard().type(" ");
                    antibanService.antibanSleep(AntibanActionType.SlowPace);

                    int counter = 0;
                    while (getInventory().contains(Items.IronOre.name) && counter < 8) {
                        counter++;
                        antibanService.antibanSleep(AntibanActionType.FastPace);
                        if (getLocalPlayer().isAnimating()) {
                            logScript("Still smelting bars");
                            counter = 0;
                        }
                    }
                } else {
                    logScript("Couldn't walk to furnace?");
                }

                break;

            case BANK:

                // if player in edgeville, go to edgeville north bank... else go to closest bank
                if (Areas.EdgevilleBankToFurnace.getArea().contains(getLocalPlayer())) {
                    logScript("Walking to edgeville bank");
                    sharedService.walkTo(Areas.EdgevilleBankNorth);
                } else {
                    logScript("Walking to closest bank");
                    sharedService.walkTo(getBank().getClosestBankLocation().getArea(10));
                }

                logScript("Banking all");
                bankService.bankAll(false, false);

                if (!getBank().contains(Items.IronOre.name) || !getBank().contains(Items.RingOfForging.name)) {
                    stop();
                    break;
                }

                Item playersRingOfForging = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), Items.RingOfForging.name));
                if (playersRingOfForging == null) {
                    bankService.withdraw(Items.RingOfForging.name, 1, true, false, false);
                    interactService.interactInventoryItem(Items.RingOfForging.name, false);
                }
                bankService.withdraw(Items.IronOre.name, null, true, false, false);

                break;

        }

        return 0;
    }

}
