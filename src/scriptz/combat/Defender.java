package scriptz.combat;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Items;
import shared.services.AntibanService;
import shared.services.CombatService;
import shared.services.InteractService;
import shared.services.InventoryService;

import java.util.Objects;

@ScriptManifest(author = "xpt", name = "Get That Defender", category = Category.COMBAT, version = 1.0, description = "Get That Defender")
public class Defender extends RunescriptAbstractContext {

    enum State { EAT, START_KILLIN, KEEP_KILLIN, GET_DEFENDER, TALK_TO_KAMFREENA, STOP }

    private String nextDefender = null;

    private String CYCLOPS = "Cyclops";
    private String[] DEFENDERS = {
            Items.BronzeDefender.name,
            Items.IronDefender.name,
            Items.SteelDefender.name,
            Items.BlackDefender.name,
            Items.MithrilDefender.name,
            Items.AdamantDefender.name,
            Items.RuneDefender.name,
            Items.DragonDefender.name
    };

    private String KAMFREENA = "Kamfreena";
    private String[] DIALOGUE_OPTIONS = {};

    private Area THIRD_FLOOR_OUTSIDE = new Area(2838, 3527, 2846, 3542, 2);
    private Area THIRD_FLOOR_DOOR_INSIDE = new Area(2847, 3528, 2852, 3542, 2);

    private CombatService combatService;
    private AntibanService antibanService;
    private InteractService interactService;
    private InventoryService inventoryService;

    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.antibanService = AntibanService.getInstance();
        this.interactService = InteractService.getInstance();
        this.inventoryService = InventoryService.getInstance();
        this.antibanService.setSkillsToHover(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE);

        this.nextDefender = getNextDefender();

        logScript("Defender script starting - creditz to XpT ø*ø");
    }

    private State getState() {

        if (getLocalPlayer().getHealthPercent() < 70) {
            boolean playerHasEdibles = getInventory().contains(i -> i != null && i.hasAction("Eat"));
            if (!playerHasEdibles) {
                return State.STOP;
            }
            return State.EAT;
        }

        if (getLocalPlayer().isInCombat() || getLocalPlayer().isInteractedWith()) {
            return State.KEEP_KILLIN;
        }

        if (!getGroundItems().all(DEFENDERS).isEmpty()) {
//            return State.GET_DEFENDER;
            return State.STOP;
        }



        if (getInventory().contains(nextDefender) || getEquipment().contains(nextDefender) || THIRD_FLOOR_OUTSIDE.contains(getLocalPlayer())) {
            return State.TALK_TO_KAMFREENA;
        }

        return State.START_KILLIN;
    }

    @Override
    public int onLoop() {

        super.onLoop();
        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {
            case EAT:
                sharedService.eatAnything();
                break;

            case START_KILLIN:
                if (!getInventory().isFull()) {
                    interactService.takeClosestGroundItem(Items.BigBones.name);
                    inventoryService.buryBones(Items.BigBones.name);
                }
                if (!getLocalPlayer().isInCombat()) {
                    combatService.attackNearest(CYCLOPS);
                    Util.sleepUntil(() -> getLocalPlayer().isInCombat(), Calculations.random(3000, 4000));
                }
                break;

            case KEEP_KILLIN:
                Util.sleepUntil(() -> !getLocalPlayer().isInCombat() && !getLocalPlayer().isInteractedWith(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);
                break;

            case GET_DEFENDER:
                for (String loot : DEFENDERS) {
                    interactService.takeClosestGroundItem(loot);
                }
                break;

            case TALK_TO_KAMFREENA:

//                if (!THIRD_FLOOR_OUTSIDE.contains(getLocalPlayer())) {
//                    sharedService.walkTo(THIRD_FLOOR_DOOR_INSIDE);
                    interactService.interactWithGameObject("Door", "Open");
                    Util.sleepUntil(() -> !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
//                }
//
//                if (getCurrentDefender() != null) {
//                    interactService.useItemOnNpc(getCurrentDefender(), KAMFREENA);
//                    Util.sleepUntil(() -> getDialogues().inDialogue(), Constants.MAX_SLEEP_UNTIL);
//                    while (getDialogues().inDialogue()) {
//                        getKeyboard().type(" ");
//                        Util.sleepUntil(() -> !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
//                        antibanService.antibanSleep(AntibanActionType.SlowPace);
//                    }
//                }
//
//                nextDefender = getNextDefender();
//                logScript("Next defender is: " + nextDefender);
//                logScript("Going inside to kill some " + CYCLOPS);
//
//                interactService.interactWithGameObject("Door", "Open");
//                Util.sleepUntil(() -> !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
//                while (getDialogues().inDialogue()) {
//                    getKeyboard().type(" ");
//                    antibanService.antibanSleep(AntibanActionType.SlowPace);
//                }
//                Util.sleepUntil(() -> !THIRD_FLOOR_OUTSIDE.contains(getLocalPlayer()), Constants.MAX_SLEEP_UNTIL);
                break;

            case STOP:
//                sharedService.walkTo(THIRD_FLOOR_DOOR_INSIDE);
                interactService.interactWithGameObject("Door", "Open");
                sleep(Calculations.random(10000, 12000));
                stop();
                break;
        }

        return 0;
    }

    public String getCurrentDefender() {
        if (getInventory().contains(Items.DragonDefender.name) || getEquipment().contains(Items.DragonDefender.name)) {
            return Items.DragonDefender.name;
        } else if (getInventory().contains(Items.RuneDefender.name) || getEquipment().contains(Items.RuneDefender.name)) {
            return Items.RuneDefender.name;
        } else if (getInventory().contains(Items.AdamantDefender.name) || getEquipment().contains(Items.AdamantDefender.name)) {
            return Items.AdamantDefender.name;
        } else if (getInventory().contains(Items.MithrilDefender.name) || getEquipment().contains(Items.MithrilDefender.name)) {
            return Items.MithrilDefender.name;
        } else if (getInventory().contains(Items.BlackDefender.name) || getEquipment().contains(Items.BlackDefender.name)) {
            return Items.BlackDefender.name;
        } else if (getInventory().contains(Items.SteelDefender.name) || getEquipment().contains(Items.SteelDefender.name)) {
            return Items.SteelDefender.name;
        } else if (getInventory().contains(Items.IronDefender.name) || getEquipment().contains(Items.IronDefender.name)) {
            return Items.IronDefender.name;
        } else if (getInventory().contains(Items.BronzeDefender.name) || getEquipment().contains(Items.BronzeDefender.name)) {
            return Items.BronzeDefender.name;
        } else {
            return null;
        }
    }

    public String getNextDefender() {

        String currentDefender = getCurrentDefender();

        if (currentDefender == null) {
            return Items.BronzeDefender.name;
        } else if (Objects.equals(currentDefender, Items.BronzeDefender.name)) {
            return Items.IronDefender.name;
        } else if (Objects.equals(currentDefender, Items.IronDefender.name)) {
            return Items.SteelDefender.name;
        } else if (Objects.equals(currentDefender, Items.SteelDefender.name)) {
            return Items.BlackDefender.name;
        } else if (Objects.equals(currentDefender, Items.BlackDefender.name)) {
            return Items.MithrilDefender.name;
        } else if (Objects.equals(currentDefender, Items.MithrilDefender.name)) {
            return Items.AdamantDefender.name;
        } else if (Objects.equals(currentDefender, Items.AdamantDefender.name)) {
            return Items.RuneDefender.name;
        } else if (Objects.equals(currentDefender, Items.RuneDefender.name)) {
            return null;
        } else if (Objects.equals(currentDefender, Items.DragonDefender.name)) {
            return null;
        }

        return null;
    }

}