package scriptz.cooking;

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

    enum State { BANK, LIGHT_FIRE, START_COOKING, KEEP_COOKING,  }

    private List<String> fishesToFry = Arrays.asList(Items.RawShrimps.name);
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
        return null;
    }

    @Override
    public int onLoop() {

        super.onLoop();

        if (!getTabs().isOpen(Tab.INVENTORY)) {
            getTabs().open(Tab.INVENTORY);
        }

        boolean inventoryContainsFish = getInventory().contains(i -> i != null && fishesToFry.contains(i.getName()));
        boolean inventoryContainsTinderbox = getInventory().contains(Items.Tinderbox.name);
        boolean inventoryContainsLogs = getInventory().contains(i -> i != null && logsToBurn.contains(i.getName()));;

        GameObject fire = getGameObjects().closest(GameObjects.Fire.name);

        if (inventoryContainsFish && ((fire != null && fire.distance() <= 1.5) || (inventoryContainsTinderbox && inventoryContainsLogs))) {

            if (getLocalPlayer().isAnimating()) {
                int counter = 0;
                while (inventoryContainsFish && counter < 5) {
                    if (getLocalPlayer().isAnimating()) {
                        counter = 0;
                    } else {
                        counter++;
                    }
                    antibanService.antibanSleep(AntibanActionType.FastPace);
                    inventoryContainsFish = getInventory().contains(i -> i != null && fishesToFry.contains(i.getName()));
                }
            } else if (fire != null && fire.distance() <= 1.5) {
                Item fish = getInventory().get(i -> i != null && fishesToFry.contains(i.getName()));
                interactService.interactGameObjectWithInventoryItem(fish.getName(), GameObjects.Fire.name, true);
                antibanService.antibanSleep(AntibanActionType.Latency);
                getKeyboard().type(" ");
                sleepUntil(() -> !getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.SlowPace);
            } else if (inventoryContainsTinderbox && inventoryContainsLogs) {
                if (sharedService.walkTo(Areas.GrandExchangeCloseToEastBank)) {
                    Item logs = getInventory().get(i -> i != null && logsToBurn.contains(i.getName()));
                    firemakingService.lightFire(logs.getName(), true);
                }
            }
        } else {
            bankService.bankAllExcept(false, Items.Tinderbox.name);

            if (!getInventory().contains(Items.Tinderbox.name)) {
                bankService.withdraw(Items.Tinderbox.name, 1, false, false);
            }

            bankService.withdraw(Items.WillowLogs.name, 1, false, false);

            for (String fishName : fishesToFry) {
                if (!getInventory().isFull()) {
                    bankService.withdraw(fishName, null, false, false);
                }
            }

            bankService.closeBank();
        }

        return 0;
    }
}
