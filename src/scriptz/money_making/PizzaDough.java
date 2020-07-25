package scriptz.money_making;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
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


    private final String COINS = "Coins";
    private final String POT_OF_FLOUR = "Pot of flour";
    private final int POT_OF_FLOUR_ID = 1933;
    private final String JUG_OF_WATER = "Jug of water";
    private final int JUG_OF_WATER_ID = 1937;
    private final String PIZZA_BASE = "Pizza base";
    private final int PIZZA_BASE_ID = 2283;

    private boolean rebuy = true;
    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalPotOfFlour = -1;
    private int totalJugOfWater = -1;
    private int totalPizzaBase = -1;

    private int lastPotOfFlourPrice = -1;
    private int lastJugOfWaterPrice = -1;
    private int lastPizzaBasePrice = -1;

    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();

        logScript("Starting dough script!");
    }

    public void onExit() {
        log("Ending dough script!");
    }

    private State getState() {

        if (getInventory().isFull()) {
            logScript("-- Current state: [" + State.BANK + "] inventory full");
            return State.BANK;
        }

        if (getInventory().contains(POT_OF_FLOUR) && getInventory().contains(JUG_OF_WATER)) {
            if (!getInventory().get(POT_OF_FLOUR).isNoted() && !getInventory().get(JUG_OF_WATER).isNoted()) {
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

            int playerTotalMoney = totalCoins + potOfFlourMoney + jugOfWaterMoney;

            if (initialMoney == -1) {
                initialMoney = totalCoins;
            }

            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total equivalent of" +
                    "\n\t- gp = " + playerTotalMoney / 1000 + "k" +
                    "\n\t- gp/h = " + moneyPerHour / 1000 + "k/h" +
                    "\n\t- pot of flour = " + totalPotOfFlour + " [ " + potOfFlourMoney + "k]" +
                    "\n\t- jug of water = " + totalJugOfWater + " [ " + jugOfWaterMoney + "k]" +
                    "\n\t- pizza bases = " + totalPizzaBase + " [ " + pizzaBaseMoney + "k]");
        }
    }

    @Override
    public int onLoop() {

        super.onLoop();

        State currentState = getState();

        printPlayerTotals();

        switch (currentState) {

            case BUY:
                if (getInventory().count("Coins") < 1000) {
                    bankService.withdraw(COINS, null, true, false);
                    totalCoins = getInventory().count(COINS);
                }

                bankService.closeBank(); // just making sure bank is closed

                while (lastPotOfFlourPrice == -1) {
                    if (grandExchangeService.addBuyExchange("pot of", POT_OF_FLOUR, false, true, false)) {
                        lastPotOfFlourPrice = getGrandExchange().getCurrentPrice();
                    }
                }

                while (lastJugOfWaterPrice == -1) {
                    if (grandExchangeService.addBuyExchange("jug o", JUG_OF_WATER, false, true, false)) {
                        lastJugOfWaterPrice = getGrandExchange().getCurrentPrice();
                    }
                }

                int potOfFlourFinalPrice = (int) (lastPotOfFlourPrice * 1.21);
                int jugOfWaterFinalPrice = (int) (lastJugOfWaterPrice * 1.21);
                int amount = totalCoins / (potOfFlourFinalPrice + jugOfWaterFinalPrice);

                if (grandExchangeService.addBuyExchange("pot of", POT_OF_FLOUR, false, false, false)) {
                    lastPotOfFlourPrice = getGrandExchange().getCurrentPrice();
                    grandExchangeService.setPriceQuantityConfirm(POT_OF_FLOUR, potOfFlourFinalPrice, amount, false);
                }

                if (grandExchangeService.addBuyExchange("jug o", JUG_OF_WATER, false, false, false)) {
                    lastJugOfWaterPrice = getGrandExchange().getCurrentPrice();
                    grandExchangeService.setPriceQuantityConfirm(JUG_OF_WATER, jugOfWaterFinalPrice, amount, false);
                }

                grandExchangeService.collect(true);

                totalCoins = getInventory().count(COINS);
                totalPotOfFlour = getInventory().count(POT_OF_FLOUR);
                totalJugOfWater = getInventory().count(JUG_OF_WATER);
                break;

            case SELL:
                bankService.withdraw(PIZZA_BASE, null, false, true);
                bankService.withdraw(COINS, null, true, false);

                if (grandExchangeService.addSellExchange(PIZZA_BASE)) {
                    lastPizzaBasePrice = getGrandExchange().getCurrentPrice();
                    if (grandExchangeService.setPriceQuantityConfirm(PIZZA_BASE, (int) (lastPizzaBasePrice * 0.79), null, false)) {
                        grandExchangeService.collect(false);
                        totalPizzaBase = 0;
                        totalCoins = getInventory().count(COINS);
                    }
                }

                break;

            case MAKE_DOUGH:
                if (!getInventory().isFull() && getInventory().contains(POT_OF_FLOUR) && getInventory().contains(JUG_OF_WATER)) {
                    interactService.interactInventoryItems(POT_OF_FLOUR, JUG_OF_WATER, false, false);

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
                    while (!getInventory().isFull() && getInventory().contains(POT_OF_FLOUR) && getInventory().contains(JUG_OF_WATER) && counter < 20) {
                        logScript("Still interacting with widget");
                        antibanService.antibanSleep(AntibanActionType.SlowPace);
                        counter++;
                    }
                }
                break;

            case BANK:
                bankService.bankAll(false);

                // update variables
                totalPotOfFlour = getBank().count(POT_OF_FLOUR);
                totalJugOfWater = getBank().count(JUG_OF_WATER);
                totalPizzaBase = getBank().count(PIZZA_BASE);
                totalCoins = getBank().count(COINS);

                int counter = 0;
                while (totalPotOfFlour > 0 && getInventory().count(POT_OF_FLOUR) == 0 && counter < 20) {
                    bankService.withdraw(POT_OF_FLOUR, 9, false, false);
                    counter++;
                }

                counter = 0;
                while (totalJugOfWater > 0 && getInventory().count(JUG_OF_WATER) == 0 && counter < 20) {
                    bankService.withdraw(JUG_OF_WATER, 9, true, false);
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