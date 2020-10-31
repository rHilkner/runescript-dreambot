package scriptz.fletching;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
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

@ScriptManifest(author = "xpt", name = "Stringing Bows", version = 1.0, description = "Strings bows", category = Category.FLETCHING)
public class StringingBows extends RunescriptAbstractContext {

    enum State { STRING_BOWS, BANK }

    private int inventoriesDone = 0;
    private BankService bankService;
    private InteractService interactService;

    private final String BOW_NAME = Items.MagicLongbowU.name;
    private final String BOW_STRING = Items.BowString.name;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.FLETCHING);

        logScript("Starting stringing bows script!");
    }

    public State getState() {
        if (getInventory().contains(BOW_NAME) && !getInventory().get(BOW_NAME).isNoted()
                && getInventory().contains(BOW_STRING) && !getInventory().get(BOW_STRING).isNoted()) {
            return State.STRING_BOWS;
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

            case STRING_BOWS:
                // Sanity check stuff here
                bankService.closeBank(true);
                sharedService.openInventory();
                sharedService.deselectAnyItem();

                // Stringing bows
                interactService.interactInventoryItems(BOW_NAME, BOW_STRING, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (getInventory().contains(BOW_NAME) && getInventory().contains(BOW_STRING) && counter < 5) {
                    counter++;
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                    if (getLocalPlayer().isAnimating()) {
                        logScript("Still stringing bows");
                        counter = 0;
                    }
                }

                inventoriesDone++;

                break;
            case BANK:
                // making sure to unselect item
                sharedService.deselectAnyItem();

                bankService.bankAll(false, true);
                if (!getBank().contains(BOW_NAME) || !getBank().contains(BOW_STRING)) {
                    logScript("No more bows to string. Finishing execution.");
                    stop();
                    break;
                }

                bankService.withdraw(BOW_NAME, 14, false, false, true);
                bankService.withdraw(BOW_STRING, 14, true, false, true);

                Util.sleepUntil(() -> getInventory().contains(BOW_NAME) && getInventory().contains(BOW_STRING), Constants.MAX_SLEEP_UNTIL);
                break;

        }

        return 0;
    }

}
