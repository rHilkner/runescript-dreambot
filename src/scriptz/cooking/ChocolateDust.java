package scriptz.cooking;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Util;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;

import java.util.Date;

@ScriptManifest(author = "Xpt", name = "Chocolate dust", version = 1.0, description = "Makes chocolate dust", category = Category.MONEYMAKING)
public class ChocolateDust extends RunescriptAbstractContext {

    enum State { SELL, BUY, MAKE_DUST, BANK, STOP }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    private boolean spamChocolateBars = true;
    private boolean rebuy = false;

    private final String KNIFE = "Knife";
    private final String CHOCOLATE_BAR = "Chocolate bar";
    private final String CHOCOLATE_DUST = "Chocolate dust";
    private final String COINS = "Coins";
    private int initialMoney = -1;
    private int totalMoney = -1;
    private int totalChocolateBars = -1;
    private int totalChocolateDust = -1;
    private int moneyPerHour = -1;
    private Date lastMoneyPerHourLog;

    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();

        log("Starting chocolate script!");
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
        if (initialMoney == -1 || totalChocolateBars == -1 || totalChocolateDust == -1 || totalMoney == -1) {
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

    @Override
    public int onLoop() {
        super.onLoop();
        
        State currentState = getState();

        logScript("-- Current state: " + currentState.name());

        logScript("-- Player has a total of" +
                "\n\t[money = " + totalMoney + "]" +
                "\n\t[money = " + moneyPerHour + "]" +
                "\n\t[chocolate bars = " + totalChocolateBars + "]" +
                "\n\t[chocolate dust = " + totalChocolateDust + "]");

        // log about money per hour every ... minutes
        int logEveryXSeconds = 60;
        if (lastMoneyPerHourLog == null || lastMoneyPerHourLog.after(Util.dateAddSeconds(new Date(), logEveryXSeconds))) {
            logScript("-- Money per hour: " + moneyPerHour);
            lastMoneyPerHourLog = new Date();
        }

        switch (currentState) {
            case SELL:
                bankService.withdraw(CHOCOLATE_DUST, null, false, true);
                bankService.withdraw(COINS, null, true, false);
                grandExchangeService.addSellExchange(null, CHOCOLATE_DUST, null, 0.78, false);
                totalChocolateDust = 0;
                grandExchangeService.collect(false);
                totalMoney = getInventory().count(COINS) + 1;
                double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
                moneyPerHour = (int) ((totalMoney - initialMoney) / (hoursSinceBeginning));
                break;
            case BUY:
                if (getInventory().count("Coins") < 100) {
                    bankService.withdraw(COINS, null, true, false);
                }
                bankService.closeBank(); // just making sure bank is closed
                grandExchangeService.addBuyExchange("choco", CHOCOLATE_BAR, null, 1.21, false);
                totalMoney = getInventory().count(COINS) + 1;
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
                totalMoney = getBank().count(COINS) + getInventory().count(COINS);
                if (initialMoney == -1) {
                    initialMoney = totalMoney;
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