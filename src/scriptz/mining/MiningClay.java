package scriptz.mining;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.enums.Areas;
import shared.enums.RockTypes;

@ScriptManifest(category = Category.MINING, name = "Varrock south mining clay", author = "XpT", version = 1.0)
public class MiningClay extends AbstractMiner {

    @Override
    public void onStart() {
        logScript("Varrock south mining clay - all creditz to Xpt º*º");
        super.onStart(RockTypes.Clay, Areas.VarrockSouthWestMineClay, Areas.VarrockWestBank);
    }

    @Override
    public int onLoop() {
        super.onLoop();
        return 0;
    }

}
