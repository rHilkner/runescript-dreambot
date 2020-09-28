package scriptz.thieving;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

@ScriptManifest(author = "xpt", name = "Pickpocket NPC", version = 1.0, description = "Pickpocket NPC", category = Category.THIEVING)
public class PickpocketNPCs extends RunescriptAbstractContext {

    enum State { WALK_TO_NPC_AREA, PICKPOCKET, WALK_TO_BANK, BANK }

    private BankService bankService;
    private InteractService interactService;

    private final String NPC_NAME = "Man";
    private final Areas NPC_AREA = Areas.EdgevilleHayStacks;
    private final Areas BANK_AREA = Areas.EdgevilleBankNorth;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.FLETCHING);

        logScript("Starting pickpocketing script!");
    }

    public State getState() {
        if (getInventory().isFull()) {
            if (BANK_AREA.getArea().contains(getLocalPlayer())) {
                return State.BANK;
            } else {
                return State.WALK_TO_BANK;
            }
        }

        if (!NPC_AREA.getArea().contains(getLocalPlayer())) {
            return State.WALK_TO_NPC_AREA;
        }

        return State.PICKPOCKET;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {

            case WALK_TO_NPC_AREA:
                sharedService.walkTo(NPC_AREA);
                break;

            case PICKPOCKET:

                bankService.closeBank();
                if (!getTabs().isOpen(Tab.INVENTORY)) {
                    getTabs().open(Tab.INVENTORY);
                }

                if (getInventory().count(Items.CoinPouch.name) >= 25) {
                    interactService.interactInventoryItem(Items.CoinPouch.name, false);
                }

                interactService.interactClosestNpc(NPC_NAME, "Pickpocket");
                antibanService.antibanSleep(AntibanActionType.Latency);

                break;

            case WALK_TO_BANK:
                sharedService.walkTo(BANK_AREA);
                break;

            case BANK:
                bankService.bankAll(true);
                break;

        }

        return 0;
    }

}
