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

@ScriptManifest(author = "xpt", name = "Making Headless Arrows", version = 1.0, description = "Makes headless arrows", category = Category.FLETCHING)
public class MakingHeadlessArrows extends RunescriptAbstractContext {

    enum State { BUY, MAKE_HEADLESS_ARROWS, BANK, STOP }

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
        if (getInventory().contains(Items.ArrowShaft.name) && getInventory().contains(Items.Feather.name) && !getInventory().isFull()) {
            return State.MAKE_HEADLESS_ARROWS;
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
            case MAKE_HEADLESS_ARROWS:
                bankService.closeBank(false);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }
                interactService.interactInventoryItems(Items.ArrowShaft.name, Items.Feather.name, false, false);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                int counter = 0;
                while (getInventory().contains(Items.ArrowShaft.name) && getInventory().contains(Items.Feather.name) && counter < 8) {
                    counter++;
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                    if (getLocalPlayer().isAnimating()) {
                        logScript("Still making headless arrows");
                        counter = 0;
                    }
                }

                break;
            case BANK:
                bankService.bankAll(false, false);
                if (getBank().count(Items.ArrowShaft.name) <= 0 || getBank().count(Items.Feather.name) <= 0) {
                    stop();
                } else {
                    bankService.withdraw(Items.ArrowShaft.name, null, false, false, false);
                    bankService.withdraw(Items.Feather.name, null, true, false, false);
                }
                break;
            case STOP:
                stop();
                break;

        }

        return 0;
    }

}
