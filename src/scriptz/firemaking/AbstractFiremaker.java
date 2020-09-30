package scriptz.firemaking;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.GameStyle;
import shared.enums.Items;
import shared.services.BankService;
import shared.services.FiremakingService;

public abstract class AbstractFiremaker extends RunescriptAbstractContext {

    private Items logs;
    private Areas fireStartArea;

    private boolean didPlayerJustBank = true;

    GameStyle originalGameStyle;
    private FiremakingService firemakingService;
    private BankService bankService;

    public void onStart(Items logs, Areas fireStartArea) {
        super.onStart();
        logScript("Starting AbstractFiremaker for fireing " + logs.name + " at " + fireStartArea.name());
        this.logs = logs;
        this.fireStartArea = fireStartArea;
        originalGameStyle = ctx.getGameStyle();
        firemakingService = FiremakingService.getInstance();
        bankService = BankService.getInstance();
        antibanService.setSkillsToHover(Skill.FIREMAKING);
    }

    @Override
    public int onLoop() {
        super.onLoop();

        if (!getTabs().isOpen(Tab.INVENTORY)) {
            getTabs().open(Tab.INVENTORY);
        }

        if (getInventory().contains(logs.name)) {
            if (didPlayerJustBank) {
                if (sharedService.walkTo(fireStartArea)) {
                    didPlayerJustBank = false;
                }
            } else {
                firemakingService.fireLogs(logs.name, true);
            }
        } else if (!getLocalPlayer().isMoving()) {
            bankService.openBank(false);
            if (getBank().count(logs.name) == 0) {
                stop();
            }

            if (!getInventory().contains(Items.Tinderbox.name)) {
                bankService.withdraw(Items.Tinderbox.name, 1, false, false, false);
            }

            bankService.withdraw(logs.name, null, true, false, false);
            didPlayerJustBank = true;
        }

        return 0;
    }

}
