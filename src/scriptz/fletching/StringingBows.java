package scriptz.fletching;

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

@ScriptManifest(author = "xpt", name = "Stringing Bows", version = 1.0, description = "Strings bows", category = Category.FLETCHING)
public class StringingBows extends RunescriptAbstractContext {

    enum State { STRING_BOWS, BANK }

    private BankService bankService;
    private InteractService interactService;

    private final String BOW_NAME = Items.YewLongbowU.name;
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
        if (getInventory().contains(BOW_NAME) && getInventory().contains(BOW_STRING)) {
            return State.STRING_BOWS;
        }
        return State.BANK;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {

            case STRING_BOWS:
                bankService.closeBank(false);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }
                interactService.interactInventoryItems(BOW_NAME, BOW_STRING, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (getInventory().count(BOW_NAME) > 0 && getInventory().count(BOW_STRING) > 0 && counter < 8) {
                    counter++;
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                    if (getLocalPlayer().isAnimating()) {
                        logScript("Still stringing bows");
                        counter = 0;
                    }
                }

                break;
            case BANK:
                bankService.bankAll(false, false);
                if (getBank().count(BOW_NAME) <= 0 || getBank().count(BOW_STRING) <= 0) {
                    logScript("No more bows to string. Finishing execution.");
                    stop();
                } else {
                    bankService.withdraw(BOW_NAME, 14, false, false, false);
                    bankService.withdraw(BOW_STRING, 14, true, false, false);
                }
                break;

        }

        return 0;
    }

}
