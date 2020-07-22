package shared.services;

import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import shared.Constants;
import shared.enums.AntibanActionType;

import java.util.Objects;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class GrandExchangeService extends AbstractService {

    private static GrandExchangeService instance;

    enum ExchangeType { BUY, SELL }

    private final XptZenAntibanService antibanService;

    private GrandExchangeService() {
        super();
        antibanService = XptZenAntibanService.getInstance();
    }

    public static GrandExchangeService getInstance() {
        if (instance == null)
            instance = new GrandExchangeService();
        return instance;
    }

    public void openGE() {
        if (!ctx.getGrandExchange().isOpen()) {
            logScript("Opening GE");
            sleepUntil(() -> ctx.getGrandExchange().open(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> ctx.getGrandExchange().isOpen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void closeGE() {
        if (ctx.getGrandExchange().isOpen()) {
            logScript("Closing GE");
            sleepUntil(() -> ctx.getGrandExchange().close(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> !ctx.getGrandExchange().isOpen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void collect(boolean close) {
        openGE();

        if (ctx.getGrandExchange().isOpen()) {
            sleepUntil(() -> ctx.getGrandExchange().collect(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }

        if (close) {
            closeGE();
        }
    }

    public void addBuyExchange(String searchString, String itemName, Integer amount, double itemPriceMultiplier, boolean close) {
        addExchange(searchString, itemName, amount, itemPriceMultiplier, ExchangeType.BUY, close);
    }

    public void addSellExchange(String searchString, String itemName, Integer amount, double itemPriceMultiplier, boolean close) {
        addExchange(searchString, itemName, amount, itemPriceMultiplier, ExchangeType.SELL, close);
    }

    private void addExchange(String searchString, String itemName, Integer amount, double itemPriceMultiplier, ExchangeType type, boolean close) {

        openGE();

        if (!ctx.getGrandExchange().isOpen()) {
            return;
        }

        if (type == ExchangeType.BUY) {
            logScript("addbuying");
            while (!ctx.getGrandExchange().isBuyOpen()) {
                logScript("Opening buy screen");
                sleepUntil(() -> ctx.getGrandExchange().openBuyScreen(0), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }

            WidgetChild itemWidget = null;
            Item currentItem = ctx.getGrandExchange().getCurrentChosenItem();
            while (currentItem == null || !Objects.equals(currentItem.getName(), itemName)) {
                while (itemWidget == null) {
                    logScript("Searching for item: " + searchString);
                    sleepUntil(() -> ctx.getGrandExchange().searchItem(searchString), Constants.MAX_SLEEP_UNTIL);
                    antibanService.antibanSleep(AntibanActionType.FastPace);
                    itemWidget = ctx.getGrandExchange().getItemChildInSearch(itemName);
                }
                WidgetChild finalItemWidget = itemWidget;
                logScript("Interacting with widget: " + finalItemWidget.getText());
                sleepUntil(() -> finalItemWidget.interact(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                currentItem = ctx.getGrandExchange().getCurrentChosenItem();
            }
        } else if (type == ExchangeType.SELL) {
            logScript("addselling");
            Item currentItem = ctx.getGrandExchange().getCurrentChosenItem();
            while (currentItem == null || !Objects.equals(currentItem.getName(), itemName)) {
                sleepUntil(() -> ctx.getGrandExchange().addSellItem(itemName), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                currentItem = ctx.getGrandExchange().getCurrentChosenItem();
            }
        }

        Item currentItem = ctx.getGrandExchange().getCurrentChosenItem();
        int currentPrice = ctx.getGrandExchange().getCurrentPrice();
        int currentAmount = ctx.getGrandExchange().getCurrentAmount();
        logScript("Adding exchange [" + type.name() + "] for:" +
                "\n\t- item: " + currentItem.getName() +
                "\n\t- price: " + currentPrice + " gp" +
                "\n\t- amount: " + currentAmount);

        int itemPrice = ctx.getGrandExchange().getCurrentPrice();
        int finalPrice = (int) (itemPrice * itemPriceMultiplier);
        int finalAmount = -1;

        if (type == ExchangeType.BUY) {
            int totalMoney = ctx.getInventory().count("Coins");
            int maxAmount = totalMoney / finalPrice;
            finalAmount = amount == null || amount > maxAmount ? maxAmount : amount;

            while (finalAmount > 0 && finalAmount != ctx.getGrandExchange().getCurrentAmount()) {
                int finalAmount1 = finalAmount;
                logScript("GE setting total amount: " + finalAmount1);
                sleepUntil(() -> ctx.getGrandExchange().setQuantity(finalAmount1), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        } else if (type == ExchangeType.SELL) {
            finalAmount = ctx.getGrandExchange().getCurrentAmount();
        }

        logScript("Adding exchange [" + type.name() + "] for:" +
                "\n\t- item: " + itemName +
                "\n\t- price: " + finalPrice + " gp" +
                "\n\t- amount: " + finalAmount);

        while (finalPrice != ctx.getGrandExchange().getCurrentPrice()) {
            logScript("GE setting price: " + finalPrice);
            sleepUntil(() -> ctx.getGrandExchange().setPrice(finalPrice), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> ctx.getGrandExchange().getCurrentPrice() == finalPrice, Constants.MAX_SLEEP_UNTIL);
            logScript("GE current price: " + ctx.getGrandExchange().getCurrentPrice());
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }

        while (ctx.getGrandExchange().isBuyOpen() || ctx.getGrandExchange().isSellOpen()) {
            logScript("GE confirming");
            sleepUntil(() -> ctx.getGrandExchange().confirm(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }

        if (close) {
            closeGE();
        }
    }

}
