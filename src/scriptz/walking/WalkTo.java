package scriptz.walking;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;

@ScriptManifest(author = "xpt", name = "Walk to...", category = Category.UTILITY, version = 1.0, description = "Walks to some locations")
public class WalkTo extends RunescriptAbstractContext {
    
    private Areas destinationArea;

    @Override
    public void onStart() {
        super.onStart();
        this.destinationArea = Areas.GrandExchange;
        logScript("Walk to... starting - creditz to XpT ø*ø");
    }

    @Override
    public int onLoop() {

        super.onLoop();
        logScript("Loop of walk to...");

        if (!destinationArea.getArea().contains(getLocalPlayer())) {
            sharedService.walkTo(destinationArea);
        }

        return 0;
    }
}