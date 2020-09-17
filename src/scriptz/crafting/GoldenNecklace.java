package scriptz.crafting;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;

import java.util.Date;

@ScriptManifest(author = "Xpt", name = "Golden necklace", version = 1.0, description = "Makes gold necklaces", category = Category.MONEYMAKING)
public class GoldenNecklace extends RunescriptAbstractContext {

    enum State { SELL, BUY, MAKE_NECKLACE, BANK, STOP }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    private boolean rebuy = false;

    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalGoldBars = -1;
    private int totalGoldNecklaces = -1;

    private int lastGoldBarsPrice = -1;
    private int lastGoldNecklacePrice = -1;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();
        antibanService.setSkillsToHover(Skill.CRAFTING);

        logScript("Starting gold necklaces script!");
    }

    @Override
    public void onExit() {
        log("Ending gold necklaces script!");
    }

    private State getState() {

        if (getInventory().contains(Items.NecklaceMould.name) && getInventory().contains(Items.GoldBar.name)) {
            if (!getInventory().get(Items.NecklaceMould.name).isNoted() && !getInventory().get(Items.GoldBar.name).isNoted()) {
                logScript("-- Current state: [" + State.MAKE_NECKLACE + "] inventory contains gold bar");
                return State.MAKE_NECKLACE;
            }
        }

        // Initialize all statuses
        if (initialMoney == -1 || totalGoldBars == -1 || totalGoldNecklaces == -1 || totalCoins == -1) {
            logScript("-- Current state: [" + State.BANK + "] initialize variable counts");
            return State.BANK;
        }

        if (totalGoldBars == 0) {
            if (totalGoldNecklaces > 0) {
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

        if (lastGoldBarsPrice != -1 && lastGoldNecklacePrice == -1 && totalCoins != -1) {
            int goldBarMoney = totalGoldBars * lastGoldBarsPrice;
            int goldNecklacesMoney = totalGoldNecklaces * lastGoldNecklacePrice;

            int playerTotalMoney = totalCoins + goldBarMoney + goldNecklacesMoney;

            if (initialMoney == -1) {
                initialMoney = playerTotalMoney;
            }

            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total equivalent of" +
                    "\n\t- gp = " + playerTotalMoney/1000 + "k" +
                    "\n\t- gp/h = " + moneyPerHour/1000 + "k/h" +
                    "\n\t- gold bars = " + totalGoldBars + " [ " + goldBarMoney/1000 + "k]" +
                    "\n\t- gold necklace = " + totalGoldNecklaces + " [ " + goldNecklacesMoney/1000 + "k]");
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
                bankService.withdraw(Items.GoldNecklace.name, null, false, true);
                bankService.withdraw(Items.Coins.name, null, true, false);

                if (grandExchangeService.addSellExchange(Items.GoldNecklace.name)) {
                    lastGoldNecklacePrice = getGrandExchange().getCurrentPrice();
                    if (grandExchangeService.setPriceQuantityConfirm(Items.PizzaBase.name, (int) (lastGoldNecklacePrice * 0.79), null, false)) {
                        grandExchangeService.collect(false);
                        totalGoldNecklaces = 0;
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
                        grandExchangeService.setPriceQuantityConfirm(Items.GoldBar.name, (int) (lastGoldBarsPrice * 1.21), null, false);
                    }

                    grandExchangeService.collect(true);

                    totalCoins = getInventory().count(Items.Coins.name);
                    totalGoldBars = getInventory().count(Items.GoldBar.name);
                }
                break;

            case MAKE_NECKLACE:
                bankService.closeBank(); // just making sure bank is closed
                if (!getLocalPlayer().isAnimating()) {
                    if (sharedService.walkTo(Areas.EdgevilleBankToFurnace)) {
                        if (sharedService.walkTo(Areas.EdgevilleFurnace)) {
                             if (interactService.interactWithGameObject("Furnace")) {
                                 WidgetChild widgetChild = getWidgets().getWidgetChild(446, 21);
                                 if (widgetChild != null) {
                                     logScript("Interacting with " + widgetChild.getText());
                                     widgetChild.interact();
                                     antibanService.antibanSleep(AntibanActionType.SlowPace);

                                     int counter = 0;
                                     while (getInventory().contains("Gold bar") && counter < 20) {
                                         antibanService.antibanSleep(AntibanActionType.SlowPace);
                                         counter++;
                                     }
                                 }
                             }
                        }
                    }
                }
                break;

            case BANK:
                if (sharedService.walkTo(Areas.EdgevilleBankNorth)) {
                    bankService.bankAllExcept(false, Items.NecklaceMould.name);

                    if (getInventory().count(Items.NecklaceMould.name) == 0) {
                        bankService.withdraw(Items.NecklaceMould.name, 1, false, false);
                    }

                    bankService.withdraw(Items.GoldBar.name, null, false, false);

                    // update variables
                    totalGoldBars = getBank().count(Items.GoldBar.name) + getInventory().count(Items.GoldBar.name);
                    totalGoldNecklaces = getBank().count(Items.GoldNecklace.name) + getInventory().count(Items.GoldNecklace.name);
                    totalCoins = getBank().count(Items.Coins.name) + getInventory().count(Items.Coins.name);
                    if (initialMoney == -1) {
                        initialMoney = totalCoins;
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
