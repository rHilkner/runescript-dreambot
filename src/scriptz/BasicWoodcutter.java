package scriptz;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Basic Woodcutter", author = "XpT", version = 1.0)
public class BasicWoodcutter extends WoodcuttingAbstractScript {

    @Override
    public void onStart() {
        logScript("Starting BasicWoodcutter - all creditz to Xpt º*º");
        super.onStart("Tree", Areas.EDGEVILLE_TREES, Areas.EDGEVILLE_BANK);
        logScript("Starting BasicWoodcutter - all creditz to Xpt º*º");
    }

    @Override
    public int onLoop() {
        logScript("loop1");
        super.onLoop();
        logScript("loopend1");
        return 0;
    }

    @Override
    public void onPaint(Graphics graphics) {

    }
}
