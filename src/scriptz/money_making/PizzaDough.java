package scriptz.money_making;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;
import shared.services.providers.GELookupResult;

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

    private boolean rebuy = false;
    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalPotOfFlour = -1;
    private int totalJugOfWater = -1;
    private int totalPizzaBase = -1;

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

        if (getInventory().contains(POT_OF_FLOUR) && getInventory().contains(JUG_OF_WATER)) {
            if (!getInventory().get(POT_OF_FLOUR).isNoted() && !getInventory().get(JUG_OF_WATER).isNoted()) {
                return State.MAKE_DOUGH;
            }
        }

        // Initialize all statuses
        if (initialMoney == -1 || totalPotOfFlour == -1 || totalPizzaBase == -1 || totalCoins == -1) {
            return State.BANK;
        }

        if (totalPotOfFlour == 0 || totalJugOfWater == 0) {
            if (totalPizzaBase > 0) {
                return State.SELL;
            }
            if (rebuy) {
                return State.BUY;
            } else {
                return State.STOP;
            }
        }

        return State.BANK;
    }

    public void printPlayerTotals() {
        GELookupResult gePotOfFlour = grandExchangeService.getApi().lookup(POT_OF_FLOUR_ID);
        GELookupResult geJugOfWater = grandExchangeService.getApi().lookup(JUG_OF_WATER_ID);
        GELookupResult gePizzaBase = grandExchangeService.getApi().lookup(PIZZA_BASE_ID);

        if (gePotOfFlour != null && geJugOfWater != null && gePizzaBase != null) {
            int potOfFlourMoney = totalPotOfFlour * gePotOfFlour.price;
            int jugOfWaterMoney = totalJugOfWater * geJugOfWater.price;
            int pizzaBaseMoney = totalPizzaBase * gePizzaBase.price;

            int playerTotalMoney = totalCoins + potOfFlourMoney + jugOfWaterMoney;
            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total of" +
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

        logScript("-- Current state: " + currentState.name());
        printPlayerTotals();

        switch (currentState) {

            case BUY:
                if (getInventory().count("Coins") < 1000) {
                    bankService.withdraw(COINS, null, true, false);
                    totalCoins = getInventory().count(COINS);
                }

                bankService.closeBank(); // just making sure bank is closed



                GELookupResult gePotOfFlour = grandExchangeService.getApi().lookup(POT_OF_FLOUR_ID);
                GELookupResult geJugOfWater = grandExchangeService.getApi().lookup(JUG_OF_WATER_ID);
                int potOfFlourPrice = (int) (gePotOfFlour.price * 1.21);
                int jugOfWaterPrice = (int) (geJugOfWater.price * 1.21);
                int amount = totalCoins / (potOfFlourPrice + jugOfWaterPrice);
                grandExchangeService.addBuyExchange("pot of", POT_OF_FLOUR);
                grandExchangeService.addBuyExchange("jug o", JUG_OF_WATER);
                grandExchangeService.collect(true);

                totalCoins = getInventory().count(COINS) + 1;
                totalPotOfFlour = getInventory().count(POT_OF_FLOUR);
                totalJugOfWater = getInventory().count(JUG_OF_WATER);
                break;

            case SELL:
                bankService.withdraw(PIZZA_BASE, null, false, true);
                bankService.withdraw(COINS, null, true, false);

                GELookupResult gePizzaBase = grandExchangeService.getApi().lookup(PIZZA_BASE_ID);
                grandExchangeService.addSellExchange(PIZZA_BASE);
                grandExchangeService.collect(false);

                totalPizzaBase = 0;
                totalCoins = getInventory().count(COINS) + 1;
                break;

            case MAKE_DOUGH:
                while (getInventory().contains(POT_OF_FLOUR) && getInventory().contains(JUG_OF_WATER)) {
                    interactService.interactInventoryItems(POT_OF_FLOUR, JUG_OF_WATER, true, false);
                }
                break;

            case BANK:
                bankService.bankAll(false);
                bankService.withdraw(POT_OF_FLOUR, 9, false, false);
                bankService.withdraw(JUG_OF_WATER, 9, true, false);

                // update variables
                totalPotOfFlour = getBank().count(POT_OF_FLOUR) + getInventory().count(POT_OF_FLOUR);
                totalJugOfWater = getBank().count(JUG_OF_WATER) + getInventory().count(JUG_OF_WATER);
                totalPizzaBase = getBank().count(PIZZA_BASE) + getInventory().count(PIZZA_BASE);
                totalCoins = getBank().count(COINS) + getInventory().count(COINS);
                if (initialMoney == -1) {
                    initialMoney = totalCoins;
                }

                break;

            case STOP:
                stop();
                break;

        }

        return 0;
    }
}