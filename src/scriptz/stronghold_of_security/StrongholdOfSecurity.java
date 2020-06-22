package scriptz.stronghold_of_security;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import shared.Constants;
import shared.RunescriptAbstractContext;
import shared.Util;
import shared.enums.ActionType;

import java.util.Arrays;
import java.util.List;

@ScriptManifest(category = Category.QUEST, name = "Stronghold Of Security", author = "XpT", version = 1.0)
public class StrongholdOfSecurity extends RunescriptAbstractContext {

    private int currentPlayerInteractionIndex = 2;
    private List<Interactions> playerInteractions = Arrays.asList(
            Interactions.Floor0Entrance,
            Interactions.Floor1Door0,
            Interactions.Floor1Door1,
            Interactions.Floor1Door2
    );

    private List<String> rightAnswers = Arrays.asList(
            "Through account settings on oldschool.runescape.com.",
            "No, you should never buy an account",
            "Don't tell them anything and click the 'Report Abuse' button.",
            "Don't give out your password to anyone. Not even close friends."
    );

    @Override
    public void onStart() {
        super.onStart();
        logScript("Stronghold Of Security script is now running - all creditz do XpT º*º");
    }

    @Override
    public int onLoop() {

        super.onLoop();

        if (currentPlayerInteractionIndex == playerInteractions.size()) {
            logScript("Quest finished! Congratulations <3 (all interactions of the quest are done)");
            sleep(Constants.MAX_SLEEP_UNTIL);
            return 0;
        }

        Interactions currentInteraction = playerInteractions.get(currentPlayerInteractionIndex);

        logScript("Next interaction (" + (currentPlayerInteractionIndex+1) + "/" + playerInteractions.size() + "): "
                + currentInteraction.interactionType + " + " + currentInteraction.objectName);

        if (getDialogues().inDialogue()) {
            // First try to answer dialogue
            logScript("Answering dialogue");
            answerDialogue();
        } else if (currentInteraction.interactionArea != null) {
            // Then tries to interact with nearby object or walks to area of interaction
            if (currentInteraction.interactionArea.contains(getLocalPlayer())) {
                logScript("Interacting with: " + currentInteraction.interactionType + " + " + currentInteraction.objectName);
                interact(currentInteraction);
                currentPlayerInteractionIndex++;
            } else {
                logScript("Walking to interaction area");
                sharedService.walkTo(currentInteraction.interactionArea);
            }
        }

        return 0;
    }

    @Override
    public void onExit() {
        super.onExit();
        logScript("End of execution.");
    }

    private void interact(Interactions currentInteraction) {
        GameObject gameObject = currentInteraction.interactionObjectTile != null ?
                sharedService.getObjectOnTileWithName(currentInteraction.interactionObjectTile, currentInteraction.objectName) :
                getGameObjects().closest(Util.filterGameObjectByName(currentInteraction.objectName));

        if (gameObject != null) {
            logScript("Interacting with game-object: " + gameObject);
            gameObject.interact(currentInteraction.interactionType);
            antibanService.antibanSleep(ActionType.FastPace);
            sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
            antibanService.antibanSleep(ActionType.FastPace);
        } else {
            logScript("ERROR: Player is in interaction area, but is not finding GameObject with name " + currentInteraction.objectName);
        }
    }

    private void answerDialogue() {
        String dialogue = getDialogues().getNPCDialogue();

        while (getDialogues().inDialogue()) {
            if (getDialogues().canContinue()) {
                logScript("NPC Dialogue continuing: " + dialogue);
                getDialogues().clickContinue();
                antibanService.antibanSleep(ActionType.Spam);
            } else if (getDialogues().getOptions() != null && getDialogues().getOptions().length != 0) {
                logScript(Arrays.toString(getDialogues().getOptions()));
                for (String option : getDialogues().getOptions()) {
                    if (rightAnswers.contains(option)) {
                        getDialogues().clickOption(option);
                        antibanService.antibanSleep(ActionType.FastPace);
                        break;
                    }
                }
            } else {
                logScript("ERROR: Why player is in dialog, can't continue and also cant choose options?");
            }
        }
    }
}
