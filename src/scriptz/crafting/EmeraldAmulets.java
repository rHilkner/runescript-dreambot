package scriptz.crafting;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;

import java.util.Date;

@ScriptManifest(author = "Xpt", name = "Emerald amulets", version = 1.0, description = "Makes emerald amulets", category = Category.CRAFTING)
public class EmeraldAmulets extends RunescriptAbstractContext {

    enum State { SELL, BUY, MAKE_AMULETS, BANK, STOP }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    private boolean rebuy = true;

    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalGoldBars = -1;
    private int totalEmeralds = -1;
    private int totalEmeraldAmulets = -1;

    private int lastGoldBarsPrice = -1;
    private int lastEmeraldPrice = -1;
    private int lastEmeraldAmuletPrice = -1;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();
        antibanService.setSkillsToHover(Skill.CRAFTING);

        logScript("Starting emerald amulets script!");
    }

    @Override
    public void onExit() {
        log("Ending emerald amulets script!");
    }

    private State getState() {

        if (getInventory().contains(Items.AmuletMould.name) && getInventory().contains(Items.GoldBar.name) && getInventory().contains(Items.Emerald.name)) {
            if (!getInventory().get(Items.AmuletMould.name).isNoted() && !getInventory().get(Items.GoldBar.name).isNoted() && !getInventory().get(Items.Emerald.name).isNoted()) {
                logScript("-- Current state: [" + State.MAKE_AMULETS + "] inventory contains gold bar");
                return State.MAKE_AMULETS;
            }
        }

        // Initialize all statuses
        if (initialMoney == -1 || totalGoldBars == -1 || totalEmeralds == -1 || totalEmeraldAmulets == -1 || totalCoins == -1) {
            logScript("-- Current state: [" + State.BANK + "] initialize variable counts");
            return State.BANK;
        }

        if (totalGoldBars == 0 || totalEmeralds == 0) {
            if (totalEmeraldAmulets > 0) {
                logScript("-- Current state: [" + State.SELL + "] going to sell");
                return State.SELL;
            }
            if (rebuy) {
                logScript("-- Current state: [" + State.BUY + "] going to buy");
                return State.BUY;
            } else {
                logScript("-- Current state: [" + State.STOP + "] stopping");
                return State.STOP;
            }
        }

        logScript("-- Current state: [" + State.BANK + "] nothing interesting happens");
        return State.BANK;
    }

    public void printPlayerTotals() {

        if (lastGoldBarsPrice != -1 && lastEmeraldPrice != -1 && lastEmeraldAmuletPrice == -1 && totalCoins != -1) {
            int goldBarMoney = totalGoldBars * lastGoldBarsPrice;
            int emeraldMoney = totalEmeralds * lastEmeraldPrice;
            int emeraldAmuletMoney = totalEmeraldAmulets * lastEmeraldAmuletPrice;

            int playerTotalMoney = totalCoins + goldBarMoney + emeraldMoney + emeraldAmuletMoney;

            if (initialMoney == -1) {
                initialMoney = playerTotalMoney;
            }

            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total equivalent of" +
                    "\n\t- gp = " + playerTotalMoney/1000 + "k" +
                    "\n\t- gp/h = " + moneyPerHour/1000 + "k/h" +
                    "\n\t- gold bars = " + totalGoldBars + " [ " + goldBarMoney/1000 + "k]" +
                    "\n\t- emerald = " + totalEmeralds + " [ " + emeraldMoney/1000 + "k]" +
                    "\n\t- emerald amulet = " + totalEmeraldAmulets + " [ " + emeraldAmuletMoney/1000 + "k]");
        }
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

            case SELL:
                bankService.withdraw(Items.EmeraldAmulet.name, null, false, true);
                bankService.withdraw(Items.Coins.name, null, true, false);

                if (grandExchangeService.addSellExchange(Items.EmeraldAmulet.name)) {
                    lastEmeraldAmuletPrice = getGrandExchange().getCurrentPrice();
                    if (grandExchangeService.setPriceQuantityConfirm(Items.PizzaBase.name, (int) (lastEmeraldAmuletPrice * 0.79), null, false)) {
                        grandExchangeService.collect(false);
                        totalEmeraldAmulets = 0;
                        totalCoins = getInventory().count(Items.Coins.name);
                    }
                }

                break;

            case BUY:
                if (sharedService.walkTo(Areas.GrandExchange)) {
                    if (getInventory().count("Coins") < 100) {
                        bankService.withdraw(Items.Coins.name, null, true, false);
                        bankService.closeBank(); // just making sure bank is closed
                    }

                    if (grandExchangeService.addBuyExchange("gold b", Items.GoldBar.name, false, false)) {
                        lastGoldBarsPrice = getGrandExchange().getCurrentPrice();
                        logScript("lastGoldBarsPrice = " + lastGoldBarsPrice);
                        grandExchangeService.setPriceQuantityConfirm(Items.GoldBar.name, (int) (lastGoldBarsPrice * 1.21), 500, false);
                    }

                    if (grandExchangeService.addBuyExchange("emerald", Items.Emerald.name, false, false)) {
                        lastEmeraldPrice = getGrandExchange().getCurrentPrice();
                        logScript("lastEmeraldPrice = " + lastEmeraldPrice);
                        grandExchangeService.setPriceQuantityConfirm(Items.Emerald.name, (int) (lastEmeraldPrice * 1.21), 500, false);
                    }

                    grandExchangeService.collect(true);

                    totalCoins = getInventory().count(Items.Coins.name);
                    totalGoldBars = getInventory().count(Items.GoldBar.name);
                    totalEmeralds = getInventory().count(Items.Emerald.name);
                }
                break;

            case MAKE_AMULETS:
                bankService.closeBank(); // just making sure bank is closed
                if (getLocalPlayer().isAnimating() ||
                        !sharedService.walkTo(Areas.EdgevilleBankToFurnace) ||
                        !sharedService.walkTo(Areas.EdgevilleFurnace) ||
                        !interactService.interactWithGameObject("Furnace")) {
                    break;
                }

                sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);

                WidgetChild widgetChild;
                int counter = 0;
                do {
                    widgetChild = getWidgets().getWidget(446) == null ? null : getWidgets().getWidget(446).getChild(36);
                    sleep(200);
                    counter++;
                } while (widgetChild == null && counter < 20);

                if (widgetChild == null) {
                    logScript("widget not found");
                    break;
                }

                logScript("Interacting with " + widgetChild.getText());
                widgetChild.interact();
                antibanService.antibanSleep(AntibanActionType.SlowPace);

                counter = 0;
                while (getInventory().contains(Items.GoldBar.name) && getInventory().contains(Items.Emerald.name) && counter < 8) {
                    if (getLocalPlayer().isAnimating()) {
                        counter = 0;
                    } else {
                        counter++;
                    }
                    antibanService.antibanSleep(AntibanActionType.SlowPace);
                }

                break;

            case BANK:

                // if player in edgeville, go to edgeville north bank... else go to closest bank
                if (Areas.EdgevilleBankToFurnace.getArea().contains(getLocalPlayer())) {
                    sharedService.walkTo(Areas.EdgevilleBankNorth);
                }

                if (bankService.bankAllExcept(false, Items.AmuletMould.name)) {
                    // update variables
                    totalGoldBars = getBank().count(Items.GoldBar.name) + getInventory().count(Items.GoldBar.name);
                    totalEmeralds = getBank().count(Items.Emerald.name) + getInventory().count(Items.Emerald.name);
                    totalEmeraldAmulets = getBank().count(Items.EmeraldAmulet.name) + getInventory().count(Items.EmeraldAmulet.name);
                    totalCoins = getBank().count(Items.Coins.name) + getInventory().count(Items.Coins.name);
                    if (initialMoney == -1) {
                        initialMoney = totalCoins;
                    }

                    if (getInventory().count(Items.AmuletMould.name) == 0) {
                        bankService.withdraw(Items.AmuletMould.name, 1, false, false);
                    }

                    if (totalGoldBars > 0 && totalEmeralds > 0) {
                        bankService.withdraw(Items.GoldBar.name, 13, false, false);
                        bankService.withdraw(Items.Emerald.name, 13, false, false);
                    }
                }

                break;

            case STOP:
                sharedService.logout();
                stop();
                break;
        }

        return 0;
    }

}
