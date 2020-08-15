package scriptz.cooking;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;

import java.util.Date;

@ScriptManifest(author = "xpt", name = "Chocolate cake", version = 1.0, description = "Makes chocolate cake", category = Category.COOKING)
public class ChocolateCake extends RunescriptAbstractContext {

    enum State { BUY, SELL, MAKE_CAKE, BANK, STOP }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    private boolean rebuy = false;
    private final int MAX_QUANTITY = 1000;

    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalCake = -1;
    private int totalChocolateBar = -1;
    private int totalChocolateCake = -1;

    private int lastCakePrice = -1;
    private int lastChocolateBarPrice = -1;
    private int lastChocolateCakePrice = -1;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();

        logScript("Starting cake script!");
    }
    
    @Override
    public void onExit() {
        log("Ending cake script!");
    }

    private State getState() {

        if (getInventory().contains(Items.Cake.name) && getInventory().contains(Items.ChocolateBar.name)) {
            if (!getInventory().get(Items.Cake.name).isNoted() && !getInventory().get(Items.ChocolateBar.name).isNoted()) {
                logScript("-- Current state: [" + State.MAKE_CAKE + "] inventory contains flour and water");
                return State.MAKE_CAKE;
            }
        }

        // Initialize all statuses
        if (totalCake == -1 || totalChocolateBar == -1 || totalChocolateCake == -1 || totalCoins == -1) {
            logScript("-- Current state: [" + State.BANK + "] initialize variable counts");
            return State.BANK;
        }

        if (totalCake == 0 || totalChocolateBar == 0) {
            if (totalChocolateCake > 0) {
                logScript("-- Current state: [" + State.SELL + "] going to sell");
                return State.SELL;
            }
            if (rebuy) {
                logScript("-- Current state: [" + State.BUY + "] going to buy");
                return State.BUY;
            } else {
                logScript("-- Current state: [" + State.STOP + "] stoping");
                return State.STOP;
            }
        }

        logScript("-- Current state: [" + State.BANK + "] nothing interesting happens");
        return State.BANK;
    }

    private void printPlayerTotals() {

        if (lastCakePrice != -1 && lastChocolateBarPrice != -1 && lastChocolateCakePrice != -1 && totalCoins != -1) {
            int cakeMoney = totalCake * lastCakePrice;
            int chocolateBarMoney = totalChocolateBar * lastChocolateBarPrice;
            int chocolateCakeMoney = totalChocolateCake * lastChocolateCakePrice;

            int playerTotalMoney = totalCoins + cakeMoney + chocolateBarMoney + chocolateCakeMoney;

            if (initialMoney == -1) {
                initialMoney = playerTotalMoney;
            }

            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total equivalent of" +
                    "\n\t- gp = " + playerTotalMoney/1000 + "k" +
                    "\n\t- gp/h = " + moneyPerHour/1000 + "k/h" +
                    "\n\t- cake = " + totalCake + " [ " + cakeMoney/1000 + "k]" +
                    "\n\t- choc bar = " + totalChocolateBar + " [ " + chocolateBarMoney/1000 + "k]" +
                    "\n\t- choc cake = " + totalChocolateCake + " [ " + chocolateCakeMoney/1000 + "k]");
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

            case BUY:
                if (getInventory().count(Items.Coins.name) < 1000) {
                    bankService.withdraw(Items.Coins.name, null, true, false);
                    totalCoins = getInventory().count(Items.Coins.name);
                }

                bankService.closeBank(); // just making sure bank is closed

                while (lastCakePrice == -1) {
                    if (grandExchangeService.addBuyExchange("cake", Items.Cake.name, false, false)) {
                        lastCakePrice = getGrandExchange().getCurrentPrice();
                        logScript("lastCakePrice = " + lastCakePrice);
                        grandExchangeService.goBack();
                    }
                }

                while (lastChocolateBarPrice == -1) {
                    if (grandExchangeService.addBuyExchange("choco", Items.ChocolateBar.name, false, false)) {
                        lastChocolateBarPrice = getGrandExchange().getCurrentPrice();
                        logScript("lastChocolateBarPrice = " + lastChocolateBarPrice);
                        grandExchangeService.goBack();
                    }
                }

                int cakeFinalPrice = (int) (lastCakePrice * 1.21);
                int chocolateBarFinalPrice = (int) (lastChocolateBarPrice * 1.21);
                int amount = (int) (totalCoins  * 0.95) / (cakeFinalPrice + chocolateBarFinalPrice);

                if (grandExchangeService.addBuyExchange("cake", Items.Cake.name, false, false)) {
                    lastCakePrice = getGrandExchange().getCurrentPrice();
                    logScript("lastCakePrice = " + lastCakePrice);
                    grandExchangeService.setPriceQuantityConfirm(Items.Cake.name, cakeFinalPrice, Math.min(amount, MAX_QUANTITY), false);
                }

                if (grandExchangeService.addBuyExchange("choco", Items.ChocolateBar.name, false, false)) {
                    lastChocolateBarPrice = getGrandExchange().getCurrentPrice();
                    logScript("lastChocolateBarPrice = " + lastChocolateBarPrice);
                    grandExchangeService.setPriceQuantityConfirm(Items.ChocolateBar.name, chocolateBarFinalPrice, Math.min(amount, MAX_QUANTITY), false);
                }

                grandExchangeService.collect(true);

                totalCoins = getInventory().count(Items.Coins.name);
                totalCake = getInventory().count(Items.Cake.name);
                totalChocolateBar = getInventory().count(Items.ChocolateBar.name);
                break;

            case SELL:
                bankService.withdraw(Items.ChocolateCake.name, null, false, true);
                bankService.withdraw(Items.Coins.name, null, true, false);

                if (grandExchangeService.addSellExchange(Items.ChocolateCake.name)) {
                    lastChocolateCakePrice = getGrandExchange().getCurrentPrice();
                    logScript("lastChocolateCakePrice = " + lastChocolateCakePrice);
                    if (grandExchangeService.setPriceQuantityConfirm(Items.ChocolateCake.name, (int) (lastChocolateCakePrice * 0.79), null, false)) {
                        grandExchangeService.collect(false);
                        totalChocolateCake = 0;
                        totalCoins = getInventory().count(Items.Coins.name);
                    }
                }

                break;

            case MAKE_CAKE:
                bankService.closeBank(); // just making sure bank is closed

                if (getInventory().contains(Items.Cake.name) && getInventory().contains(Items.ChocolateBar.name)) {
                    interactService.interactInventoryItems(Items.Cake.name, Items.ChocolateBar.name, false, false);
                    getKeyboard().type(" ");
                    sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 5000));

                    // counter variable is there to make sure script doesn't get stuck if cake isn't being made
                    int counter = 0;
                    while (getInventory().contains(Items.Cake.name) && getInventory().contains(Items.ChocolateBar.name) && counter < 20) {
                        logScript("Still making cake");
                        antibanService.antibanSleep(AntibanActionType.SlowPace);
                        counter++;
                    }
                }
                break;

            case BANK:
                bankService.bankAll(false);

                // update variables
                totalCake = getBank().count(Items.Cake.name);
                totalChocolateBar = getBank().count(Items.ChocolateBar.name);
                totalChocolateCake = getBank().count(Items.ChocolateCake.name);
                totalCoins = getBank().count(Items.Coins.name);

                logScript("updated variables:" +
                        "\n- #coins: " + totalCoins +
                        "\n- #cake: " + totalCake +
                        "\n- #choc bar: " + totalChocolateBar +
                        "\n- #choc cake: " + totalChocolateCake);

                int counter = 0;
                while (totalCake > 0 && getInventory().count(Items.Cake.name) == 0 && counter < 20) {
                    bankService.withdraw(Items.Cake.name, 14, false, false);
                    counter++;
                }

                counter = 0;
                while (totalChocolateBar > 0 && getInventory().count(Items.ChocolateBar.name) == 0 && counter < 20) {
                    bankService.withdraw(Items.ChocolateBar.name, 14, true, false);
                    counter++;
                }

                break;

            case STOP:
                bankService.closeBank(); // just making sure bank is closed
                sharedService.logout();
                stop();
                break;

        }

        return 0;
    }
}