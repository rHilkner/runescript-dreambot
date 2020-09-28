package scriptz.magic;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import scriptz.RunescriptAbstractContext;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

import java.util.Objects;

@ScriptManifest(author = "Xpt", name = "Enchanting Jewellery", version = 1.0, description = "Enchanting Jewellery", category = Category.MAGIC)
public class EnchantJewellery extends RunescriptAbstractContext {

    enum State { ENCHANT, BANK }
    enum Enchants {
        LVL2(218, 21);
        private final int[] widgetChildInts;
        Enchants(int... ints) { this.widgetChildInts = ints; }
        WidgetChild getWidgetChild() {
            return ctx.getWidgets().getWidgetChild(widgetChildInts);
        }
    }

    private BankService bankService;
    private InteractService interactService;

    private final String JEWELLERY = Items.EmeraldRing.name;
    private final Enchants ENCHANTMENT = Enchants.LVL2;
    private final String COSMIC_RUNE = Items.CosmicRune.name;
    private final String STAFF = Items.StaffOfAir.name;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.MAGIC);

        logScript("Starting enchanting jewellery script!");
    }

    public State getState() {
        boolean hasStaffEquipped = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), STAFF)) != null;
        if (getInventory().contains(COSMIC_RUNE) && getInventory().contains(JEWELLERY) && hasStaffEquipped) {
            return State.ENCHANT;
        }
        return State.BANK;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {

            case ENCHANT:
                // basic closing shit and stuff
                bankService.closeBank();
                if (!getTabs().isOpen(Tab.MAGIC)) {
                    getTabs().open(Tab.MAGIC);
                }

                while (getInventory().contains(JEWELLERY) && getInventory().contains(COSMIC_RUNE)) {
                    logScript("Enchanting jewellery");

                    int tick = 600;
                    ENCHANTMENT.getWidgetChild().interact();
                    antibanService.antibanSleep(AntibanActionType.Latency);
//                    interactService.interactWithWidget(ENCHANTMENT.getWidgetChild());
//                    Util.sleep(Util.getGaussianBetween(tick * 0.37, tick * 0.55));
                    Item item = ctx.getInventory().get(i -> i != null && Objects.equals(i.getName(), JEWELLERY));
                    item.interact();
                    antibanService.antibanSleep(AntibanActionType.Latency);
//                    interactService.interactInventoryItem(JEWELLERY);

                    Util.sleepUntil(() -> getTabs().isOpen(Tab.MAGIC), Util.getGaussianBetween(1000, 2000));
                    if (!getTabs().isOpen(Tab.MAGIC)) {
                        getTabs().openWithMouse(Tab.MAGIC);
                    }
                    antibanService.antibanSleep(AntibanActionType.Latency);
                }

                // making sure to unselect spell
                if (Objects.equals(ENCHANTMENT.getWidgetChild().getSelectedAction(), "Cast")) {
                    getTabs().openWithMouse(Tab.INVENTORY);
                    antibanService.antibanSleep(AntibanActionType.Latency);
                }

                break;
            case BANK:
                bankService.bankAllExcept(false, COSMIC_RUNE);

                boolean hasStaffEquiped = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), STAFF)) != null;
                if (!getBank().contains(JEWELLERY) || (!getBank().contains(COSMIC_RUNE) && !getInventory().contains(COSMIC_RUNE)) || (!getBank().contains(STAFF) && !hasStaffEquiped)) {
                    logScript("No more jewellery to enchant. Finishing execution.");
                    stop();
                    break;
                }

                Item playersStaff = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), STAFF));
                if (playersStaff == null) {
                    bankService.withdraw(STAFF, 1, true, false);
                    interactService.interactInventoryItem(STAFF, false);
                }

                bankService.openBank();
                if (getBank().contains(COSMIC_RUNE)) {
                    bankService.withdraw(COSMIC_RUNE, null, false, false);
                }
                bankService.withdraw(JEWELLERY, null, true, false);

                break;

        }

        return 0;
    }
    
}
