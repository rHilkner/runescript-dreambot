package scriptz.cooking;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.GameObjects;
import shared.enums.Items;
import shared.services.AntibanService;
import shared.services.BankService;
import shared.services.FiremakingService;
import shared.services.InteractService;

import java.util.Arrays;
import java.util.List;

@ScriptManifest(author = "Xpt", name = "Cooking fish at GE", version = 1.0, description = "Cooks shrimps, anchovies, trout, salmon at GE", category = Category.COOKING)
public class CookingFishGE extends RunescriptAbstractContext {

    enum State { BANK, LIGHT_FIRE, START_COOKING, KEEP_COOKING, STOP }

    private List<String> fishesToFry = Arrays.asList(Items.RawShrimps.name, Items.RawAnchovies.name, Items.RawTrout.name, Items.RawSalmon.name);
    private List<String> logsToBurn = Arrays.asList(Items.Logs.name, Items.OakLogs.name, Items.WillowLogs.name, Items.MapleLogs.name);

    private AntibanService antibanService;
    private BankService bankService;
    private FiremakingService firemakingService;
    private InteractService interactService;

    @Override
    public void onStart() {
        super.onStart();
        this.antibanService = AntibanService.getInstance();
        this.bankService = BankService.getInstance();
        this.firemakingService = FiremakingService.getInstance();
        this.interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.COOKING);
        logScript("Fish cook starting - creditz to XpT ø*ø");
    }

    private State getState() {

        boolean inventoryContainsFish = getInventory().contains(i -> i != null && fishesToFry.contains(i.getName()));
        boolean inventoryContainsTinderbox = getInventory().contains(Items.Tinderbox.name);
        boolean inventoryContainsLogs = getInventory().contains(i -> i != null && logsToBurn.contains(i.getName()));;

        GameObject fire = getGameObjects().closest(GameObjects.Fire.name);

        if (inventoryContainsFish && ((fire != null && fire.distance() <= 2.01) || (inventoryContainsTinderbox && inventoryContainsLogs))) {

            if (getLocalPlayer().isAnimating()) {
                return State.KEEP_COOKING;
            } else if (fire != null && fire.distance() <= 2.01) {
                return State.START_COOKING;
            } else if (inventoryContainsTinderbox && inventoryContainsLogs) {
                return State.LIGHT_FIRE;
            }
        }

        return State.BANK;
    }

    @Override
    public int onLoop() {

        super.onLoop();

        if (!getTabs().isOpen(Tab.INVENTORY)) {
            getTabs().open(Tab.INVENTORY);
        }

        if (getInventory().isItemSelected()) {
            getInventory().deselect();
        }

        State currentState = getState();

        logScript("-- Current state: " + currentState.name());
        printPlayerTotals();

        switch (currentState) {
            case BANK:
                bankService.bankAllExcept(false, Items.Tinderbox.name);

                if (!getInventory().contains(Items.Tinderbox.name)) {
                    bankService.withdraw(Items.Tinderbox.name, 1, false, false);
                }

                bankService.withdraw(Items.WillowLogs.name, 1, false, false);

                for (int i = 0; i < fishesToFry.size(); i++) {
                    String fishName = fishesToFry.get(i);
                    if (!getInventory().isFull() && getBank().count(fishName) > 0) {
                        bankService.withdraw(fishName, null, false, false);
                    }
                }

                if (!getInventory().isFull()) {
                    stop();
                }

                bankService.closeBank();
                break;
            case LIGHT_FIRE:
                if (sharedService.walkTo(Areas.GrandExchangeCloseToEastBank)) {
                    GameObject fire = getGameObjects().closest(GameObjects.Fire.name);
                    boolean noFireCloseEnough = fire == null || fire.distance() > 2.01;
                    if (noFireCloseEnough) {
                        Item logs = getInventory().get(i -> i != null && logsToBurn.contains(i.getName()));
                        firemakingService.lightFire(logs.getName(), true);
                    }
                }
                break;
            case START_COOKING:
                Item fish = getInventory().get(i -> i != null && fishesToFry.contains(i.getName()));
                interactService.interactGameObjectWithInventoryItem(fish.getName(), GameObjects.Fire.name, true);
                sleepUntil(() -> !getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving() && getLocalPlayer().isStandingStill(), 3000);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                getKeyboard().type(" ");
                sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));
                antibanService.antibanSleep(AntibanActionType.FastPace);
                break;
            case KEEP_COOKING:
                int counter = 0;
                boolean inventoryContainsFish = getInventory().contains(i -> i != null && fishesToFry.contains(i.getName()));
                while (inventoryContainsFish && counter < 5) {
                    if (getLocalPlayer().isAnimating()) {
                        counter = 0;
                        logScript("Still cooking...");
                    } else {
                        counter++;
                    }
                    antibanService.antibanSleep(AntibanActionType.FastPace);
                    inventoryContainsFish = getInventory().contains(i -> i != null && fishesToFry.contains(i.getName()));
                }
                break;
            case STOP:
                stop();
                break;
        }

        return 0;
    }

    private void printPlayerTotals() {
        // todo: print total fishes cooked, burnt and xp gained
    }
}
