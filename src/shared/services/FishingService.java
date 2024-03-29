package shared.services;

import org.dreambot.api.wrappers.interactive.NPC;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.FishingType;

public class FishingService extends AbstractService {

    private static FishingService instance;

    private final AntibanService antibanService;

    private FishingService() {
        super();
        this.antibanService = AntibanService.getInstance();
    }

    public static FishingService getInstance() {
        if (instance == null)
            instance = new FishingService();
        return instance;
    }

    public void fish(FishingType fish) {
        NPC fishNpc = ctx.getNpcs().closest(fish.getFishingSpot());
        if (fishNpc != null && fishNpc.interact(fish.getInteractionType())) {
            ctx.logScript("Fishing with " + fish.getEquipmentName());
            antibanService.antibanSleep(AntibanActionType.SlowPace);

            int counter = 0;
            while (ctx.getLocalPlayer().isAnimating() || counter < 4) {
                Util.sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                counter = ctx.getLocalPlayer().isAnimating() ? 0 : counter + 1;
            }
        }
    }

}
