package scriptz;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;
import shared.enums.Trees;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Edgeville willow woodcutter", author = "XpT", version = 1.0)
public class EdgevilleWillowCutter extends WoodcuttingAbstractScript {

    @Override
    public void onStart() {
        logScript("Edgeville willow woodcutter - all creditz to Xpt º*º");
        super.onStart(Trees.Willow, Areas.EdgevilleWillowTrees, Areas.EdgevilleBank);
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
