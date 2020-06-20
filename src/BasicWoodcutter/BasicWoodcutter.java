package BasicWoodcutter;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Basic Woodcutter", author = "XpT", version = 1.0)
public class BasicWoodcutter extends AbstractScript {

    final int LOOP_FLOOR_MILLIS = 600;
    final int LOOP_CEILING_MILLIS = 1500;

    static int counter = 0;

    Area bankArea = new Area(3092, 3240, 3097, 3246, 0);
    Area treeArea = new Area(3074, 3265, 3083, 3274, 0);

    @Override
    public void onStart() {
        log("Hi");
    }

    @Override
    public int onLoop() {

        counter++;

        int woodcuttingLevel = getSkills().getRealLevel(Skill.WOODCUTTING);

        if (woodcuttingLevel == 31) {
            getTabs().logout();
        }

        // Chopping trees: Time to chop some trees, our inventory isn't full. We want to fill it up.
        if (!getInventory().isFull()) {
            if (treeArea.contains(getLocalPlayer())) {
                chopTree("Tree"); //change "Tree" to the name of your tree.
            } else {
                if (getWalking().walk(treeArea.getRandomTile())) {
                    sleep(Calculations.random(3000, 5500));
                }
            }
        }

         // Banking: Time to bank our logs. Our inventory is full, we want to empty it.
        if (getInventory().isFull()) { //it is time to bank
            if (bankArea.contains(getLocalPlayer())) {
                bank();
            } else {
                if (getWalking().walk(bankArea.getRandomTile())) {
                    sleep(Calculations.random(3000, 6000));
                }
            }
        }

        return Calculations.random(LOOP_FLOOR_MILLIS, LOOP_CEILING_MILLIS);
    }

    @Override
    public void onExit() {
        log("Bye");
    }

    @Override
    public void onPaint(Graphics graphics) {

    }

    private void chopTree(String nameOfTree) {
        GameObject tree = getGameObjects().closest(gameObject -> gameObject != null && gameObject.getName().equals(nameOfTree));
        if (tree != null && tree.interact("Chop down")) {
            int countLog = getInventory().count("Logs");
            sleepUntil(() -> getInventory().count("Logs") > countLog, 12000);
        }
    }

    private void bank() {
        NPC banker = getNpcs().closest(npc -> npc != null && npc.hasAction("Bank"));
        if (banker != null && banker.interact("Bank")) {
            if (sleepUntil(() -> getBank().isOpen(), 9000)) {
                if (getBank().depositAllExcept(item -> item != null && item.getName().contains("axe"))) {
                    if (sleepUntil(() -> !getInventory().isFull(), 8000)) {
                        if (getBank().close()) {
                            sleepUntil(() -> !getBank().isOpen(), 8000);
                        }
                    }
                }
            }
        }
    }
}
