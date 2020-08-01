package scriptz.firemaking;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.Items;

@ScriptManifest(category = Category.WOODCUTTING, name = "GE Firemaker", author = "xpt º*º", version = 1.0)
public class GEFiremaker extends AbstractFiremaker {

    @Override
    public void onStart() {
        super.onStart(Items.WillowLogs, Areas.GrandExchangeNorthFiremake);
        RunescriptAbstractContext.logScript("Starting GE Firemaker - all creditz to Xpt º*º");
    }

    @Override
    public int onLoop() {
        super.onLoop();
        return 0;
    }

}
