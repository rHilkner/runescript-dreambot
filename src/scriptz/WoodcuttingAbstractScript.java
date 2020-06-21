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

        logScript("asdasda");
        if (!treeArea.getArea().contains(getLocalPlayer())) {
            logScript("walking to");
            sharedService.walkTo(treeArea);
        }

        // Chopping trees: Time to chop some trees, our inventory isn't full. We want to fill it up.
        if (!getInventory().isFull()) {
            if (treeArea.getArea().contains(getLocalPlayer())) {
                logScript("chopping");
                woodcuttingService.chopTree(treeName); //change "Tree" to the name of your tree.
            } else if (getWalking().walk(treeArea.getArea().getRandomTile())) {
                logScript("walking to");
                sleep(Calculations.random(3000, 5500));
            }
        }

        // Banking: Time to bank our logs. Our inventory is full, we want to empty it.
        if (getInventory().isFull()) { // it is time to bank
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
