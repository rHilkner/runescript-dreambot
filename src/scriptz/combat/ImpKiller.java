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
import shared.services.AntibanService;

@ScriptManifest(author = "xpt", name = "Imp Killer ¡Karamja!", category = Category.COMBAT, version = 1.0, description = "Kills imps in Karamja and deposit the beads in Port Sarim")
public class ImpKiller extends RunescriptAbstractContext {

    private CombatService combatService;
    private BankService bankService;
    private AntibanService antibanService;
    private int loopCount = 0;

    @Override
    public void onStart() {
        super.onStart();
        setGameStyle(GameStyle.Normal);
        this.combatService = CombatService.getInstance();
        this.bankService = BankService.getInstance();
        this.antibanService = AntibanService.getInstance();
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
        if (Areas.KaramjaVolcanoWest.getArea().contains(getLocalPlayer())) {
            String[] targets = new String[]{"Imp"};
            String[] lootItems = new String[]{"Black bead", "White bead", "Yellow bead", "Blue bead", "Red bead", "Mind talisman"};
            combatService.combatLoot(targets, lootItems, Areas.KaramjaVolcanoWest.getArea(), true, false);
        } else if (sharedService.walkTo(Areas.KaramjaVolcanoWest)) {
            logScript("Going to volcano");
        } else {
            if (Areas.PortSarimToKaramja.getArea().contains(getLocalPlayer())) {
                payFare(true);
            } else if (Areas.PortSarimToKaramjaBoat.getArea().contains(getLocalPlayer())) {
                crossNearestPlank();
            } else {
                logScript("Going to get boat to Karamja");
                sharedService.walkTo(Areas.PortSarimToKaramja);
            }
        }
    }

    private void goBank() {
        if (Areas.PortSarimDepositBox.getArea().contains(getLocalPlayer())) {
            bankService.depositAllExcept(true, "Air rune", "Mind rune", "Coins");
        } else if (sharedService.walkTo(Areas.PortSarimDepositBox)) {
            logScript("Going to deposit box");
        } else {
            if (Areas.KaramjaToPortSarim.getArea().contains(getLocalPlayer())) {
                payFare(false);
            } else if (Areas.KaramjaToPortSarimBoat.getArea().contains(getLocalPlayer())) {
                crossNearestPlank();
            } else {
                logScript("Going to get boat to Port Sarim");
                sharedService.walkTo(Areas.KaramjaToPortSarim);
            }
        }
    }

    private void crossNearestPlank() {
        logScript("Crossing nearest plank");
        GameObject plank = getGameObjects().closest(p -> p != null && p.hasAction("Cross"));
        if (plank.exists()) {
            plank.interact("Cross");
            sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(AntibanActionType.FastPace);
        }
    }

    private void payFare(boolean toKaramja) {
        NPC sailor;
        String[] interactionOptions;
        if (toKaramja) {
            logScript("Paying fare to Karamja");
            sailor = getNpcs().closest("Seaman Lorris", "Seaman Thresnor", "Captain Tobias");
            interactionOptions = new String[]{"Yes please."};
        } else {
            logScript("Paying fare to Port Sarim");
            sailor = getNpcs().closest("Customs officer");
            interactionOptions = new String[]{"Can I journey on this ship?", "Search away, I have nothing to hide.", "Ok."};
        }

        if (sailor == null) {
            crossNearestPlank();
        } else {
            if (getDialogues().inDialogue()) {
                if (getDialogues().canContinue()) {
                    getDialogues().spaceToContinue();
                } else {
                    for (String interactionOption : interactionOptions) {
                        getDialogues().chooseOption(interactionOption);
                    }
                }
            } else {
                sailor.interact("Pay-fare");
                sleepUntil(() -> getDialogues().inDialogue(), 8000);
            }
        }
        antibanService.antibanSleep(AntibanActionType.FastPace);
    }

}
