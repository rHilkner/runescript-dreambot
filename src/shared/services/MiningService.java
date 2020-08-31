package shared.services;

import org.dreambot.api.wrappers.interactive.GameObject;
import shared.enums.RockTypes;

public class MiningService extends AbstractService {

    private static MiningService instance;

    private final AntibanService antibanService;
    private final InteractService interactService;

    private MiningService() {
        super();
        this.antibanService = AntibanService.getInstance();
        this.interactService = InteractService.getInstance();
    }

    public static MiningService getInstance() {
        if (instance == null)
            instance = new MiningService();
        return instance;
    }

    public boolean mineRock(RockTypes rockType) {
        GameObject rockObject = ctx.getGameObjects().closest(o -> {
            for (int modelColor : o.getModelColors()) {
                if (modelColor == rockType.modelColor) {
                    return true;
                }
            }
            return false;
        });

        if (rockObject == null) {
            ctx.logScript("No [" + rockType.rockName + "] rocks found near by");
            return false;
        }

        return interactService.interactWithGameObject(rockObject);
    }

    public boolean mineRockWithDistanceLimit(RockTypes rockType, double maxDistance) {
        // Adding 0.01 to max distance so that it doesnt fall into any bugs that make the rock stay at a distance 1.0000001, but its actually 1.0
        double finalMaxDistance = maxDistance + 0.01;
        GameObject rockObject = ctx.getGameObjects().closest(o -> {
            if (o == null || o.getModelColors() == null || o.getModelColors().length == 0) {
                return false;
            }
            for (int modelColor : o.getModelColors()) {
                if (modelColor == rockType.modelColor && o.getTile().distance(ctx.getLocalPlayer().getTile()) <= finalMaxDistance) {
                    return true;
                }
            }
            return false;
        });

        if (rockObject == null) {
            ctx.logScript("No [" + rockType.rockName + "] rocks found near by");
            return false;
        }

        ctx.logScript("Mining rock [" + rockType.rockName + "] at tile [" + rockObject.getTile() + "]");
        return interactService.interactWithGameObject(rockObject);
    }

}
