package scriptz.magic;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

import java.util.Objects;

@ScriptManifest(author = "Xpt", name = "High Alchemy", version = 1.0, description = "High Alchemy", category = Category.MAGIC)
public class HighAlchemy extends RunescriptAbstractContext {

    enum State {HIGH_ALCH, BANK }

    private BankService bankService;
    private InteractService interactService;

    private final String ITEM = Items.MagicLongbow.name;
    private final int[] HIGH_ALCH_WIDGET_INTS = {218, 39};
    private final String NATURE_RUNE = Items.NatureRune.name;
    private final String STAFF = Items.StaffOfFire.name;

    private WidgetChild getHighAlchWidgetChild() {
        return getWidgets().getWidgetChild(HIGH_ALCH_WIDGET_INTS);
    }

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.MAGIC);

        logScript("Starting high alchemy script!");
    }

    public State getState() {
        boolean hasStaffEquipped = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), STAFF)) != null;
        if (getInventory().contains(NATURE_RUNE) && getInventory().contains(ITEM) && hasStaffEquipped) {
            return State.HIGH_ALCH;
        }
        return State.BANK;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {

            case HIGH_ALCH:
                // basic closing shit and stuff
                bankService.closeBank(false);
                sharedService.deselectAnyItem();
                if (!getTabs().isOpen(Tab.MAGIC)) {
                    getTabs().openWithMouse(Tab.MAGIC);
                }

//                while (getInventory().contains(ITEM) && getInventory().contains(NATURE_RUNE)) {
                    logScript("High alching");

                    getHighAlchWidgetChild().interact();
                    antibanService.antibanSleep(AntibanActionType.FastPace);
                    Item item = ctx.getInventory().get(i -> i != null && Objects.equals(i.getName(), ITEM));
                    item.interact();
                    antibanService.antibanSleep(AntibanActionType.FastPace);

                    Util.sleepUntil(() -> getTabs().isOpen(Tab.MAGIC), Constants.MAX_SLEEP_UNTIL);
//                    if (!getTabs().isOpen(Tab.MAGIC)) {
//                        getTabs().openWithMouse(Tab.MAGIC);
//                    }
                    antibanService.antibanSleep(AntibanActionType.Latency);
//                }

                break;
            case BANK:

                // making sure to unselect spell
                if (Objects.equals(getHighAlchWidgetChild().getSelectedAction(), "Cast")) {
                    getTabs().openWithMouse(Tab.INVENTORY);
                    antibanService.antibanSleep(AntibanActionType.Latency);
                }
                // making sure to unselect item
                sharedService.deselectAnyItem();

                bankService.bankAllExcept(false, NATURE_RUNE);

                boolean hasStaffEquiped = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), STAFF)) != null;
                if (!getBank().contains(ITEM) || (!getBank().contains(NATURE_RUNE) && !getInventory().contains(NATURE_RUNE)) || (!getBank().contains(STAFF) && !hasStaffEquiped)) {
                    logScript("No more items to alch. Finishing execution.");
                    stop();
                    break;
                }

                Item playersStaff = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), STAFF));
                if (playersStaff == null) {
                    bankService.withdraw(STAFF, 1, true, false, false);
                    interactService.interactInventoryItem(STAFF, false);
                }

                bankService.openBank(false);
                if (getBank().contains(NATURE_RUNE)) {
                    bankService.withdraw(NATURE_RUNE, null, false, false, false);
                }
                bankService.withdraw(ITEM, null, true, true, false);

                break;

        }

        return 0;
    }
    
}
