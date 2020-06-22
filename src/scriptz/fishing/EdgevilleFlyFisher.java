package scriptz.fishing;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;
import shared.enums.FishingType;

import java.awt.*;

@ScriptManifest(category = Category.WOODCUTTING, name = "Barbarian Village fly fisher", author = "XpT", version = 1.0)
public class EdgevilleFlyFisher extends FishingAbstractScript {

    @Override
    public void onStart() {
        logScript("Edgeville fly fisher - all creditz to Xpt º*º");
        super.onStart(FishingType.FlyFishingRod, Areas.BarbarianVillageFlyFishing, Areas.EdgevilleBank);
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
