package scriptz;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Edgeville willow woodcutter", author = "XpT", version = 1.0)
public class EdgevilleWillowCutter extends WoodcuttingAbstractScript {

    @Override
    public void onStart() {
        logScript("Starting EdgevilleTreeCutter - all creditz to Xpt º*º");
        super.onStart("Tree", Areas.EdgevilleWillowTrees, Areas.EdgevilleBank);
        logScript("Starting EdgevilleTreeCutter - all creditz to Xpt º*º");
    }

    @Override
    public int onLoop() {
        super.onLoop();
        return 0;
    }

    @Override
    public void onPaint(Graphics graphics) {

    }
}
