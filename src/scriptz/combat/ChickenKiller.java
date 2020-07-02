package scriptz.combat;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.services.CombatService;
import shared.services.XptZenAntibanService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ScriptManifest(author = "xpt", name = "Chicken Killer", category = Category.COMBAT, version = 1.0, description = "Kills chickens and bury their bonesId")
public class ChickenKiller extends RunescriptAbstractContext {
    
    private CombatService combatService;
    private XptZenAntibanService antibanService;


    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.antibanService = XptZenAntibanService.getInstance();
        antibanService.setSkillsToHover(Skill.ATTACK, Skill.STRENGTH);
        logScript("Chicken Killer starting - creditz to XpT ø*ø");
    }

    @Override
    public int onLoop() {

        super.onLoop();
        logScript("Loop of killing chicken");

        List<String> targets = Collections.singletonList("Chicken");
        List<String> lootItems = Arrays.asList("Feathers", "Bones");

        if (Areas.FaladorSouthChickens.getArea().contains(ctx.getLocalPlayer())) {
            combatService.combatLoot((String[]) targets.toArray(), (String[]) lootItems.toArray(), false, true);
        } else {
            sharedService.walkTo(Areas.FaladorSouthChickens);
        }

        return 0;
    }
}