package shared.services;

import org.dreambot.api.wrappers.interactive.NPC;
import shared.Constants;
import shared.enums.ActionType;
import shared.enums.FishingType;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class FishingService extends AbstractService {

    private static FishingService instance;

    private XptZenAntibanService antibanService;

    private FishingService() {
        super();
        this.antibanService = XptZenAntibanService.getInstance();
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
            antibanService.antibanSleep(ActionType.SlowPace);
            if (!ctx.getLocalPlayer().isAnimating()) {
                sleepUntil(() -> !ctx.getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(ActionType.FastPace);
            }
        }
    }

}
