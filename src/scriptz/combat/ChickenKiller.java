package scriptz.combat;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.services.AntibanService;
import shared.services.CombatService;

@ScriptManifest(author = "xpt", name = "Chicken Killer", category = Category.COMBAT, version = 1.0, description = "Kills chickens and bury their bonesId")
public class ChickenKiller extends RunescriptAbstractContext {
    
    private CombatService combatService;
    private AntibanService antibanService;


    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.antibanService = AntibanService.getInstance();
        antibanService.setSkillsToHover(Skill.ATTACK, Skill.STRENGTH);
        logScript("Chicken Killer starting - creditz to XpT ø*ø");
    }

    @Override
    public int onLoop() {

        super.onLoop();
        logScript("Loop of killing chicken");

        String[] targets = new String[]{"Chicken"};
        String[] lootItems = new String[]{"Feathers", "Bones"};

        if (Areas.FaladorSouthChickens.getArea().contains(ctx.getLocalPlayer())) {
            combatService.combatLoot(targets, lootItems, Areas.FaladorSouthChickens.getArea(), false, true);
        } else {
            sharedService.walkTo(Areas.FaladorSouthChickens);
        }

        return 0;
    }
}