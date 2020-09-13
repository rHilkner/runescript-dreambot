package scriptz.fletching;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;

@ScriptManifest(author = "xpt", name = "Stringing Bows", version = 1.0, description = "Strings bows", category = Category.FLETCHING)
public class StringingBows extends RunescriptAbstractContext {

    enum State { BUY, STRING_BOWS, BANK }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();
        antibanService.setSkillsToHover(Skill.FLETCHING);

        logScript("Starting stringing bows script!");
    }

    public State getState() {
        if (getInventory().contains(Items.MapleLongbowU.name) && getInventory().contains(Items.BowString.name)) {
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

            case BUY:
                break;
            case STRING_BOWS:
                bankService.closeBank();
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }
                interactService.interactInventoryItems(Items.MapleLongbowU.name, Items.BowString.name, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (counter < 4) {
                    counter++;
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                    if (getLocalPlayer().isAnimating()) {
                        logScript("Still stringing bows");
                        counter = 0;
                    }
                }

                break;
            case BANK:
                bankService.bankAll(false);
                if (getBank().count(Items.MapleLongbowU.name) <= 0 || getBank().count(Items.BowString.name) <= 0) {
                    stop();
                } else {
                    bankService.withdraw(Items.MapleLongbowU.name, 14, false, false);
                    bankService.withdraw(Items.BowString.name, 14, true, false);
                }
                break;

        }

        return 0;
    }

}
