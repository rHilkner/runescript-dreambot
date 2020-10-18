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

@ScriptManifest(author = "xpt", name = "Pickpocket Ardy Knights", version = 1.0, description = "Pickpocket Ardy Knights", category = Category.THIEVING)
public class PickpocketArdyKnights extends RunescriptAbstractContext {

    enum State { PICKPOCKET, BANK, EAT, STOP }

    private BankService bankService;
    private InteractService interactService;

    int pickpocketsDone = 0;

    private final String FOOD = Items.Lobster.name;
    private final String DODGY_NECKLACE = Items.DodgyNecklace.name;
    private final String NPC_NAME = "Knight of Ardougne";
    private final Areas NPC_AREA = Areas.ArdougneBank;
    private final Areas BANK_AREA = Areas.ArdougneBank;
    private final Area BANK_UPSTAIRS = new Area(2649, 3285, 2651, 3287, 1);

    @Override
    public void onStart() {
        super.onStart();
        bankService = BankService.getInstance();
        interactService = InteractService.getInstance();
        antibanService.setSkillsToHover(Skill.THIEVING);

        logScript("Starting pickpocketing script!");
    }

    public State getState() {
        if (getLocalPlayer().getHealthPercent() < 60 && getInventory().contains(FOOD)) {
            return State.EAT;
        }

        // go bank if inventory is full or if player has no food or no dodgy necklace
        if (getInventory().isFull() || !getInventory().contains(FOOD) ||
                (!getInventory().contains(DODGY_NECKLACE) && !getEquipment().contains(DODGY_NECKLACE))) {
            return State.BANK;
        }

        return State.PICKPOCKET;
    }

    @Override
    public int onLoop() {
        super.onLoop();

        State currentState = getState();
        logScript("-- Current state: " + currentState.name());
        logScript("-- Pickpockets done: " + pickpocketsDone + " (" + (pickpocketsDone * 84)/1000 + "k xp)");

        switch (currentState) {

            case EAT:
                sharedService.eatAnything();
                break;

            case PICKPOCKET:

                bankService.closeBank(false);
                sharedService.openInventory();

                if (BANK_UPSTAIRS.contains(getLocalPlayer())) {
                    logScript("Player is upstairs, climbing down");
                    interactService.interactWithGameObject("Ladder", "Climb-down");
                }

                if (!NPC_AREA.getArea().contains(getLocalPlayer())) {
                    sharedService.walkTo(NPC_AREA);
                }

                if (!getEquipment().contains(DODGY_NECKLACE)) {
                    interactService.interactInventoryItem(DODGY_NECKLACE, false);
                }

                if (getInventory().count(Items.CoinPouch.name) >= Calculations.random(20, 27)) {
                    pickpocketsDone += getInventory().count(Items.CoinPouch.name);
                    while (getInventory().contains(Items.CoinPouch.name)) {
                        interactService.interactInventoryItem(Items.CoinPouch.name, false);
                        Util.sleepUntil(() -> !getInventory().contains(Items.CoinPouch.name), Calculations.random(1000, 2000));
                    }
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

            case BANK:

                if (getInventory().contains(Items.CoinPouch.name)) {
                    pickpocketsDone += getInventory().count(Items.CoinPouch.name);
                    while (getInventory().contains(Items.CoinPouch.name)) {
                        interactService.interactInventoryItem(Items.CoinPouch.name, false);
                        Util.sleepUntil(() -> !getInventory().contains(Items.CoinPouch.name), Calculations.random(1000, 2000));
                    }
                }

                bankService.bankAll(false, false);
                if (!getBank().contains(FOOD)) {
                    stop();
                } else {
                    bankService.withdraw(FOOD, 20, false, false, false);
                    bankService.withdraw(DODGY_NECKLACE, 5, true, false, false);
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
