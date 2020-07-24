package shared.services;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.services.providers.GrandExchangeApi;

import java.util.Objects;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static scriptz.RunescriptAbstractContext.logScript;

public class GrandExchangeService extends AbstractService {

    enum ExchangeType { BUY, SELL }

    private static GrandExchangeService instance;

    private final GrandExchangeApi api;
    private final XptZenAntibanService antibanService;
    private final InteractService interactService;

    private GrandExchangeService() {
        super();
        antibanService = XptZenAntibanService.getInstance();
        interactService = InteractService.getInstance();
        api = GrandExchangeApi.getInstance();
    }

    public static GrandExchangeService getInstance() {
        if (instance == null)
            instance = new GrandExchangeService();
        return instance;
    }

    public GrandExchangeApi getApi() {
        return api;
    }

    public boolean openGE() {
        int counter = 0;

        while (!ctx.getGrandExchange().isOpen() && counter < 20) {
            logScript("Opening GE");
            sleepUntil(() -> ctx.getGrandExchange().open(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> ctx.getGrandExchange().isOpen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return ctx.getGrandExchange().isOpen();
    }

    public boolean closeGE() {
        int counter = 0;
        while (ctx.getGrandExchange().isOpen() && counter < 20) {
            logScript("Closing GE");
            sleepUntil(() -> ctx.getGrandExchange().close(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> !ctx.getGrandExchange().isOpen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }
        return ctx.getGrandExchange().isOpen();
    }

    public void collect(boolean close) {
        if (openGE()) {
            sleepUntil(() -> ctx.getGrandExchange().collect(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }

        if (close) {
            closeGE();
        }
    }

    public boolean addBuyExchange(String searchString, String itemName) {
        return addExchange(searchString, itemName, ExchangeType.BUY);
    }

    public boolean addSellExchange(String itemName) {
        return addExchange(null, itemName, ExchangeType.SELL);
    }

    public boolean setPriceQuantityConfirm(String itemName, Integer price, Integer amount, boolean close) {
        ExchangeType type;

        if (ctx.getGrandExchange().isBuyOpen()) {
            type = ExchangeType.BUY;
        } else if (ctx.getGrandExchange().isSellOpen()) {
            type = ExchangeType.SELL;
        } else {
            return false;
        }

        if (!setPrice(price)) {
            return false;
        }
        if (!setQuantity(itemName, amount, type)) {
            return false;
        }

        Item currentItem = ctx.getGrandExchange().getCurrentChosenItem();
        int currentPrice = ctx.getGrandExchange().getCurrentPrice();
        int currentAmount = ctx.getGrandExchange().getCurrentAmount();
        logScript("Adding exchange [" + type.name() + "] for:" +
                "\n\t- item: " + currentItem.getName() +
                "\n\t- price: " + currentPrice + " gp" +
                "\n\t- amount: " + currentAmount);

        confirm();

        if (close) {
            closeGE();
        }

        return true;
    }

    private boolean addExchange(String searchString, String itemName, ExchangeType type) {

        if (!openGE()) {
            return false;
        }

        if (type == ExchangeType.BUY && !addBuyItem(searchString, itemName)) {
            return false;
        } else if (type == ExchangeType.SELL && !addSellItem(itemName)) {
            return false;
        }

        return true;
    }

    private boolean openBuyScreen() {
        if (!ctx.getGrandExchange().isOpen() && !openGE()) {
            return false;
        }

        int counter = 0;
        while (!ctx.getGrandExchange().isBuyOpen() && counter < 20) {
            logScript("Opening buy screen");
            sleepUntil(() -> ctx.getGrandExchange().openBuyScreen(0), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return ctx.getGrandExchange().isBuyOpen();
    }

    private boolean addBuyItem(String searchString, String itemName) {
        WidgetChild itemWidget = null;

        if (!openBuyScreen()) {
            return false;
        }

        Item currentItem = ctx.getGrandExchange().getCurrentChosenItem();

        int counter = 0;
        while ((currentItem == null || !Objects.equals(currentItem.getName(), itemName)) && counter < 20) {
            int counter1 = 0;

            // searching for the item
            while (itemWidget == null && counter1 < 20) {
                logScript("Searching for item: " + searchString);
                sleepUntil(() -> ctx.getGrandExchange().searchItem(searchString), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                itemWidget = ctx.getGrandExchange().getItemChildInSearch(itemName);
                counter1++;
            }

            if (itemWidget != null) {
                interactService.interactWithWidget(itemWidget);
            }

            // interacting with the item
            currentItem = ctx.getGrandExchange().getCurrentChosenItem();
            counter++;
        }

        return currentItem == null || !Objects.equals(currentItem.getName(), itemName);
    }

    private boolean addSellItem(String itemName) {
        if (!ctx.getGrandExchange().isOpen() && !openGE()) {
            return false;
        }
        Item currentItem = ctx.getGrandExchange().getCurrentChosenItem();

        int counter = 0;
        while ((currentItem == null || !Objects.equals(currentItem.getName(), itemName)) && counter < 20) {
            sleepUntil(() -> ctx.getGrandExchange().addSellItem(itemName), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            currentItem = ctx.getGrandExchange().getCurrentChosenItem();
            counter++;
        }

        return currentItem != null && Objects.equals(currentItem.getName(), itemName);
    }

    private boolean setPrice(int price) {
        int counter = 0;
        while (price != ctx.getGrandExchange().getCurrentPrice() && counter < 20) {
            logScript("GE setting price: " + price);
            sleepUntil(() -> ctx.getGrandExchange().setPrice(price), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> ctx.getGrandExchange().getCurrentPrice() == price, Constants.MAX_SLEEP_UNTIL);
            logScript("GE current price: " + ctx.getGrandExchange().getCurrentPrice());
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return price == ctx.getGrandExchange().getCurrentPrice();
    }

    private boolean setQuantity(String itemName, Integer amount, ExchangeType type) {
        int maxAmount = 0;

        switch (type) {
            case BUY:
                int totalMoney = ctx.getInventory().count("Coins");
                int price = ctx.getGrandExchange().getCurrentPrice();
                maxAmount = totalMoney / price;
                break;
            case SELL:
                maxAmount = ctx.getInventory().count(itemName);
                break;
        }

        int finalAmount = amount == null || amount > maxAmount ? maxAmount : amount;

        // setting quantity
        int counter = 0;
        while (finalAmount > 0 && finalAmount != ctx.getGrandExchange().getCurrentAmount() && counter < 20) {
            // sleeping before setting amount to look like thinking
            ctx.sleep(Calculations.random(2800, 6200));

            logScript("GE setting total amount: " + finalAmount);
            sleepUntil(() -> ctx.getGrandExchange().setQuantity(finalAmount), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return finalAmount == ctx.getGrandExchange().getCurrentAmount();
    }

    private void confirm() {
        int counter = 0;
        while ((ctx.getGrandExchange().isBuyOpen() || ctx.getGrandExchange().isSellOpen()) && counter < 20) {
            logScript("GE confirming");
            sleepUntil(() -> ctx.getGrandExchange().confirm(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }
    }

}
