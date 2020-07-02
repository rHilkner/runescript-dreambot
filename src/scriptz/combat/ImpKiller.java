package scriptz.combat;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.GameStyle;
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
        setGameStyle(GameStyle.Normal);
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
            goBank();
        } else {
            goKillImps();
        }

        return 0;
    }

    private void goKillImps() {
        if (Areas.KaramjaVolcano.getArea().contains(getLocalPlayer())) {
            List<String> targets = Collections.singletonList("Imp");
            List<String> lootItems = Arrays.asList("Black bead", "White bead", "Yellow bead", "Blue bead", "Red");
            combatService.combatLoot((String[]) targets.toArray(), (String[]) lootItems.toArray(), true, false);
        } else if (ctx.getMap().canReach(Areas.KaramjaVolcano.getArea().getRandomTile())) {
            sharedService.walkTo(Areas.KaramjaVolcano);
        } else {
            if (Areas.PortSarimToKaramja.getArea().contains(getLocalPlayer())) {
                crossNearestPlank();
            } else {
                sharedService.walkTo(Areas.PortSarimToKaramja);
            }
        }
    }

    private void goBank() {
        if (Areas.PortSarimDepositBox.getArea().contains(getLocalPlayer())) {
            bankService.depositAllExcept("Air rune", "Mind rune", "Coins");
        } else if (ctx.getMap().canReach(Areas.PortSarimDepositBox.getArea().getRandomTile())) {
            sharedService.walkTo(Areas.PortSarimDepositBox);
        } else {
            if (Areas.KaramjaToPortSarim.getArea().contains(getLocalPlayer())) {
                payFare(true);
            } else {
                sharedService.walkTo(Areas.KaramjaToPortSarim);
            }
        }
    }

    private void crossNearestPlank() {
        GameObject plank = getGameObjects().closest(p -> p != null && p.hasAction("Cross"));
        if (plank.exists()) {
            plank.interact("Cross");
            sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    private void payFare(boolean toKaramja) {
        NPC sailor;
        String interactionOption;
        if (toKaramja) {
            sailor = getNpcs().closest("Seaman Lorris", "Seaman Thresnor", "Captain Tobias");
            interactionOption = "Yes, please";
        } else {
            sailor = getNpcs().closest("Customs officer");
            interactionOption = "Can I journey on this ship?";
        }

        if (sailor == null) {
            crossNearestPlank();
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }

        if (Areas.PortSarimToKaramja.getArea().contains(getLocalPlayer()) && sailor != null) {
            if (getDialogues().inDialogue()) {
                if (getDialogues().canContinue()) {
                    getDialogues().spaceToContinue();
                } else {
                    getDialogues().chooseOption(interactionOption);
                }
                antibanService.antibanSleep(AntibanActionType.FastPace);
            } else {
                NPC sailor2 = getNpcs().closest("Customs officer");
                sailor2.interact("Pay-fare");
                sleepUntil(() -> getDialogues().inDialogue(), 8000);
                antibanService.antibanSleep(AntibanActionType.FastPace);
            }
        }
    }

}
