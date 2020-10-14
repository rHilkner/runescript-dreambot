package scriptz.combat;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.Items;
import shared.services.AntibanService;
import shared.services.BankService;
import shared.services.CombatService;
import shared.services.InteractService;

@ScriptManifest(author = "xpt", name = "Animated Armour Killer", category = Category.COMBAT, version = 1.0, description = "Animated Armour Killer")
public class AnimatedArmours extends RunescriptAbstractContext {

    enum State { EAT, KEEP_KILLIN, GET_LOOT, ANIMATE_ARMOUR, BANK, STOP }

    private String FOOD_NAME = Items.Monkfish.name;
    private Area DOOR_INSIDE = new Area(2854, 3544, 2855, 3545, 0);
    private Area DOOR_OUTSIDE = new Area(2854, 3546, 2855, 3547, 0);

    private String[] FULL_ARMOUR = {
            Items.BlackPlatebody.name,
            Items.BlackPlatelegs.name,
            Items.BlackFullHelm.name
    };

    private String[] LOOTS = {
            Items.BlackPlatebody.name,
            Items.BlackPlatelegs.name,
            Items.BlackFullHelm.name,
            Items.WarriorGuildToken.name
    };

    private CombatService combatService;
    private AntibanService antibanService;
    private InteractService interactService;
    private BankService bankService;

    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.antibanService = AntibanService.getInstance();
        this.interactService = InteractService.getInstance();
        this.bankService = BankService.getInstance();
        antibanService.setSkillsToHover(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE);
        logScript("Armour Animation Killer starting - creditz to XpT ø*ø");
    }

    private State getState() {

        if (getLocalPlayer().getHealthPercent() < 60) {
            return State.EAT;
        }

        if (getLocalPlayer().isInCombat()) {
            return State.KEEP_KILLIN;
        }

        if (!getGroundItems().all(LOOTS).isEmpty()) {
            return State.GET_LOOT;
        }

        if (!getInventory().contains(FULL_ARMOUR)) {
            return State.STOP;
        }

        boolean playerHasEdibles = getInventory().contains(i -> i != null && i.hasAction("Eat"));
        if (!playerHasEdibles) {
            return State.BANK;
        }

        return State.ANIMATE_ARMOUR;
    }

    @Override
    public int onLoop() {

        super.onLoop();
        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {
            case EAT:
                // eat twice
                sharedService.eatAnything();
                break;

            case KEEP_KILLIN:
                Util.sleepUntil(() -> !getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
                Util.sleepUntil(() -> !getGroundItems().all(LOOTS).isEmpty(), Constants.MAX_SLEEP_UNTIL);
                break;

            case GET_LOOT:
                for (String loot : LOOTS) {
                    interactService.takeClosestGroundItem(loot);
                }
                break;

            case ANIMATE_ARMOUR:
                interactService.interactWithGameObject("Magical Animator");
                Util.sleepUntil(() -> getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
                break;

            case BANK:
                sharedService.walkTo(DOOR_INSIDE);
                interactService.interactWithGameObject("Door");
                Util.sleepUntil(() -> !getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                bankService.withdraw(FOOD_NAME, null, true, false, false);
                sharedService.walkTo(DOOR_OUTSIDE);
                interactService.interactWithGameObject("Door");
                Util.sleepUntil(() -> !getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                break;

            case STOP:
                sleep(Calculations.random(10000, 12000));
                stop();
                break;
        }

        return 0;
    }

}