package scriptz;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import shared.enums.Areas;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Basic Woodcutter", author = "XpT", version = 1.0)
public class BasicWoodcutter extends WoodcuttingAbstractScript {

    final int LOOP_FLOOR_MILLIS = 600;
    final int LOOP_CEILING_MILLIS = 1500;

    @Override
    public void onStart() {
        logScript("Starting BasicWoodcutter - all creditz to Xpt º*º");
        setBankArea(Areas.EDGEVILLE_BANK);
        setTreeArea(Areas.EDGEVILLE_TREES);
        setTreeName("Tree");
    }

    @Override
    public int onLoop() {
        super.onLoop();
        return 0;
    }

    @Override
    public void onExit() {
        log("Bye");
    }

    @Override
    public void onPaint(Graphics graphics) {

    }
}
