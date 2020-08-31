package scriptz.mining;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;
import shared.enums.RockTypes;

@ScriptManifest(category = Category.MINING, name = "Varrock South Mining Tin", author = "XpT", version = 1.0)
public class MiningTin extends AbstractMiner {

    @Override
    public void onStart() {
        logScript("Varrock South Mining Tin - all creditz to Xpt º*º");
        super.onStart(RockTypes.Tin, Areas.VarrockSouthWestMineTin, Areas.VarrockWestBank);
    }

    @Override
    public int onLoop() {
        super.onLoop();
        return 0;
    }

}
