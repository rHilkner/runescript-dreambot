package scriptz;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;
import shared.enums.FishingType;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Edgeville small-net fisher", author = "XpT", version = 1.0)
public class EdgevilleSmallNetFisher extends FishingAbstractScript {

    @Override
    public void onStart() {
        logScript("Edgeville small-net fisher - all creditz to Xpt º*º");
        super.onStart(FishingType.SmallFishingNet, Areas.DraynorVillageFishingSpots, Areas.DraynorVillageBank);
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
