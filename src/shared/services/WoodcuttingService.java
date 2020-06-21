package shared.services;

import org.dreambot.api.wrappers.interactive.GameObject;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class WoodcuttingService extends AbstractService {

    private static WoodcuttingService instance;

    private WoodcuttingService() {
        super();
    }

    public static WoodcuttingService getInstance() {
        if (instance == null)
            instance = new WoodcuttingService();
        return instance;
    }

    public void chopTree(String nameOfTree) {
        GameObject tree = ctx.getGameObjects().closest(gameObject -> gameObject != null && gameObject.getName().equals(nameOfTree));
        if (tree != null && tree.interact("Chop down")) {
            int countLog = ctx.getInventory().count("Logs");
            sleepUntil(() -> ctx.getInventory().count("Logs") > countLog, 12000);
        }
    }

}
