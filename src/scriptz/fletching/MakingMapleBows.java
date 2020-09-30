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
import shared.services.GrandExchangeService;
import shared.services.InteractService;

@ScriptManifest(author = "xpt", name = "Making Maple Bows", version = 1.0, description = "Makes arrow shafts", category = Category.FLETCHING)
public class MakingMapleBows extends RunescriptAbstractContext {

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
        if (getInventory().contains(Items.Knife.name) && getInventory().contains(Items.MapleLogs.name)) {
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
                bankService.closeBank(false);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }
                interactService.interactInventoryItems(Items.Knife.name, Items.MapleLogs.name, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (getInventory().contains(Items.MapleLogs.name) && counter < 5) {
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
                if (getBank().count(Items.MapleLogs.name) <= 0) {
                    stop();
                } else {
                    bankService.withdraw(Items.MapleLogs.name, null, true, false, false);
                }
                break;

        }

        return 0;
    }

}
