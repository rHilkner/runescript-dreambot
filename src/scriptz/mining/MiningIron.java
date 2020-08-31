package scriptz.mining;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;
import shared.enums.RockTypes;

@ScriptManifest(category = Category.MINING, name = "Varrock South Mining Iron", author = "XpT", version = 1.0)
public class MiningIron extends AbstractMiner {

    @Override
    public void onStart() {
        logScript("Varrock South Mining Iron - all creditz to Xpt º*º");
        super.onStart(RockTypes.Iron, Areas.VarrockSouthWestMineIron, Areas.VarrockWestBank);
    }

    @Override
    public int onLoop() {
        super.onLoop();
        return 0;
    }

}
