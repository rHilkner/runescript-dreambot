package shared.services;

import shared.Util;
import shared.enums.ActionType;

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
            antibanService.antibanSleep(ActionType.FastPace);
        } else {
            ctx.getBank().depositAll(item -> Util.isElementInList(item.getID(), itemIDs));
            antibanService.antibanSleep(ActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(ActionType.FastPace);
        }
    }

    public void bankAllExcept(String... exceptItems) {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().open(ctx.getBank().getClosestBankLocation());
            antibanService.antibanSleep(ActionType.FastPace);
        } else {
            ctx.getBank().depositAllExcept(item -> {
                if (item == null) {
                    return false;
                }
                for (String exceptItem : exceptItems) {
                    if (item.getName().contains(exceptItem)) {
                        return true;
                    }
                }
                return false;
            });
            antibanService.antibanSleep(ActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(ActionType.FastPace);
        }

//        NPC banker = ctx.getNpcs().closest(npc -> npc != null && npc.hasAction("Bank"));
//        if (banker == null || !banker.interact("Bank")) {
//            return;
//        }
//
//        if (sleepUntil(() -> ctx.getBank().isOpen(), Constants.MAX_SLEEP_UNTIL)) {
//            antibanService.antibanSleep(ActionType.FastPace);
//            if (ctx.getBank().depositAllExcept(item -> item != null && item.getName().contains(exceptItem))) {
//                antibanService.antibanSleep(ActionType.FastPace);
//                if (sleepUntil(() -> !ctx.getInventory().isFull(), Constants.MAX_SLEEP_UNTIL)) {
//                    if (ctx.getBank().close()) {
//                        antibanService.antibanSleep(ActionType.FastPace);
//                        sleepUntil(() -> !ctx.getBank().isOpen(), Constants.MAX_SLEEP_UNTIL);
//                    }
//                }
//            }
//        }
//        antibanService.antibanSleep(ActionType.SlowPace);
    }

    public void bankAll() {
        if (!ctx.getBank().isOpen()) {
            ctx.getBank().open(ctx.getBank().getClosestBankLocation());
            antibanService.antibanSleep(ActionType.FastPace);
        } else {
            ctx.getBank().depositAllItems();
            antibanService.antibanSleep(ActionType.FastPace);
            ctx.getBank().close();
            antibanService.antibanSleep(ActionType.FastPace);
        }
    }
}
