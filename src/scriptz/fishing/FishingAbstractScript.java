package scriptz.fishing;

import org.dreambot.api.methods.skills.Skill;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.FishingType;
import shared.enums.GameStyle;
import shared.services.BankService;
import shared.services.FishingService;

public abstract class FishingAbstractScript extends RunescriptAbstractContext {

    private FishingType fish;
    private Areas fishArea;
    private Areas bankArea;

    GameStyle originalGameStyle;
    private FishingService fishingService;
    private BankService bankService;

    /** LOOP FUNCTIONS */

    public void onStart(FishingType fish, Areas treeArea, Areas bankArea) {
        super.onStart();
        logScript("Starting WoodcuttingAbstractScript");
        this.fish = fish;
        this.fishArea = treeArea;
        this.bankArea = bankArea;
        originalGameStyle = ctx.getGameStyle();
        fishingService = FishingService.getInstance();
        bankService = BankService.getInstance();
        antibanService.setSkillsToHover(Skill.FISHING);
    }

    @Override
    public int onLoop() {
        super.onLoop();

        if (!getInventory().isFull()) {
            if (fishArea.getArea().contains(getLocalPlayer())) {
                if (!getLocalPlayer().isAnimating()) {
                    fishingService.fish(fish);
                }
            } else {
                sharedService.walkTo(fishArea);
            }
        } else {
            if (bankArea.getArea().contains(getLocalPlayer())) {
                bankService.bankAllExcept(true, fish.getEquipmentName(), "Feather", "Bait");
            } else {
                sharedService.walkTo(bankArea);
            }
        }

        return 0;
    }

}
