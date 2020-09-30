package scriptz.smithing;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

@ScriptManifest(author = "Xpt", name = "Smelting Bars", version = 1.0, description = "Smelting Bars", category = Category.SMITHING)
public class SmeltingBars extends RunescriptAbstractContext {

    enum State { SMELT_BARS, BANK }

    private BankService bankService;
    private InteractService interactService;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.SMITHING);

        logScript("Starting smelting bars script!");
    }

    private State getState() {

        if (getInventory().contains(Items.TinOre.name) && getInventory().contains(Items.CopperOre.name)) {
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
                    while (getInventory().contains(Items.TinOre.name) && getInventory().contains(Items.CopperOre.name) && counter < 8) {
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

                bankService.bankAll(false, false);

                if (!getBank().contains(Items.TinOre.name) || !getBank().contains(Items.CopperOre.name)) {
                    stop();
                    break;
                }

                bankService.withdraw(Items.TinOre.name, 14, false, false, false);
                bankService.withdraw(Items.CopperOre.name, 14, true, false, false);

                break;

        }

        return 0;
    }

}
