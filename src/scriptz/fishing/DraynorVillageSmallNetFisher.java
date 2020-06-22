package scriptz.fishing;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;
import shared.enums.FishingType;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Draynor Village small-net fisher", author = "XpT", version = 1.0)
public class DraynorVillageSmallNetFisher extends FishingAbstractScript {

    @Override
    public void onStart() {
        logScript("Draynor Village small-net fisher - all creditz to Xpt º*º");
        super.onStart(FishingType.SmallNet, Areas.DraynorVillageFishingSpots, Areas.DraynorVillageBank);
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
