package scriptz.woodcutting;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.Trees;

@ScriptManifest(category = Category.WOODCUTTING, name = "Basic Woodcutter", author = "XpT", version = 1.0)
public class EdgevilleTreeCutter extends AbstractWCutter {

    @Override
    public void onStart() {
        super.onStart(Trees.Tree, Areas.DraynorVillageTrees, Areas.DraynorVillageBank);
        RunescriptAbstractContext.logScript("Starting EdgevilleTreeCutter - all creditz to Xpt º*º");
    }

    @Override
    public int onLoop() {
        super.onLoop();
        return 0;
    }

}
