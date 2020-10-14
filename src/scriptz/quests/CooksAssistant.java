package scriptz.quests;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.Quest;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.GameObjects;
import shared.enums.Items;
import shared.services.InteractService;
import shared.services.SharedService;

import java.util.Random;

@ScriptManifest(
        author = "xpt",
        description = "Completes Cook's Assistant. The script will gather all the items needed if you don't already have them in your inventory." +
                "ie. Bucket, Pot. Script will complete quicker if these items area already had so for maximum efficiency this " +
                "should be ran after the completion of Tutorial Island. Script can be started from anywhere in the world at ground level.",
        category = Category.QUEST,
        version = 1.0,
        name = "Cook's Assistant Quest"
)
public class CooksAssistant extends RunescriptAbstractContext {
    public Random Rand = new Random();
    public Area cookArea = new Area(3207, 3217, 3210, 3212, 0);
    public Area wheatArea = new Area(3162, 3289, 3166, 3285, 0);
    public Area windmillArea = new Area(3165, 3300, 3168, 3302, 0);
    public Area cookBasementArea = new Area(3208, 9618, 3210, 9615);
    public Area eggArea = new Area(3227, 3300, 3230, 3298);
    public Area dairyCowArea = new Area(3254, 3278, 3259, 3273);
    public Area bucketArea = new Area(3213, 9624, 3216, 9622);
    public static final String COOK = "Cook";

    private SharedService sharedService;
    private InteractService interactService;

    @Override
    public void onStart() {
        super.onStart();
        this.sharedService = SharedService.getInstance();
        this.interactService = InteractService.getInstance();

        getTabs().openWithMouse(Tab.QUEST);
        if (getQuests().isFinished(Quest.COOKS_ASSISTANT)) {
            logScript("Quest is already completed");
            stop();
        }
    }

    @Override
    public int onLoop() {
        super.onLoop();

        logScript("Starting Cook's Assistant...");
        int counter = 0;
        while (!startCooksAssistant() && counter < 20) {
            counter++;
        }

        logScript("Getting pot...");
        counter = 0;
        while (!getPot() && counter < 20) {
            counter++;
        }
        logScript("Getting bucket...");
        counter = 0;
        while (!getBucket() && counter < 20) {
            counter++;
        }
        logScript("Getting egg...");
        counter = 0;
        while (!getEgg() && counter < 20) {
            counter++;
        }
        logScript("Getting milk...");
        counter = 0;
        while (!getMilk() && counter < 20) {
            counter++;
        }
        logScript("Getting flour...");
        counter = 0;
        while (!getFlour() && counter < 20) {
            counter++;
        }
        logScript("Finishing Quest...");
        counter = 0;
        while (!completeCooksAssistant() && counter < 20) {
            counter++;
        }
        logScript("Terminating Script");
        stop();

        return 0;
    }

    private boolean startCooksAssistant() {
        getTabs().openWithMouse(Tab.QUEST);
        if (getQuests().isStarted(Quest.COOKS_ASSISTANT)) {
            return true;
        }

        if (!sharedService.walkTo(cookArea)) {
            return false;
        }

        if (!interactService.interactClosestNpc(COOK, "Talk-to", false)) {
            return false;
        }

        Util.sleepUntil(() -> getDialogues().inDialogue(), Constants.MAX_SLEEP_UNTIL);
        String[] interactionOptions = new String[]{"What's wrong?", "I'm always happy to help a cook in distress.", "Actually I know where to find this stuff."};

        while (getDialogues().inDialogue()) {
            while (getDialogues().canContinue()) {
                getDialogues().continueDialogue();
            }

            if (!getDialogues().canContinue()) {
                for (String interactionOption : interactionOptions) {
                    getDialogues().chooseOption(interactionOption);
                }
            }
        }

        return true;
    }

    private boolean getEgg() {
        if (!getInventory().contains(Items.Egg.name)) {
            if (!sharedService.walkTo(eggArea)) {
                return false;
            }
            return interactService.takeClosestGroundItem(Items.Egg.name);
        }
        return true;
    }

    public boolean getPot() {
        if (!getInventory().contains(Items.Pot.name)) {
            if (!sharedService.walkTo(cookArea)) {
                return false;
            }
            return interactService.takeClosestGroundItem(Items.Pot.name);
        }
        return true;
    }

    public boolean getBucket() {
        if (!getInventory().contains(Items.Bucket.name) && !getInventory().contains(Items.BucketOfMilk.name)) {
            if (!sharedService.walkTo(cookArea)) {
                return false;
            }
            interactService.interactWithGameObject(GameObjects.Trapdoor.name, "Climb-down");
            antibanService.antibanSleep(AntibanActionType.SlowPace);
            
            //Walk to bucket
            while (!sharedService.walkTo(bucketArea)) {}
            interactService.takeClosestGroundItem(Items.Bucket.name);
            
            //Walk to ladder
            while (!sharedService.walkTo(cookBasementArea)) {}
            antibanService.antibanSleep(AntibanActionType.FastPace);
            climbClosestLadder(1, true);
        }
        return true;
    }

    public boolean getMilk() {
        if (!getInventory().contains(Items.BucketOfMilk.name)) {
            if (!sharedService.walkTo(dairyCowArea)) {
                return false;
            }
            if (!interactService.interactWithGameObject(GameObjects.DairyCow.name, "Milk")) {
                return false;
            }
            Util.sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
        }
        return true;
    }

    public boolean getGrain() {
        if (!getInventory().contains(Items.Grain.name)) {
            if (!sharedService.walkTo(wheatArea)) {
                return false;
            }
            Util.sleepUntil(() -> getLocalPlayer().isStandingStill(), Rand.nextInt(6000));
            while (!getInventory().contains(Items.Grain.name)) {
                if (!interactService.interactWithGameObject(GameObjects.Gate.name, "Open")) {
                    return false;
                }
                Util.sleepUntil(() -> getLocalPlayer().isStandingStill(), 1000 + Rand.nextInt(500));
                if (!interactService.interactWithGameObject(Items.Wheat.name, "Pick")) {
                    return false;
                }
            }
            //In case it has been closed
            if (!interactService.interactWithGameObject(GameObjects.Gate.name, "Open")) {
                return false;
            }
            Util.sleepUntil(() -> getLocalPlayer().isStandingStill(), 1000 + Rand.nextInt(500));
        }
        return getInventory().contains(Items.Grain.name);
    }

    public boolean getFlour() {
        if (getInventory().contains(Items.Flour.name)) {
            logScript("Flour Acquired!");
            return true;
        } else {
            if (!getInventory().contains(Items.Pot.name)) {
                if (!getPot()) {
                    return false;
                }
            } else {
                if (!getInventory().contains(Items.Grain.name)) {
                    if (!getGrain()) {
                        return false;
                    }
                }

                int counter = 0;
                while (!interactService.interactGameObjectWithInventoryItem(Items.Grain.name, GameObjects.Hopper.name, true) && counter < 20) {
                    sharedService.walkTo(windmillArea);
                    interactService.interactWithGameObject(GameObjects.LargeDoor.name, "Open");
                    if (getLocalPlayer().getZ() == 0 || getLocalPlayer().getZ() == 1) {
                        climbClosestLadder(2-getLocalPlayer().getZ(), true);
                    }
                    counter++;
                }

                if (!interactService.interactWithGameObject(GameObjects.HopperControls.name, "Operate")) {
                    return false;
                }

                Util.sleepUntil(() -> !getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                antibanService.antibanSleep(AntibanActionType.FastPace);

                if (getLocalPlayer().getZ() == 2 || getLocalPlayer().getZ() == 1) {
                    climbClosestLadder(getLocalPlayer().getZ(), false);
                }

                while (!getInventory().contains(Items.PotOfFlour.name) && getGameObjects().closest(GameObjects.FlourBin.name).hasAction("Empty")) {
                    if (!interactService.interactWithGameObject(GameObjects.FlourBin.name, "Empty")) {
                        return false;
                    }
                }
            }
        }
        logScript("We got flour");
        return true;
    }

    public boolean completeCooksAssistant() {
        sharedService.walkTo(cookArea);

        if (!interactService.interactClosestNpc(COOK, "Talk-to", false)) {
            return false;
        }

        Util.sleepUntil(() -> getDialogues().inDialogue(), Constants.MAX_SLEEP_UNTIL);

        while (getDialogues().inDialogue()) {
            getDialogues().continueDialogue();
            antibanService.antibanSleep(AntibanActionType.SlowPace);
        }
        return true;
    }

    public boolean climbClosestLadder(int floors, boolean isUp) {

        if (floors == 0) {
            return true;
        }

        String action = isUp ? "Climb-up" : "Climb-down";

        int counter = 0;
        while (floors >= 0 && counter < 20) {
            if (interactService.interactWithGameObject(GameObjects.Ladder.name, action)) {
                floors--;
            }
            counter++;
        }

        return floors == 0;
    }
}