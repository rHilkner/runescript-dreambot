package shared.services;

import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.wrappers.items.Item;
import shared.Constants;
import shared.Util;
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

    public boolean openBank(boolean quickly) {
        int counter = 0;
        while (!ctx.getBank().isOpen() && counter < 8) {
            ctx.logScript("Opening bank");
            ctx.getBank().openClosest();
            if (!quickly) {
                Util.sleepUntil(() -> ctx.getBank().isOpen(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
            counter++;
        }
        return ctx.getBank().isOpen();
    }

    public void closeBank(boolean quickly) {
        if (ctx.getBank().isOpen()) {
            ctx.logScript("Closing bank");
            ctx.getBank().close();
            if (!quickly) {
                Util.sleepUntil(() -> !ctx.getBank().isOpen(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        }
    }

    /**
     * @param quantity null means All and -1 means AllButOne
     * @param quickly
     */
    public void withdraw(String itemName, Integer quantity, boolean closeBank, boolean noted, boolean quickly) {
        if (!openBank(quickly)) {
            return;
        }

        if (noted) {
            ctx.logScript("Trying to withdraw " + quantity + " of " + itemName + " (noted)");
        } else {
            ctx.logScript("Trying to withdraw " + quantity + " of " + itemName);
        }

        if (noted && ctx.getBank().getWithdrawMode() != BankMode.NOTE) {
            ctx.getBank().setWithdrawMode(BankMode.NOTE);
            if (!quickly) {
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        } else if (!noted && ctx.getBank().getWithdrawMode() != BankMode.ITEM) {
            ctx.getBank().setWithdrawMode(BankMode.ITEM);
            if (!quickly) {
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        }

        int itemQuantity = ctx.getBank().count(itemName);

        if (ctx.getBank().contains(itemName)) {
            if (quantity == null || itemQuantity < quantity) {
                ctx.getBank().withdrawAll(itemName);
            } else if (quantity == -1) {
                ctx.getBank().withdraw(itemName, itemQuantity - 1);
            } else {
                ctx.getBank().withdraw(itemName, quantity);
            }
            if (!quickly) {
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        }

        if (closeBank) {
            closeBank(quickly);
        }

    }

    public boolean bankAllExcept(boolean closeBank, String... exceptItems) {
        if (openBank(false)) {
            if (!ctx.getInventory().isEmpty()) {
                ctx.logScript("Trying to bank all except " + Arrays.toString(exceptItems));
                Util.sleepUntil(() -> ctx.getBank().depositAllExcept(exceptItems), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                if (closeBank) {
                    closeBank(false);
                }
            } else {
                ctx.logScript("No items to bank");
            }
        }

        for (Item item : ctx.getInventory().all()) {
            for (String itemName : exceptItems) {
                if (item != null && !Objects.equals(item.getName(), itemName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean bankAll(boolean close, boolean quickly) {
        if (openBank(quickly)) {
            if (!ctx.getInventory().isEmpty()) {
                ctx.logScript("Banking all items");
                ctx.getBank().depositAllItems();
            } else {
                ctx.logScript("No items to bank");
            }
            if (close) {
                closeBank(quickly);
            }
        }
        return ctx.getInventory().isEmpty();
    }

    public boolean openDepositBox() {
        int counter = 0;
        while (!ctx.getDepositBox().isOpen() && counter < 20) {
            ctx.logScript("Opening deposit box");
            Util.sleepUntil(() -> ctx.getDepositBox().openClosest(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            counter++;
        }

        return ctx.getDepositBox().isOpen();
    }

    public void closeDepositBox() {
        if (ctx.getDepositBox().isOpen()) {
            ctx.logScript("Closing deposit box");
            Util.sleepUntil(() -> ctx.getDepositBox().close(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void depositAllExcept(boolean close, String... exceptItems) {
        if (openDepositBox()) {
            ctx.logScript("Depositing all except " + Arrays.toString(exceptItems));
            Util.sleepUntil(() -> ctx.getDepositBox().depositAllExcept(exceptItems), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);

            if (close) {
                closeDepositBox();
            }
        }
    }

    public void depositAll(boolean close) {
        ctx.logScript("Depositing all");
        if (!ctx.getDepositBox().isOpen()) {
            Util.sleepUntil(() -> ctx.getDepositBox().openClosest(), Constants.MAX_SLEEP_UNTIL);
        } else {
            Util.sleepUntil(() -> ctx.getDepositBox().depositAllItems(), Constants.MAX_SLEEP_UNTIL);
        }
        antibanService.antibanSleep(AntibanActionType.FastPace);

        if (close) {
            Util.sleepUntil(() -> ctx.getDepositBox().close(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }
}
