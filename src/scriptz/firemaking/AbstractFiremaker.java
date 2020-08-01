package scriptz.firemaking;

import org.dreambot.api.methods.skills.Skill;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.GameStyle;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.FiremakingService;

public abstract class AbstractFiremaker extends RunescriptAbstractContext {

    private Items logs;
    private Areas fireStartArea;
    private Areas bankArea;

    GameStyle originalGameStyle;
    private FiremakingService firemakingService;
    private BankService bankService;

    /** LOOP FUNCTIONS */

    public void onStart(Items logs, Areas fireStartArea, Areas bankArea) {
        super.onStart();
        logScript("Starting AbstractFiremaker for fireing " + logs.name + " at " + fireStartArea.name());
        this.logs = logs;
        this.fireStartArea = fireStartArea;
        this.bankArea = bankArea;
        originalGameStyle = ctx.getGameStyle();
        firemakingService = FiremakingService.getInstance();
        bankService = BankService.getInstance();
        antibanService.setSkillsToHover(Skill.WOODCUTTING);
    }

    @Override
    public int onLoop() {
        super.onLoop();

        if (!getInventory().contains(logs.name)) {
            if (fireStartArea.getArea().contains(getLocalPlayer())) {
                firemakingService.fireLogs(logs.name, true);
            } else {
                sharedService.walkTo(fireStartArea);
            }
        } else {
            if (bankArea.getArea().contains(getLocalPlayer()) && !getInventory().isFull()) {
                bankService.bankAllExcept(false, Items.Tinderbox.name);
                bankService.withdraw(logs.name, null, true, false);
            } else {
                sharedService.walkTo(bankArea);
            }
        }

        return 0;
    }

}
