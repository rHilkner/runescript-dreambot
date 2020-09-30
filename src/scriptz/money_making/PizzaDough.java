package scriptz.money_making;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;

import java.util.Date;

@ScriptManifest(author = "xpt", name = "Pizza Dough", version = 1.0, description = "Makes Pizza Dough", category = Category.COOKING)
public class PizzaDough extends RunescriptAbstractContext {

    enum State { BUY, SELL, MAKE_DOUGH, BANK, STOP }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    private boolean rebuy = false;

    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalPotOfFlour = -1;
    private int totalJugOfWater = -1;
    private int totalPizzaBase = -1;

    private int lastPotOfFlourPrice = -1;
    private int lastJugOfWaterPrice = -1;
    private int lastPizzaBasePrice = -1;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();

        logScript("Starting dough script!");
    }
    
    @Override
    public void onExit() {
        log("Ending dough script!");
    }

    private State getState() {

        if (getInventory().isFull()) {
            logScript("-- Current state: [" + State.BANK + "] inventory full");
            return State.BANK;
        }

        if (getInventory().contains(Items.PotOfFlour.name) && getInventory().contains(Items.JugOfWater.name)) {
            if (!getInventory().get(Items.PotOfFlour.name).isNoted() && !getInventory().get(Items.JugOfWater.name).isNoted()) {
                logScript("-- Current state: [" + State.MAKE_DOUGH + "] inventory contains flour and water");
                return State.MAKE_DOUGH;
            }
        }

        // Initialize all statuses
        if (totalPotOfFlour == -1 || totalJugOfWater == -1 || totalPizzaBase == -1 || totalCoins == -1) {
            logScript("-- Current state: [" + State.BANK + "] initialize variable counts");
            return State.BANK;
        }

        if (totalPotOfFlour == 0 || totalJugOfWater == 0) {
            if (totalPizzaBase > 0) {
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

        if (lastPotOfFlourPrice != -1 && lastJugOfWaterPrice != -1 && lastPizzaBasePrice != -1 && totalCoins != -1) {
            int potOfFlourMoney = totalPotOfFlour * lastPotOfFlourPrice;
            int jugOfWaterMoney = totalJugOfWater * lastJugOfWaterPrice;
            int pizzaBaseMoney = totalPizzaBase * lastPizzaBasePrice;

            int playerTotalMoney = totalCoins + potOfFlourMoney + jugOfWaterMoney + pizzaBaseMoney;

            if (initialMoney == -1) {
                initialMoney = playerTotalMoney;
            }

            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total equivalent of" +
                    "\n\t- gp = " + playerTotalMoney/1000 + "k" +
                    "\n\t- gp/h = " + moneyPerHour/1000 + "k/h" +
                    "\n\t- pot of flour = " + totalPotOfFlour + " [ " + potOfFlourMoney/1000 + "k]" +
                    "\n\t- jug of water = " + totalJugOfWater + " [ " + jugOfWaterMoney/1000 + "k]" +
                    "\n\t- pizza bases = " + totalPizzaBase + " [ " + pizzaBaseMoney/1000 + "k]");
        }
    }

    @Override
    public int onLoop() {

        super.onLoop();

        State currentState = getState();

        printPlayerTotals();

        switch (currentState) {

            case BUY:
                if (getInventory().count(Items.Coins.name) < 1000) {
                    bankService.withdraw(Items.Coins.name, null, true, false, false);
                    totalCoins = getInventory().count(Items.Coins.name);
                }

                bankService.closeBank(false); // just making sure bank is closed

                while (lastPotOfFlourPrice == -1) {
                    if (grandExchangeService.addBuyExchange("pot of", Items.PotOfFlour.name, false, false)) {
                        lastPotOfFlourPrice = getGrandExchange().getCurrentPrice();
                        logScript("lastPotOfFlourPrice = " + lastPotOfFlourPrice);
                        grandExchangeService.goBack();
                    }
                }

                while (lastJugOfWaterPrice == -1) {
                    if (grandExchangeService.addBuyExchange("jug o", Items.JugOfWater.name, false, false)) {
                        lastJugOfWaterPrice = getGrandExchange().getCurrentPrice();
                        logScript("lastJugOfWaterPrice = " + lastJugOfWaterPrice);
                        grandExchangeService.goBack();
                    }
                }

                int potOfFlourFinalPrice = (int) (lastPotOfFlourPrice * 1.21);
                int jugOfWaterFinalPrice = (int) (lastJugOfWaterPrice * 1.21);
                int amount = (int) (totalCoins  * 0.95) / (potOfFlourFinalPrice + jugOfWaterFinalPrice);

                if (grandExchangeService.addBuyExchange("pot of", Items.PotOfFlour.name, false, false)) {
                    lastPotOfFlourPrice = getGrandExchange().getCurrentPrice();
                    logScript("lastPotOfFlourPrice = " + lastPotOfFlourPrice);
                    grandExchangeService.setPriceQuantityConfirm(Items.PotOfFlour.name, potOfFlourFinalPrice, amount, false);
                }

                if (grandExchangeService.addBuyExchange("jug o", Items.JugOfWater.name, false, false)) {
                    lastJugOfWaterPrice = getGrandExchange().getCurrentPrice();
                    logScript("lastJugOfWaterPrice = " + lastJugOfWaterPrice);
                    grandExchangeService.setPriceQuantityConfirm(Items.JugOfWater.name, jugOfWaterFinalPrice, amount, false);
                }

                grandExchangeService.collect(true);

                totalCoins = getInventory().count(Items.Coins.name);
                totalPotOfFlour = getInventory().count(Items.PotOfFlour.name);
                totalJugOfWater = getInventory().count(Items.JugOfWater.name);
                break;

            case SELL:
                bankService.withdraw(Items.PizzaBase.name, null, false, true, false);
                bankService.withdraw(Items.Coins.name, null, true, false, false);

                if (grandExchangeService.addSellExchange(Items.PizzaBase.name)) {
                    lastPizzaBasePrice = getGrandExchange().getCurrentPrice();
                    if (grandExchangeService.setPriceQuantityConfirm(Items.PizzaBase.name, (int) (lastPizzaBasePrice * 0.79), null, false)) {
                        grandExchangeService.collect(false);
                        totalPizzaBase = 0;
                        totalCoins = getInventory().count(Items.Coins.name);
                    }
                }

                break;

            case MAKE_DOUGH:
                if (!getInventory().isFull() && getInventory().contains(Items.PotOfFlour.name) && getInventory().contains(Items.JugOfWater.name)) {
                    interactService.interactInventoryItems(Items.PotOfFlour.name, Items.JugOfWater.name, false, false);

                    WidgetChild pizzaBaseWidget = getWidgets().getWidgetChild(270, 16, 38);

                    if (pizzaBaseWidget == null) {
                        logScript("wtf, pizza widget null");
                        return 0;
                    } else {
                        logScript("Interacting with widget " + pizzaBaseWidget.getText());
                        pizzaBaseWidget.interact();
                        antibanService.antibanSleep(AntibanActionType.SlowPace);
                    }

                    // counter variable is there to make sure script doesn't get stuck if dough isn't being made
                    int counter = 0;
                    while (!getInventory().isFull() && getInventory().contains(Items.PotOfFlour.name) && getInventory().contains(Items.JugOfWater.name) && counter < 20) {
                        logScript("Still interacting with widget");
                        antibanService.antibanSleep(AntibanActionType.SlowPace);
                        counter++;
                    }
                }
                break;

            case BANK:
                bankService.bankAll(false, false);

                // update variables
                totalPotOfFlour = getBank().count(Items.PotOfFlour.name);
                totalJugOfWater = getBank().count(Items.JugOfWater.name);
                totalPizzaBase = getBank().count(Items.PizzaBase.name);
                totalCoins = getBank().count(Items.Coins.name);

                logScript("updated variables:" +
                        "\n- #coins: " + totalCoins +
                        "\n- #pot of flour: " + totalPotOfFlour +
                        "\n- #jug of water: " + totalJugOfWater +
                        "\n- #pizza base: " + totalPizzaBase);

                int counter = 0;
                while (totalPotOfFlour > 0 && getInventory().count(Items.PotOfFlour.name) == 0 && counter < 20) {
                    bankService.withdraw(Items.PotOfFlour.name, 9, false, false, false);
                    counter++;
                }

                counter = 0;
                while (totalJugOfWater > 0 && getInventory().count(Items.JugOfWater.name) == 0 && counter < 20) {
                    bankService.withdraw(Items.JugOfWater.name, 9, true, false, false);
                    counter++;
                }

                break;

            case STOP:
                stop();
                break;

        }

        return 0;
    }
}