package scriptz.thieving;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.AntibanActionType;
import shared.enums.Areas;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.InteractService;

@ScriptManifest(author = "xpt", name = "Pickpocket NPC", version = 1.0, description = "Pickpocket NPC", category = Category.THIEVING)
public class PickpocketMen extends RunescriptAbstractContext {

    enum State { WALK_TO_NPC_AREA, PICKPOCKET, WALK_TO_BANK, BANK, EAT, STOP }

    private BankService bankService;
    private InteractService interactService;

    private final String FOOD = Items.Lobster.name;
    private final String DODGY_NECKLACE = Items.DodgyNecklace.name;
    private final String NPC_NAME = "Man";
    private final Areas NPC_AREA = Areas.EdgevilleHayStacks;
    private final Area NPC_AREA_OUTSIDE = new Area(3101, 3508, 3103, 3510, 0);
    private final Areas BANK_AREA = Areas.EdgevilleBankNorth;

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.FLETCHING);

        logScript("Starting pickpocketing script!");
    }

    public State getState() {
        if (getLocalPlayer().getHealthPercent() < 60 && getInventory().contains(FOOD)) {
            return State.EAT;
        }

        // go bank if inventory is full or if player has no food or no dodgy necklace
        if (getInventory().isFull() || !getInventory().contains(FOOD) ||
                (!getInventory().contains(DODGY_NECKLACE) && !getEquipment().contains(DODGY_NECKLACE))) {
            if (BANK_AREA.getArea().contains(getLocalPlayer())) {
                return State.BANK;
            } else {
                return State.WALK_TO_BANK;
            }
        }

        if (!NPC_AREA.getArea().contains(getLocalPlayer()) && !NPC_AREA_OUTSIDE.contains(getLocalPlayer())) {
            return State.WALK_TO_NPC_AREA;
        }

        return State.PICKPOCKET;
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

            case WALK_TO_NPC_AREA:
                if (sharedService.walkTo(NPC_AREA_OUTSIDE)) {
                    interactService.interactWithGameObject("Large door", "Open");
                    Util.sleepUntil(() -> !getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                }
                break;

            case PICKPOCKET:

                bankService.closeBank(false);
                sharedService.openInventory();

                if (!getEquipment().contains(DODGY_NECKLACE)) {
                    interactService.interactInventoryItem(DODGY_NECKLACE, false);
                }

                if (getInventory().count(Items.CoinPouch.name) >= Calculations.random(20, 30)) {
                    interactService.interactInventoryItem(Items.CoinPouch.name, false);
                }

                interactService.interactClosestNpc(NPC_NAME, "Pickpocket", true);
                Util.sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(3000, 4000));
                if (getLocalPlayer().getAnimation() == 424) {
                    Util.sleepUntil(() -> !getLocalPlayer().isAnimating(), Constants.MAX_SLEEP_UNTIL);
                    int sleepTime = Calculations.random(1200, 2000);
                    logScript("Player got stunned, sleeping for " + sleepTime);
                    Util.sleep(sleepTime);
                } else {
                    logScript("NPC pickpocketed successfully");
                    antibanService.antibanSleep(AntibanActionType.Latency);
                }

                break;

            case WALK_TO_BANK:
                if (getInventory().contains(Items.CoinPouch.name)) {
                    interactService.interactInventoryItem(Items.CoinPouch.name, false);
                }
                if (NPC_AREA.getArea().contains(getLocalPlayer())) {
                    interactService.interactWithGameObject("Large door", "Open");
                    Util.sleepUntil(() -> !getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving(), Constants.MAX_SLEEP_UNTIL);
                }
                sharedService.walkTo(BANK_AREA);
                break;

            case BANK:
                bankService.bankAll(false, false);
                if (!getBank().contains(FOOD)) {
                    stop();
                } else {
                    bankService.withdraw(FOOD, 12, false, false, false);
                    bankService.withdraw(DODGY_NECKLACE, 12, true, false, false);
                }
                break;

            case STOP:
                Util.sleep(Calculations.random(6000, 12000));
                stop();
                break;

        }

        return 0;
    }

}
