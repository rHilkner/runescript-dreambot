package scriptz.herblore;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

@ScriptManifest(author = "xpt", name = "Unfinished Potions", version = 1.0, description = "Unfinished Potions", category = Category.HERBLORE)
public class UnfinishedPotions extends RunescriptAbstractContext {

    enum State {DO_POTIONS, BANK }

    private final String[] HERBS = {
            Items.Tarromin.name, Items.Harralander.name, Items.Toadflax.name,
            Items.IritLeaf.name, Items.Avantoe.name, Items.Kwuarm.name,
            Items.Snapdragon.name, Items.Cadantine.name, Items.Ranarr.name,
            Items.Lantadyme.name
    };
    private final String VIAL_OF_WATER = Items.VialOfWater.name;

    private int inventoriesDone = 0;

    private BankService bankService;
    private InteractService interactService;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();

        logScript("Starting unfinished potions script!");
    }

    public State getState() {
        if (getInventory().contains(VIAL_OF_WATER) && getInventory().contains(HERBS)) {
            return State.DO_POTIONS;
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

            case DO_POTIONS:
                bankService.closeBank(false);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }

                for (String herb : HERBS) {
                    if (getInventory().contains(herb)) {
                        interactService.interactInventoryItems(VIAL_OF_WATER, herb, false, false);
                        antibanService.antibanSleep(AntibanActionType.FastPace);
                        getKeyboard().type(" ");
                        Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                        int counter = 0;
                        while (getInventory().contains(VIAL_OF_WATER) && getInventory().contains(herb) && counter < 8) {
                            counter++;
                            antibanService.antibanSleep(AntibanActionType.SlowPace);
                            if (getLocalPlayer().isAnimating()) {
                                logScript("Still doing potions");
                                counter = 0;
                            }
                        }
                    }
                }
                inventoriesDone++;

                break;
            case BANK:
                bankService.bankAll(false, true);
                if (!getBank().contains(VIAL_OF_WATER) || !getBank().contains(HERBS)) {
                    logScript("No potions to do. Finishing execution.");
                    stop();
                    break;
                }

                bankService.withdraw(VIAL_OF_WATER, 14, false, false, true);
                for (String herb : HERBS) {
                    if (!getInventory().isFull() && getBank().contains(herb)) {
                        bankService.withdraw(herb, 14, false, false, true);
                        break;
                    }
                }
                bankService.closeBank(true);

                Util.sleepUntil(() -> getInventory().contains(VIAL_OF_WATER) && getInventory().contains(HERBS), Constants.MAX_SLEEP_UNTIL);

                break;

        }

        return 0;
    }

}
