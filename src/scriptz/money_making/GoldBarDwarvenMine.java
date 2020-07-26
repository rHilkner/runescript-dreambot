package scriptz.money_making;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.*;

import java.util.Date;

@ScriptManifest(author = "xpt", name = "Golden drunken barz", version = 1.0, description = "Buying gold bars from GE and selling on Dwarven Mines for profit", category = Category.MONEYMAKING)
public class GoldBarDwarvenMine extends RunescriptAbstractContext {

    enum State { GO_TO_DWARV_MINE, GO_TO_GE, BUY, SELL, WORLD_HOP, BANK, STOP }

    private BankService bankService;
    private ShopService shopService;
    private InteractService interactService;
    private WorldHopService worldHopService;
    private GrandExchangeService grandExchangeService;

    private boolean rebuy = true;
    private int SELL_UNTIL = 20;
    private Integer MAX_GOLD_BARS = null; // maximum amount of gold-bars to buy on GE (null for "as many as possible")

    private int initialMoney = -1;
    private int totalCoins = -1;
    private int totalGoldBars = -1;
    private int shopTotalGoldBars = -1;

    private int lastGoldBarPrice = -1;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        shopService = ShopService.getInstance();
        interactService = InteractService.getInstance();
        worldHopService = WorldHopService.getInstance();
        grandExchangeService = GrandExchangeService.getInstance();

        logScript("Starting golden barz0rz script!");
    }

    @Override
    public void onExit() {
        log("Ending golden barz0rz script!");
    }

    private State getState() {

        if (getLocalPlayer().getTile() == null) {
            return null;
        }

        // inventory full = go bank deposit all
        if (getInventory().isFull()) {
            logScript("-- Current state: [" + State.BANK + "] inventory empty, full or only with coins");
            return State.BANK;
        }

        // has gold-bars on inventory: go sell
        if (getInventory().contains(Items.GoldBar.name)) {
            if (shopTotalGoldBars < SELL_UNTIL) {
                if (Areas.DwarvenMineDrogoShop.getArea().contains(getLocalPlayer())) {
                    logScript("-- Current state: [" + State.SELL + "] player with gold bars and in dwarven shop");
                    return State.SELL;
                } else {
                    logScript("-- Current state: [" + State.GO_TO_DWARV_MINE + "] player with gold bars but not in dwarven shop");
                    return State.GO_TO_DWARV_MINE;
                }
            } else {
                logScript("-- Current state: [" + State.WORLD_HOP + "] dwarven shop has >= " + SELL_UNTIL + " gold bars");
                return State.WORLD_HOP;
            }
        }

        // if total-gold-bars were not initialized yet, bank
        // inventory empty (or only has coins) = go to bank check if you have any more gold bars
        if (totalGoldBars == -1) {
            logScript("-- Current state: [" + State.BANK + "] can't sell, going to bank");
            return State.BANK;
        }

        // if already banked and dont have any gold-bars, rebuy or else stop
        if (totalGoldBars == 0) {
            if (rebuy) {
                if (Areas.GrandExchange.getArea().contains(getLocalPlayer())) {
                    logScript("-- Current state: [" + State.BUY + "] no gold-bars");
                    return State.BUY;
                } else {
                    logScript("-- Current state: [" + State.GO_TO_GE + "] no gold-bars");
                    return State.GO_TO_GE;
                }
            } else {
                logScript("-- Current state: [" + State.STOP + "] no rebuy allowed");
                return State.STOP;
            }
        }

        // nothing of the above? odd... but lets bank to update all items counters
        logScript("-- Current state: [" + State.BANK + "] nothing interesting happens");
        return State.BANK;
    }

    private void printPlayerTotals() {

        if (lastGoldBarPrice != -1 && totalCoins != -1) {
            int goldBarMoney = totalGoldBars * lastGoldBarPrice;

            int playerTotalMoney = totalCoins + goldBarMoney;

            if (initialMoney == -1) {
                initialMoney = playerTotalMoney;
            }

            double hoursSinceBeginning = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60.0);
            int moneyPerHour = (int) ((playerTotalMoney - initialMoney) / (hoursSinceBeginning));

            logScript("-- Player has a total equivalent of" +
                    "\n\t- gp = " + playerTotalMoney/1000 + "k" +
                    "\n\t- gp/h = " + moneyPerHour/1000 + "k/h" +
                    "\n\t- pot of flour = " + totalGoldBars + "un * " + lastGoldBarPrice + "g [ " + goldBarMoney/1000 + "k]");
        }
    }

    @Override
    public int onLoop() {
        super.onLoop();
        State currentState = getState();
        printPlayerTotals();

        switch (currentState) {
            case GO_TO_DWARV_MINE:
                if (Areas.DwarvenMineDrogoShop.getArea().contains(getLocalPlayer())) {
                    break;
                }

                if (Areas.DwarvenMineIceMountainTop.getArea().contains(getLocalPlayer())) {
                    interactService.interactWithGameObject("Trapdoor", "Climb-down");
                }

                if (!sharedService.walkTo(Areas.DwarvenMineDrogoShop)) {
                    sharedService.walkTo(Areas.DwarvenMineIceMountainTop);
                }

                break;

            case GO_TO_GE:
                if (Areas.GrandExchange.getArea().contains(getLocalPlayer())) {
                    break;
                }

                if (Areas.DwarvenMineIceMountainBottom.getArea().contains(getLocalPlayer())) {
                    interactService.interactWithGameObject("Ladder", "Climb-up");
                }

                sharedService.walkTo(Areas.GrandExchange);
                break;

            case BUY:
                if (getInventory().count(Items.Coins.name) < 10000) {
                    bankService.withdraw(Items.Coins.name, null, true, false);
                    totalCoins = getInventory().count(Items.Coins.name);
                }

                bankService.closeBank(); // just making sure bank is closed

                if (grandExchangeService.addBuyExchange("gold ba", Items.GoldBar.name, false, false)) {
                    lastGoldBarPrice = getGrandExchange().getCurrentPrice();
                    logScript("lastJugOfWaterPrice = " + lastGoldBarPrice);
                    grandExchangeService.setPriceQuantityConfirm(Items.GoldBar.name, (int) (lastGoldBarPrice * 1.2), MAX_GOLD_BARS, false);
                }

                grandExchangeService.collect(true);

                totalCoins = getInventory().count(Items.Coins.name);
                totalGoldBars = getInventory().count(Items.GoldBar.name);

                break;

            case SELL:
                if (shopService.openShop()) {
                    shopTotalGoldBars = getShop().count(Items.GoldBar.name);
                    int quantityToSell = Integer.min(SELL_UNTIL - shopTotalGoldBars, getInventory().count(Items.GoldBar.name));
                    if (quantityToSell > 0) {
                        shopService.sell(Items.GoldBar.name, quantityToSell, false);
                    }
                    shopService.closeShop();
                }
                break;

            case WORLD_HOP:
                if (worldHopService.hopNext(true, true, true)) {
                    shopTotalGoldBars = -1;
                }
                break;

            case BANK:
                bankService.bankAllExcept(false, Items.GoldBar.name);

                // update variables
                totalCoins = getBank().count(Items.Coins.name) + getInventory().count(Items.Coins.name);
                totalGoldBars = getBank().count(Items.GoldBar.name) + getInventory().count(Items.GoldBar.name);

                logScript("updated variables:" +
                        "\n- #coins: " + totalCoins +
                        "\n- #gold bars: " + totalGoldBars);

                int counter = 0;
                while (totalGoldBars > 0 && getInventory().count(Items.GoldBar.name) == 0 && counter < 20) {
                    bankService.withdraw(Items.GoldBar.name, null, false, true);
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
