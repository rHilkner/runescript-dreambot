package scriptz.thieving;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

import java.util.Objects;

@ScriptManifest(author = "xpt", name = "Stealing Fruits", version = 1.0, description = "Stealing Fruits", category = Category.THIEVING)
public class StealFruits extends RunescriptAbstractContext {

    enum State { LURE_DOGS, STEAL, DEPOSIT }

    private BankService bankService;
    private InteractService interactService;

    private final String FRUIT_STALL = "Fruit Stall";
    private final Tile FRUIT_STALL_TILE = new Tile(1767, 3596, 0);
    private final Area LURE_AREA = new Area(1768, 3599, 1769, 3600, 0);

    private int inventoriesDone = 0;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.FLETCHING);

        logScript("Starting Stealing Fruits script!");
    }

    public State getState() {
        if (getInventory().isFull()) {
            return State.DEPOSIT;
        }

        if (getLocalPlayer().isInCombat()) {
            return State.LURE_DOGS;
        }

        return State.STEAL;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());
        logScript("-- Inventories done so far: " + inventoriesDone);

        switch (currentState) {
            case STEAL:
                if (!Objects.equals(FRUIT_STALL_TILE, getLocalPlayer().getTile())) {
                    sharedService.walkTo(FRUIT_STALL_TILE);
                }

                Util.sleepUntil(() -> getGameObjects().closest(FRUIT_STALL).hasAction("Steal-from"), Calculations.random(2000, 3500));

                if (!getLocalPlayer().isHealthBarVisible()) {
//                int filledInventorySlots = getInventory().count(i -> i != null);
                    interactService.interactWithGameObject(FRUIT_STALL);
                    Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(1000, 1800));
                    Util.sleepUntil(() -> !getLocalPlayer().isAnimating(), Calculations.random(1000, 1800));
                    // Sleeping until the number of items in the player's inventory changes (or breaking after 2.0-3.5 seconds)
//                Util.sleepUntil(() -> filledInventorySlots != getInventory().count(i -> i != null), Calculations.random(200, 300));
                }

                break;

            case DEPOSIT:
                inventoriesDone++;
                bankService.depositAll(false);
                break;

            case LURE_DOGS:
                sharedService.walkTo(LURE_AREA);
                sharedService.walkTo(FRUIT_STALL_TILE);
                break;

        }

        return 0;
    }

}
