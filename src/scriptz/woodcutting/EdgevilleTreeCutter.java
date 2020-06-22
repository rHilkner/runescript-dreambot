package scriptz.woodcutting;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.Trees;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Basic Woodcutter", author = "XpT", version = 1.0)
public class EdgevilleTreeCutter extends WoodcuttingAbstractScript {

    @Override
    public void onStart() {
        RunescriptAbstractContext.logScript("Starting EdgevilleTreeCutter - all creditz to Xpt º*º");
        super.onStart(Trees.Tree, Areas.DraynorVillageTrees, Areas.DraynorVillageBank);
        RunescriptAbstractContext.logScript("Starting EdgevilleTreeCutter - all creditz to Xpt º*º");
    }

    @Override
    public int onLoop() {
        RunescriptAbstractContext.logScript("loop1");
        super.onLoop();
        RunescriptAbstractContext.logScript("loopend1");
        return 0;
    }

    @Override
    public void onPaint(Graphics graphics) {

    }
}
