package scriptz;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import shared.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.CombatService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ScriptManifest(author = "xpt", name = "Chicken Killer", category = Category.COMBAT, version = 1.0, description = "Kills chickens and bury their bonesId")
public class ChickenKiller extends RunescriptAbstractContext {
    
    private CombatService combatService;

    @Override
    public void onStart() {
        super.onStart();
        combatService = CombatService.getInstance();
        logScript("Chicken Killer starting - creditz to XpT ø*ø");
    }

    @Override
    public int onLoop() {

        super.onLoop();

        List<String> targets = Collections.singletonList("Chicken");
        List<Items> lootItems = Arrays.asList(Items.FEATHERS, Items.BONES);
        combatService.combatLoot(targets, lootItems, Areas.FaladorSouthChickens, true);

        return 0;
    }
}