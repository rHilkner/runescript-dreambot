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
