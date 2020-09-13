package shared.services;

import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.wrappers.items.Item;
import shared.Constants;
import shared.enums.AntibanActionType;

import java.util.Arrays;
import java.util.Objects;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class BankService extends AbstractService {

    private static BankService instance;
    private final AntibanService antibanService;

    private BankService() {
        super();
        this.antibanService = AntibanService.getInstance();
    }

    public static BankService getInstance() {
        if (instance == null)
            instance = new BankService();
        return instance;
    }

    public boolean openBank() {
        int counter = 0;
        while (!ctx.getBank().isOpen() && counter < 20) {
            ctx.logScript("Trying to open bank");
            sleepUntil(() -> ctx.getBank().openClosest(), Constants.MAX_SLEEP_UNTIL);
            ctx.logScript("Opening bank");
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }
        return ctx.getBank().isOpen();
    }

    public void closeBank() {
        if (ctx.getBank().isOpen()) {
            ctx.logScript("Trying to close bank");
            sleepUntil(() -> ctx.getBank().close(), Constants.MAX_SLEEP_UNTIL);
            ctx.logScript("Closing bank");
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    /**
     * @param quantity null means All and -1 means AllButOne
     */
    public void withdraw(String itemName, Integer quantity, boolean closeBank, boolean noted) {
        if (openBank()) {

            if (!noted) {
                ctx.logScript("Trying to withdraw " + quantity + " of " + itemName);
            } else {
                ctx.logScript("Trying to withdraw " + quantity + " of " + itemName + " (noted)");
            }

            if (noted && ctx.getBank().getWithdrawMode() != BankMode.NOTE) {
                sleepUntil(() -> ctx.getBank().setWithdrawMode(BankMode.NOTE), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            } else if (ctx.getBank().getWithdrawMode() != BankMode.ITEM) {
                sleepUntil(() -> ctx.getBank().setWithdrawMode(BankMode.ITEM), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }

            int itemQuantity = ctx.getBank().count(itemName);

            if (itemQuantity > 0) {
                if (quantity == null) {
                    sleepUntil(() -> ctx.getBank().withdrawAll(itemName), Constants.MAX_SLEEP_UNTIL);
                } else if (quantity == -1) {
                    sleepUntil(() -> ctx.getBank().withdraw(itemName, itemQuantity - 1), Constants.MAX_SLEEP_UNTIL);
                } else {
                    sleepUntil(() -> ctx.getBank().withdraw(itemName, quantity), Constants.MAX_SLEEP_UNTIL);
                }
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }

            if (closeBank) {
                closeBank();
            }
        }

        antibanService.antibanSleep(AntibanActionType.FastPace);
    }

    public boolean bankAllExcept(boolean closeBank, String... exceptItems) {
        if (openBank()) {
            ctx.logScript("Trying to bank all except " + Arrays.toString(exceptItems));
            sleepUntil(() -> ctx.getBank().depositAllExcept(exceptItems), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            if (closeBank) {
                closeBank();
            }
        }

        for (Item item : ctx.getInventory().getCollection()) {
            for (String itemName : exceptItems) {
                if (item != null && !Objects.equals(item.getName(), itemName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean bankAll(boolean close) {
        if (openBank()) {
            ctx.logScript("Trying to bank all");
            int counter = 0;
            while (!ctx.getInventory().isEmpty() && counter < 8) {
                ctx.getBank().depositAllItems();
                antibanService.antibanSleep(AntibanActionType.FastPace);
                counter++;
            }

            if (close) {
                closeBank();
            }
        }
        return ctx.getInventory().isEmpty();
    }

    public boolean openDepositBox() {
        int counter = 0;
        while (!ctx.getDepositBox().isOpen() && counter < 20) {
            ctx.logScript("Opening deposit box");
            sleepUntil(() -> ctx.getDepositBox().openClosest(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return ctx.getDepositBox().isOpen();
    }

    public void closeDepositBox() {
        if (ctx.getDepositBox().isOpen()) {
            ctx.logScript("Closing deposit box");
            sleepUntil(() -> ctx.getDepositBox().close(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void depositAllExcept(boolean close, String... exceptItems) {
        if (openDepositBox()) {
            ctx.logScript("Depositing all except " + Arrays.toString(exceptItems));
            sleepUntil(() -> ctx.getDepositBox().depositAllExcept(exceptItems), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);

            if (close) {
                closeDepositBox();
            }
        }
    }

    public void depositAll(boolean close) {
        ctx.logScript("Depositing all");
        if (!ctx.getDepositBox().isOpen()) {
            sleepUntil(() -> ctx.getDepositBox().openClosest(), Constants.MAX_SLEEP_UNTIL);
        } else {
            sleepUntil(() -> ctx.getDepositBox().depositAllItems(), Constants.MAX_SLEEP_UNTIL);
        }
        antibanService.antibanSleep(AntibanActionType.FastPace);

        if (close) {
            sleepUntil(() -> ctx.getDepositBox().close(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }
}
