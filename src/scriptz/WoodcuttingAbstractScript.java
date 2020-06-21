package scriptz;

import org.dreambot.api.methods.Calculations;
import shared.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.services.BankService;
import shared.services.WoodcuttingService;

public abstract class WoodcuttingAbstractScript extends RunescriptAbstractContext {

    private String treeName;
    private Areas treeArea;
    private Areas bankArea;

    private WoodcuttingService woodcuttingService;
    private BankService bankService;

    public WoodcuttingAbstractScript() {
    }

    /** LOOP FUNCTIONS */

    public void onStart(String treeName, Areas treeArea, Areas bankArea) {
        super.onStart();
        logScript("aa");
        logScript("Starting WoodcuttingAbstractScript");
        this.treeName = treeName;
        this.treeArea = treeArea;
        this.bankArea = bankArea;
        woodcuttingService = WoodcuttingService.getInstance();
        bankService = BankService.getInstance();
    }

    @Override
    public int onLoop() {
        super.onLoop();

        if (!treeArea.getArea().contains(getLocalPlayer())) {
            logScript("walking to");
            sharedService.walkTo(treeArea);
        } else if (!getInventory().isFull()) {
            logScript("chopping");
            woodcuttingService.chopTree(treeName); //change "Tree" to the name of your tree.
        } else if (getInventory().isFull()) { // it is time to bank
            logScript("banking");
            if (bankArea.getArea().contains(getLocalPlayer())) {
                bankService.bankAllExcept("axe");
            } else if (getWalking().walk(bankArea.getArea().getRandomTile())) {
                logScript("going to bank");
                sleep(Calculations.random(3000, 6000));
            }
        }

        return 0;
    }

    @Override
    public void onExit() {
        super.onExit();
        logScript("End of script.");
    }
}
