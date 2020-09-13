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

@ScriptManifest(author = "xpt", name = "Making Arrow Shafts", version = 1.0, description = "Makes arrow shafts", category = Category.FLETCHING)
public class MakingBows extends RunescriptAbstractContext {

    enum State { BUY, MAKE_ARROW_SHAFTS, BANK }

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

        logScript("Starting headless arrows script!");
    }

    public State getState() {
        if (getInventory().contains(Items.Knife.name) && getInventory().contains(Items.WillowLogs.name)) {
            return State.MAKE_ARROW_SHAFTS;
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
            case MAKE_ARROW_SHAFTS:
                bankService.closeBank();
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }
                interactService.interactInventoryItems(Items.Knife.name, Items.WillowLogs.name, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (counter < 5) {
                    counter++;
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                    if (getLocalPlayer().isAnimating()) {
                        logScript("Still making headless arrows");
                        counter = 0;
                    }
                }

                break;
            case BANK:
                bankService.bankAllExcept(false, Items.Knife.name);
                if (getBank().count(Items.WillowLogs.name) <= 0) {
                    stop();
                } else {
                    bankService.withdraw(Items.WillowLogs.name, null, true, false);
                }
                break;

        }

        return 0;
    }

}
