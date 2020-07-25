package scriptz.money_making;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;
import shared.services.providers.GELookupResult;

import java.util.Date;

@ScriptManifest(author = "Xpt", name = "Chocolate dust", version = 1.0, description = "Makes chocolate dust", category = Category.MONEYMAKING)
public class ChocolateDust extends RunescriptAbstractContext {

    enum State { SELL, BUY, MAKE_DUST, BANK, STOP }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    private boolean spamChocolateBars = true;
    private boolean rebuy = false;
    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalChocolateBars = -1;
    private int totalChocolateDust = -1;

    private final String KNIFE = "Knife";
    private final String CHOCOLATE_BAR = "Chocolate bar";
    private final int CHOCOLATE_BAR_ID = 1973;
    private final String CHOCOLATE_DUST = "Chocolate dust";
    private final int CHOCOLATE_DUST_ID = 1975;
    private final String COINS = "Coins";

    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();

        logScript("Starting chocolate script!");
    }

    public void onExit() {
        log("Ending chocolate script!");
    }
    
    private State getState() {

        if (getInventory().contains(KNIFE) && getInventory().contains(CHOCOLATE_BAR)) {
            if (!getInventory().get(CHOCOLATE_BAR).isNoted()) {
                return State.MAKE_DUST;
            }
        }

        // Initialize all statuses
        if (initialMoney == -1 || totalChocolateBars == -1 || totalChocolateDust == -1 || totalCoins == -1) {
            return State.BANK;
        }
        
        if (totalChocolateBars == 0) {
            if (totalChocolateDust > 0) {
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
        GELookupResult geChocolateBar = grandExchangeService.getApi().lookup(CHOCOLATE_BAR_ID);
        GELookupResult geChocolateDust = grandExchangeService.getApi().lookup(CHOCOLATE_DUST_ID);
        int chocolateBarMoney = totalChocolateBars * geChocolateBar.price;
        int chocolateDustMoney = totalChocolateDust * geChocolateDust.price;

        int playerTotalMoney = totalCoins + chocolateBarMoney + chocolateDustMoney;
        double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
        int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

        logScript("-- Player has a total of" +
                "\n\t- gp = " + playerTotalMoney/1000 + "k" +
                "\n\t- gp/h = " + moneyPerHour/1000 + "k/h" +
                "\n\t- pot of flour = " + totalChocolateBars + " [ " + chocolateBarMoney + "k]" +
                "\n\t- jug of water = " + totalChocolateDust + " [ " + chocolateDustMoney + "k]");
    }

    @Override
    public int onLoop() {
        super.onLoop();
        
        State currentState = getState();

        logScript("-- Current state: " + currentState.name());
        printPlayerTotals();

        switch (currentState) {
            case SELL:
                bankService.withdraw(CHOCOLATE_DUST, null, false, true);
                bankService.withdraw(COINS, null, true, false);

                GELookupResult geChocolateDust = grandExchangeService.getApi().lookup(CHOCOLATE_DUST_ID);
                grandExchangeService.addSellExchange(CHOCOLATE_DUST);
                grandExchangeService.collect(false);

                totalChocolateDust = 0;
                totalCoins = getInventory().count(COINS) + 1;
                break;
            case BUY:
                if (getInventory().count("Coins") < 100) {
                    bankService.withdraw(COINS, null, true, false);
                    bankService.closeBank(); // just making sure bank is closed
                }

                GELookupResult geChocolateBar = grandExchangeService.getApi().lookup(CHOCOLATE_BAR_ID);
                grandExchangeService.addBuyExchange("choco", CHOCOLATE_BAR, false, false, false);
                totalCoins = getInventory().count(COINS) + 1;
                grandExchangeService.collect(true);
                totalChocolateBars = getInventory().count(CHOCOLATE_BAR);
                break;
            case MAKE_DUST:
                bankService.closeBank(); // just making sure bank is closed
                if (!getLocalPlayer().isAnimating()) {
                    interactService.interactInventoryItems(KNIFE, CHOCOLATE_BAR, spamChocolateBars, true);
                }
                break;
            case BANK:
                bankService.bankAllExcept(false, KNIFE);

                if (getInventory().count(KNIFE) == 0) {
                    bankService.withdraw(KNIFE, 1, false, false);
                }

                bankService.withdraw(CHOCOLATE_BAR, null, false, false);

                // update variables
                totalChocolateBars = getBank().count(CHOCOLATE_BAR) + getInventory().count(CHOCOLATE_BAR);
                totalChocolateDust = getBank().count(CHOCOLATE_DUST) + getInventory().count(CHOCOLATE_DUST);
                totalCoins = getBank().count(COINS) + getInventory().count(COINS);
                if (initialMoney == -1) {
                    initialMoney = totalCoins;
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