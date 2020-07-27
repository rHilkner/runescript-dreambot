package scriptz.money_making;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.GrandExchangeService;
import shared.services.InteractService;

import java.util.Date;
import java.util.List;
import java.util.Random;

@ScriptManifest(author = "Xpt", name = "Chocolate dust", version = 1.0, description = "Makes chocolate dust", category = Category.MONEYMAKING)
public class ChocolateDust extends RunescriptAbstractContext {

    enum State { SELL, BUY, MAKE_DUST, BANK, STOP, EAT_LAST_CHOCOLATE }

    private BankService bankService;
    private InteractService interactService;
    private GrandExchangeService grandExchangeService;

    private boolean spamChocolateBars = true;
    private boolean rebuy = false;

    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalChocolateBars = -1;
    private int totalChocolateDust = -1;

    private int lastChocolateBarPrice = -1;
    private int lastChocolateDustPrice = -1;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();

        logScript("Starting chocolate script!");
    }

    @Override
    public void onExit() {
        log("Ending chocolate script!");
    }
    
    private State getState() {

        // random 2% chance of eating chocolate, but only if not animating (aka doing another action)
        int random100 = new Random().nextInt(100);
        if (!getLocalPlayer().isAnimating() && random100 < 2) {
            return State.EAT_LAST_CHOCOLATE;
        }

        if (getInventory().contains(Items.Knife.name) && getInventory().contains(Items.ChocolateBar.name)) {
            if (!getInventory().get(Items.ChocolateBar.name).isNoted()) {
                logScript("-- Current state: [" + State.MAKE_DUST + "] inventory contains chocolate bar");
                return State.MAKE_DUST;
            }
        }

        // Initialize all statuses
        if (initialMoney == -1 || totalChocolateBars == -1 || totalChocolateDust == -1 || totalCoins == -1) {
            logScript("-- Current state: [" + State.BANK + "] initialize variable counts");
            return State.BANK;
        }
        
        if (totalChocolateBars == 0) {
            if (totalChocolateDust > 0) {
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

        if (lastChocolateBarPrice != -1 && lastChocolateDustPrice == -1 && totalCoins != -1) {
            int chocolateBarMoney = totalChocolateBars * lastChocolateBarPrice;
            int chocolateDustMoney = totalChocolateDust * lastChocolateDustPrice;

            int playerTotalMoney = totalCoins + chocolateBarMoney + chocolateDustMoney;

            if (initialMoney == -1) {
                initialMoney = playerTotalMoney;
            }

            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total equivalent of" +
                    "\n\t- gp = " + playerTotalMoney/1000 + "k" +
                    "\n\t- gp/h = " + moneyPerHour/1000 + "k/h" +
                    "\n\t- chocolate bars = " + totalChocolateBars + " [ " + chocolateBarMoney/1000 + "k]" +
                    "\n\t- chocolate dust = " + totalChocolateDust + " [ " + chocolateDustMoney/1000 + "k]");
        }
    }

    @Override
    public int onLoop() {
        super.onLoop();

        if (getInventory().isItemSelected()) {
            getInventory().deselect();
        }
        
        State currentState = getState();

        logScript("-- Current state: " + currentState.name());
        printPlayerTotals();

        switch (currentState) {

            case EAT_LAST_CHOCOLATE:
                List<Item> itemList = getInventory().getCollection();
                // sort starting by last position of inventory
                itemList.sort((a, b) -> b.getSlot() - a.getSlot());
                for (Item item : itemList) {
                    if (Items.ChocolateBar.name.equals(item.getName())) {
                        interactService.interactInventoryItem(item.getSlot(), "Eat");
                        break;
                    }
                }

                break;

            case SELL:
                bankService.withdraw(Items.ChocolateDust.name, null, false, true);
                bankService.withdraw(Items.Coins.name, null, true, false);

                if (grandExchangeService.addSellExchange(Items.ChocolateDust.name)) {
                    lastChocolateDustPrice = getGrandExchange().getCurrentPrice();
                    if (grandExchangeService.setPriceQuantityConfirm(Items.PizzaBase.name, (int) (lastChocolateDustPrice * 0.79), null, false)) {
                        grandExchangeService.collect(false);
                        totalChocolateDust = 0;
                        totalCoins = getInventory().count(Items.Coins.name);
                    }
                }

                break;

            case BUY:
                if (getInventory().count("Coins") < 100) {
                    bankService.withdraw(Items.Coins.name, null, true, false);
                    bankService.closeBank(); // just making sure bank is closed
                }

                if (grandExchangeService.addBuyExchange("choco", Items.ChocolateBar.name, false, false)) {
                    lastChocolateBarPrice = getGrandExchange().getCurrentPrice();
                    logScript("lastChocolateBarPrice = " + lastChocolateBarPrice);
                    grandExchangeService.setPriceQuantityConfirm(Items.PotOfFlour.name, (int) (lastChocolateBarPrice * 1.21), null, false);
                }

                grandExchangeService.collect(true);

                totalCoins = getInventory().count(Items.Coins.name);
                totalChocolateBars = getInventory().count(Items.ChocolateBar.name);
                break;

            case MAKE_DUST:
                bankService.closeBank(); // just making sure bank is closed
                if (!getLocalPlayer().isAnimating()) {
                    interactService.interactInventoryItems(Items.Knife.name, Items.ChocolateBar.name, spamChocolateBars, true);
                }
                break;

            case BANK:
                bankService.bankAllExcept(false, Items.Knife.name);

                if (getInventory().count(Items.Knife.name) == 0) {
                    bankService.withdraw(Items.Knife.name, 1, false, false);
                }

                bankService.withdraw(Items.ChocolateBar.name, null, false, false);

                // update variables
                totalChocolateBars = getBank().count(Items.ChocolateBar.name) + getInventory().count(Items.ChocolateBar.name);
                totalChocolateDust = getBank().count(Items.ChocolateDust.name) + getInventory().count(Items.ChocolateDust.name);
                totalCoins = getBank().count(Items.Coins.name) + getInventory().count(Items.Coins.name);
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