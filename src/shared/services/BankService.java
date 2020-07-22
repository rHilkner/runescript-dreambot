package shared.services;

import org.dreambot.api.methods.container.impl.bank.BankMode;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class BankService extends AbstractService {

    private static BankService instance;
    private final XptZenAntibanService antibanService;

    private BankService() {
        super();
        this.antibanService = XptZenAntibanService.getInstance();
    }

    public static BankService getInstance() {
        if (instance == null)
            instance = new BankService();
        return instance;
    }

    public void openBank() {
        if (!ctx.getBank().isOpen()) {
            sleepUntil(() -> ctx.getBank().openClosest(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void closeBank() {
        if (ctx.getBank().isOpen()) {
            sleepUntil(() -> ctx.getBank().close(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    /**
     * @param quantity null means All and -1 means AllButOne
     */
    public void withdraw(String itemName, Integer quantity, boolean closeBank, boolean noted) {
        openBank();

        if (ctx.getBank().isOpen()) {

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

    public void bankAllExcept(boolean closeBank, String... exceptItems) {
        openBank();

        if (ctx.getBank().isOpen()) {
            sleepUntil(() -> ctx.getBank().depositAllExcept(exceptItems), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            if (closeBank) {
                closeBank();
            }
        }
    }

    public void bankAll(boolean close) {
        openBank();

        if (ctx.getBank().isOpen()) {
            sleepUntil(() -> ctx.getBank().depositAllItems(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            if (close) {
                closeBank();
            }
        }
    }

    public void depositAllExcept(boolean close, String... exceptItems) {
        if (!ctx.getDepositBox().isOpen()) {
            sleepUntil(() -> ctx.getDepositBox().openClosest(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        } else {
            sleepUntil(() -> ctx.getDepositBox().depositAllExcept(exceptItems), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            sleepUntil(() -> ctx.getDepositBox().close(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }
}
