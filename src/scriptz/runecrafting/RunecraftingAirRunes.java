package scriptz.runecrafting;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

import java.util.Objects;

@ScriptManifest(author = "Xpt", name = "Runecrafting Air Runes", version = 1.0, description = "Runecrafting Air Runes", category = Category.RUNECRAFTING)
public class RunecraftingAirRunes extends RunescriptAbstractContext {

    enum State {RUNECRAFT, BANK }

    private BankService bankService;
    private InteractService interactService;
    private int inventoriesDone = 0;

    private final Area FALADOR_EAST_BANK = Areas.FaladorEastBank.getArea();
    private final Area AIR_ALTAR_OUTIDE = Areas.AirAltarOutside.getArea();
    private final Area AIR_ALTAR_INSIDE = Areas.AirAltarInside.getArea();
    private final String AIR_TIARA = Items.AirTiara.name;
    private final String RUNE_ESSENCE = Items.RuneEssence.name;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.RUNECRAFTING);

        logScript("Starting runecrafting air runes script!");
    }

    public State getState() {
        if (getInventory().contains(RUNE_ESSENCE) && !getInventory().get(RUNE_ESSENCE).isNoted()) {
            return State.RUNECRAFT;
        }
        return State.BANK;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());
        logScript("-- Inventories done so far: " + inventoriesDone);

        switch (currentState) {

            case RUNECRAFT:
                // basic closing shit and stuff
                bankService.closeBank(false);

                if (AIR_ALTAR_INSIDE.contains(getLocalPlayer())) {
                    logScript("Crafting runes");
                    interactService.interactWithGameObject("Altar");
                    inventoriesDone++;
                    Util.sleepUntil(() -> !getInventory().contains(RUNE_ESSENCE) && !getLocalPlayer().isMoving() && !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                    interactService.interactWithGameObject("Portal");
                    Util.sleepUntil(() -> !AIR_ALTAR_INSIDE.contains(getLocalPlayer()), Constants.MAX_SLEEP_UNTIL);
                } else if (AIR_ALTAR_OUTIDE.contains(getLocalPlayer())) {
                    interactService.interactWithGameObject("Mysterious ruins");
                    Util.sleepUntil(() -> !AIR_ALTAR_OUTIDE.contains(getLocalPlayer()), Constants.MAX_SLEEP_UNTIL);
                } else {
                    sharedService.walkTo(AIR_ALTAR_OUTIDE);
                }

                break;
            case BANK:
                // making sure to unselect any item
                if (getInventory().isItemSelected()) {
                    getInventory().deselect();
                }

                if (AIR_ALTAR_INSIDE.contains(getLocalPlayer())) {
                    interactService.interactWithGameObject("Portal");
                    Util.sleepUntil(() -> !AIR_ALTAR_INSIDE.contains(getLocalPlayer()), Constants.MAX_SLEEP_UNTIL);
                }

                if (sharedService.walkTo(FALADOR_EAST_BANK)) {
                    bankService.bankAll(false, false);

                    Item playersAirTiara = getEquipment().get(i -> i != null && i.getName() != null && Objects.equals(i.getName(), AIR_TIARA));
                    if (playersAirTiara == null) {
                        logScript("Equipping air tiara");
                        bankService.withdraw(AIR_TIARA, 1, true, false, false);
                        interactService.interactInventoryItem(AIR_TIARA, false);
                    }

                    bankService.withdraw(RUNE_ESSENCE, null, true, false, false);
                }

                break;

        }

        return 0;
    }

}
