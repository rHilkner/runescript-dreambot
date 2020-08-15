package scriptz.combat;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.AntibanService;
import shared.services.CombatService;

@ScriptManifest(author = "xpt", name = "Chicken Killer", category = Category.COMBAT, version = 1.0, description = "Kills chickens and bury their bonesId")
public class ChickenKiller extends RunescriptAbstractContext {
    
    private CombatService combatService;
    private AntibanService antibanService;

    String[] targets = new String[]{"Chicken"};
    String[] loots = new String[]{Items.Feather.name, Items.IronArrow.name};

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

        if (getInventory().contains(i -> i != null && Items.IronArrow.name.equals(i.getName()))) {
            Item ironArrow = getInventory().get(Items.IronArrow.name);
            ironArrow.interact();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }


        if (Areas.FaladorSouthChickens.getArea().contains(ctx.getLocalPlayer())) {
            combatService.combatLoot(targets,
                    loots,
                    Areas.FaladorSouthChickens.getArea(),
                    null, true, false);
        } else {
            sharedService.walkTo(Areas.FaladorSouthChickens);
        }

        return 0;
    }
}