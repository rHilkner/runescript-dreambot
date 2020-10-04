package scriptz.cooking;

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

@ScriptManifest(author = "xpt", name = "Pineapple Pizza", version = 1.0, description = "Pineapple Pizza", category = Category.COOKING)
public class PineapplePizza extends RunescriptAbstractContext {

    enum State {MAKE_PIZZAS, BANK }

    private int inventoriesDone = 0;
    private BankService bankService;
    private InteractService interactService;

    private final String PLAIN_PIZZA = Items.PlainPizza.name;
    private final String PINEAPPLE_RING = Items.PineappleRing.name;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.COOKING);

        logScript("Starting pineapple pizza script!");
    }

    public State getState() {
        if (getInventory().contains(PLAIN_PIZZA) && !getInventory().get(PLAIN_PIZZA).isNoted()
                && getInventory().contains(PINEAPPLE_RING) && !getInventory().get(PINEAPPLE_RING).isNoted()) {
            return State.MAKE_PIZZAS;
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

            case MAKE_PIZZAS:
                bankService.closeBank(false);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }
                interactService.interactInventoryItems(PLAIN_PIZZA, PINEAPPLE_RING, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (getInventory().contains(PLAIN_PIZZA) && getInventory().contains(PINEAPPLE_RING) && counter < 20) {
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                    logScript("Still making pizzas");
                    counter++;
                }

                inventoriesDone++;

                break;
            case BANK:

                if (getInventory().isItemSelected()) {
                    getInventory().deselect();
                }

                bankService.bankAll(false, false);
                if (!getBank().contains(PLAIN_PIZZA) || !getBank().contains(PINEAPPLE_RING)) {
                    logScript("No more pizzas to make. Finishing execution.");
                    stop();
                } else {
                    bankService.withdraw(PLAIN_PIZZA, 14, false, false, false);
                    bankService.withdraw(PINEAPPLE_RING, 14, true, false, false);
                }
                break;

        }

        return 0;
    }

}
