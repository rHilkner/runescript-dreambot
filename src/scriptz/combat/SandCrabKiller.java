package scriptz.combat;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.AntibanService;
import shared.services.CombatService;

import java.util.Objects;

@ScriptManifest(author = "xpt", name = "Sand Crab Killer", category = Category.COMBAT, version = 1.0, description = "Sand Crab Killer")
public class SandCrabKiller extends RunescriptAbstractContext {

    private CombatService combatService;
    private AntibanService antibanService;

    private final String SAND_CRAB = "Sand Crab";
    private final Tile SAND_CRABS_TILE = new Tile(1765, 3468, 0);

    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.antibanService = AntibanService.getInstance();
        antibanService.setSkillsToHover(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE);
        logScript("Sand Crab Killer starting - creditz to XpT ø*ø");
    }

    @Override
    public int onLoop() {

        super.onLoop();
        logScript("Loop of killing sand crabs");

        if (getLocalPlayer().isInCombat()) {
            logScript("Player is in combat");
            sleepUntil(() -> !getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
            return 0;
        }

        if (!Objects.equals(ctx.getLocalPlayer().getTile(), SAND_CRABS_TILE)) {
            logScript("Walking to sand crabs");
            if (!sharedService.walkTo(SAND_CRABS_TILE)) {
                logScript("Player can't walk to sand crabs... wtf?");
                antibanService.antibanSleep(AntibanActionType.SlowPace);
                return 0;
            }
        }

        antibanService.antibanSleep(AntibanActionType.SlowPace);

        if (!getLocalPlayer().isInCombat()) {
            logScript("Attacking sand crabs");
            combatService.attackNearest(SAND_CRAB);
        }

        return 0;
    }

}