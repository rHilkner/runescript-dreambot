package shared.services;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.services.providers.GrandExchangeApi;

import java.util.ArrayList;
import java.util.Arrays;
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

    private boolean isGEOpen() {
        return ctx.getGrandExchange().isOpen() || ctx.getGrandExchange().isGeneralOpen() || ctx.getGrandExchange().isBuyOpen() || ctx.getGrandExchange().isSellOpen();
    }

    public boolean openGE() {
        int counter = 0;

        while (!isGEOpen() && counter < 20) {
            logScript("Opening GE");
            sleepUntil(() -> ctx.getGrandExchange().open(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> ctx.getGrandExchange().isOpen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return isGEOpen();
    }

    public boolean closeGE() {
        int counter = 0;

        while (isGEOpen() && counter < 20) {
            logScript("Closing GE");
            sleepUntil(() -> ctx.getGrandExchange().close(), Constants.MAX_SLEEP_UNTIL);
            sleepUntil(() -> !ctx.getGrandExchange().isOpen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }
        return !isGEOpen();
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

    public boolean addBuyExchange(String searchString, String itemName, boolean confirm, boolean closeBuy, boolean closeGE) {

        if (!openGE()) {
            return false;
        }

        if (!openBuyScreen()) {
            return false;
        }

        WidgetChild itemWidget = null;
        Item currentItem = ctx.getGrandExchange().getCurrentChosenItem();

        int counter = 0;
        boolean objectFound = currentItem != null && Objects.equals(currentItem.getName(), itemName);
        while (!objectFound && counter < 20) {
            int counter1 = 0;
            logScript("Searching for item: " + searchString);

            // searching for the item
            while (!objectFound && itemWidget == null && counter1 < 20) {
                logScript("Getting item widget: " + searchString);
                ctx.getGrandExchange().searchItem(searchString);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                itemWidget = ctx.getGrandExchange().getItemChildInSearch(itemName);
                objectFound = currentItem != null && Objects.equals(currentItem.getName(), itemName);
                counter1++;
            }

            if (itemWidget != null) {
                // interacting with the item
                interactService.interactWithWidget(itemWidget);
            }

            currentItem = ctx.getGrandExchange().getCurrentChosenItem();
            objectFound = currentItem != null && Objects.equals(currentItem.getName(), itemName);
            counter++;
        }

        if (currentItem == null) {
            logScript("Added buy exchange for item " + itemName + " and found no item in response");
        } else if (currentItem.getName() != null && !currentItem.getName().equals(itemName)) {
            logScript("Added buy exchange for item " + itemName + " and found " + currentItem.getName());
        } else {
            logScript("Added buy exchange for item " + itemName + " and found this item!");
        }

        counter = 0;
        boolean isItemConfirmed = false;
        while (confirm && !isItemConfirmed && counter < 20) {
            ctx.getGrandExchange().confirm();
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
            isItemConfirmed = Arrays.stream(ctx.getGrandExchange().getItems()).anyMatch(i -> Objects.equals(i.getName(), itemName));
        }

        if (confirm && !isItemConfirmed) {
            return false;
        }

        if (ctx.getGrandExchange().isReadyToCollect()) {
            collect(closeGE);
        }

        if (closeBuy) {
            logScript("Going back");
            ctx.getGrandExchange().goBack();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }

        return Arrays.stream(ctx.getGrandExchange().getItems()).anyMatch(i -> Objects.equals(i.getName(), itemName));
    }

    public boolean addSellExchange(String itemName) {
        if (!openGE()) {
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

    private boolean openBuyScreen() {
        if (!openGE()) {
            return false;
        }

        int counter = 0;
        while (!ctx.getGrandExchange().isBuyOpen() && counter < 20) {
            logScript("Opening buy screen");
            ctx.getGrandExchange().openBuyScreen(ctx.getGrandExchange().getFirstOpenSlot());
            sleepUntil(() -> ctx.getGrandExchange().isBuyOpen(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return ctx.getGrandExchange().isBuyOpen();
    }

    public boolean setPriceQuantityConfirm(String itemName, Integer price, Integer amount, boolean closeGE) {
        ExchangeType type;

        if (ctx.getGrandExchange().isBuyOpen()) {
            type = ExchangeType.BUY;
        } else if (ctx.getGrandExchange().isSellOpen()) {
            type = ExchangeType.SELL;
        } else {
            return false;
        }


        logScript("Trying to add exchange [" + type.name() + "] for:" +
                "\n\t- item: " + itemName +
                "\n\t- price: " + price + " gp" +
                "\n\t- amount: " + amount);

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

        if (confirm() && closeGE) {
            closeGE();
        }

        return ctx.getGrandExchange().isBuyOpen() || ctx.getGrandExchange().isSellOpen();
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

    private boolean confirm() {
        int counter = 0;

        while ((ctx.getGrandExchange().isBuyOpen() || ctx.getGrandExchange().isSellOpen()) && counter < 20) {
            logScript("GE confirming");
            sleepUntil(() -> ctx.getGrandExchange().confirm(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return ctx.getGrandExchange().isBuyOpen() || ctx.getGrandExchange().isSellOpen();
    }

}
