package scriptz.miscelaneous;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

@ScriptManifest(author = "xpt", name = "Sulphurous Fertilizer", version = 1.0, description = "Sulphurous Fertilizer", category = Category.MISC)
public class SulphurousFertilizer extends RunescriptAbstractContext {

    enum State {MAKE_FERTILIZER, BANK }

    private int inventoriesDone = 0;
    private BankService bankService;
    private InteractService interactService;

    private final String COMPOST = Items.Compost.name;
    private final String SALTPETRE = Items.Saltpetre.name;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.FARMING);

        logScript("Starting sulphurous fertilizer script!");
    }

    public State getState() {
        if (getInventory().contains(COMPOST) && !getInventory().get(COMPOST).isNoted()
                && getInventory().contains(SALTPETRE) && !getInventory().get(SALTPETRE).isNoted()) {
            return State.MAKE_FERTILIZER;
        }
        return State.BANK;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());
        logScript("-- Inventories done so far: " + inventoriesDone);

        switch (currentState) {

            case MAKE_FERTILIZER:
                bankService.closeBank(false);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }
                interactService.interactInventoryItems(COMPOST, SALTPETRE, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
//                getKeyboard().type(" ");
                Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (getInventory().contains(COMPOST) && getInventory().contains(SALTPETRE) && counter < 20) {
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                    logScript("Still making fertilizers");
                    counter++;
                }

                inventoriesDone++;

                break;
            case BANK:

                if (getInventory().isItemSelected()) {
                    getInventory().deselect();
                }

                bankService.bankAll(false, false);
                if (!getBank().contains(COMPOST) || !getBank().contains(SALTPETRE)) {
                    logScript("No more fertilizers to make. Finishing execution.");
                    stop();
                } else {
                    bankService.withdraw(COMPOST, 14, false, false, false);
                    bankService.withdraw(SALTPETRE, 14, true, false, false);
                }
                break;

        }

        return 0;
    }

}
