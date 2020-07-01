package scriptz.combat;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.CombatService;
import shared.services.XptZenAntibanService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ScriptManifest(author = "xpt", name = "Imp Killer ¡Karamja!", category = Category.COMBAT, version = 1.0, description = "Kills imps in Karamja and deposit the beads in Port Sarim")
public class ImpKiller extends RunescriptAbstractContext {

    private CombatService combatService;
    private BankService bankService;
    private XptZenAntibanService antibanService;
    private int loopCount = 0;

    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.bankService = BankService.getInstance();
        this.antibanService = XptZenAntibanService.getInstance();
        antibanService.setSkillsToHover(Skill.MAGIC);
        logScript("Imp Killer ¡Karamja! - creditz to XpT ø*ø");
    }

    @Override
    public int onLoop() {

        super.onLoop();
        logScript("Loop " + loopCount++ + " of killing imps");

        if (getInventory().isFull()) {
            if (Areas.PortSarimDepositBox.getArea().contains(getLocalPlayer())) {
                bankService.bankAllExcept("runes");
            } else if (ctx.getMap().canReach(Areas.PortSarimDepositBox.getArea().getRandomTile())) {
                sharedService.walkTo(Areas.PortSarimDepositBox);
            } else {
                if (Areas.KaramjaToPortSarim.getArea().contains(getLocalPlayer())) {
                    crossNearestPlank();
                } else {
                    sharedService.walkTo(Areas.KaramjaToPortSarim);
                }
            }
        } else if (Areas.KaramjaVolcano.getArea().contains(getLocalPlayer())) {
            List<String> targets = Collections.singletonList("Imp");
            List<Items> lootItems = Arrays.asList(Items.BLACK_BEAD, Items.WHITE_BEAD, Items.RED_BEAD, Items.BLUE_BEAD, Items.YELLOW_BEAD);
            combatService.combatLoot(targets, lootItems, Areas.KaramjaVolcano, false);
        } else if (ctx.getMap().canReach(Areas.KaramjaVolcano.getArea().getRandomTile())) {
            sharedService.walkTo(Areas.KaramjaVolcano);
        } else {
            if (Areas.PortSarimToKaramja.getArea().contains(getLocalPlayer())) {
                crossNearestPlank();
            } else {
                sharedService.walkTo(Areas.PortSarimToKaramja);
            }
        }

        return 0;
    }

    private void crossNearestPlank() {
        GameObject plank = getGameObjects().closest(p -> p != null && p.hasAction("Cross"));
        if (plank.exists()) {
            plank.interact("Cross");
            sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
        }
    }

}
