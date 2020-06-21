package shared.services;

import org.dreambot.api.wrappers.interactive.NPC;
import shared.Util;
import shared.enums.ActionType;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class BankService extends AbstractService {

    private static BankService instance;

    private BankService() {
        super();
    }

    public static BankService getInstance() {
        if (instance == null)
            instance = new BankService();
        return instance;
    }

    public void bankItems(Integer[] itemIDs) {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().open(ctx.getBank().getClosestBankLocation());
            antibanService.antibanSleep(ActionType.SlowPace);
        } else {
            ctx.getBank().depositAll(item -> Util.isElementInList(item.getID(), itemIDs));
            antibanService.antibanSleep(ActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(ActionType.FastPace);
        }
    }

    public void bankAllExcept(String exceptItem) {
        NPC banker = ctx.getNpcs().closest(npc -> npc != null && npc.hasAction("Bank"));
        if (banker == null || !banker.interact("Bank")) {
            return;
        }

        if (sleepUntil(() -> ctx.getBank().isOpen(), 9000)) {
            if (ctx.getBank().depositAllExcept(item -> item != null && item.getName().contains(exceptItem))) {
                if (sleepUntil(() -> !ctx.getInventory().isFull(), 8000)) {
                    if (ctx.getBank().close()) {
                        sleepUntil(() -> !ctx.getBank().isOpen(), 8000);
                    }
                }
            }
        }
    }

    public void bankAll() {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().open(ctx.getBank().getClosestBankLocation());
            antibanService.antibanSleep(ActionType.SlowPace);
        } else {
            ctx.getBank().depositAllItems();
            antibanService.antibanSleep(ActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(ActionType.FastPace);
        }
    }
}
