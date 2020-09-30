package scriptz.herblore;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

@ScriptManifest(author = "Xpt", name = "Cleaning Herbs", version = 1.0, description = "Cleaning Herbs", category = Category.HERBLORE)
public class CleaningHerbs extends RunescriptAbstractContext {

    enum State { CLEAN_HERBS, BANK }

    private BankService bankService;
    private InteractService interactService;
    private int inventoriesDone = 0;

    private final String[] HERBS = {
            Items.GrimyTarromin.name, Items.GrimyHarralander.name, Items.GrimyToadflax.name,
            Items.GrimyIritLeaf.name, Items.GrimyAvantoe.name, Items.GrimyKwuarm.name
    };

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.HERBLORE);

        logScript("Starting cleaning herbs script!");
    }

    public State getState() {
        if (getInventory().contains(HERBS) && !getInventory().get(HERBS).isNoted()) {
            return State.CLEAN_HERBS;
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

            case CLEAN_HERBS:
                // basic closing shit and stuff
                bankService.closeBank(true);
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().openWithMouse(Tab.INVENTORY);
                }

                inventoriesDone++;

                logScript("Cleaning herbs");
                for (String herb : HERBS) {
                    if (getInventory().contains(herb)) {
                        interactService.interactFullInventory(herb);
                        Util.sleepUntil(() -> !getInventory().contains(herb), Constants.MAX_SLEEP_UNTIL);
                    }
                }

                break;
            case BANK:
                // making sure to unselect any item
                if (getInventory().isItemSelected()) {
                    getInventory().deselect();
                }

                bankService.bankAll(false, true);
                Util.sleepUntil(() -> getInventory().isEmpty(), Constants.MAX_SLEEP_UNTIL);
                if (!getBank().contains(HERBS)) {
                    stop();
                    break;
                }

                for (String herb : HERBS) {
                    if (!getInventory().isFull() && getBank().contains(herb)) {
                        bankService.withdraw(herb, null, false, false, true);
                    }
                }
                bankService.closeBank(true);
                Util.sleepUntil(() -> !getInventory().isEmpty(), Constants.MAX_SLEEP_UNTIL);

                break;

        }

        return 0;
    }

}
