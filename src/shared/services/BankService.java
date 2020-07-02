package shared.services;

import shared.Util;
import shared.enums.AntibanActionType;

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

    public void bankItems(Integer[] itemIDs) {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().openClosest();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        } else {
            ctx.getBank().depositAll(item -> Util.isElementInArray(item.getID(), itemIDs));
            antibanService.antibanSleep(AntibanActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void bankAllExcept(String... exceptItems) {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().open(ctx.getBank().getClosestBankLocation());
            antibanService.antibanSleep(AntibanActionType.FastPace);
        } else {
            ctx.getBank().depositAllExcept(exceptItems);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void bankAll() {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().open(ctx.getBank().getClosestBankLocation());
            antibanService.antibanSleep(AntibanActionType.FastPace);
        } else {
            ctx.getBank().depositAllItems();
            antibanService.antibanSleep(AntibanActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    public void depositAllExcept(String... exceptItems) {
        if (ctx.getDepositBox().isOpen()) {
            ctx.getDepositBox().openClosest();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        } else {
            ctx.getDepositBox().depositAllExcept(exceptItems);
            antibanService.antibanSleep(AntibanActionType.FastPace);
            ctx.getDepositBox().close();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }
}
