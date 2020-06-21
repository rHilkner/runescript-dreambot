package scriptz;

import org.dreambot.api.methods.Calculations;
import shared.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.services.BankService;
import shared.services.WoodcuttingService;

public class WoodcuttingAbstractScript extends RunescriptAbstractContext {

    private String treeName;
    private Areas treeArea;
    private Areas bankArea;

    private WoodcuttingService woodcuttingService;
    private BankService bankService;

    public WoodcuttingAbstractScript() {
    }

    /** GETTERS AND SETTERS */

    public void setTreeArea(Areas treeArea) {
        this.treeArea = treeArea;
    }

    public void setBankArea(Areas bankArea) {
        this.bankArea = bankArea;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    /** LOOP FUNCTIONS */

    @Override
    public void onStart() {
        super.onStart("Starting WoodcuttingAbstractScript");

        woodcuttingService = WoodcuttingService.getInstance();
        bankService = BankService.getInstance();
    }

    @Override
    public int onLoop() {
        super.onLoop();

        // Chopping trees: Time to chop some trees, our inventory isn't full. We want to fill it up.
        if (!getInventory().isFull()) {
            if (treeArea.getArea().contains(getLocalPlayer())) {
                woodcuttingService.chopTree(treeName); //change "Tree" to the name of your tree.
            } else {
                if (getWalking().walk(treeArea.getArea().getRandomTile())) {
                    sleep(Calculations.random(3000, 5500));
                }
            }
        }

        // Banking: Time to bank our logs. Our inventory is full, we want to empty it.
        if (getInventory().isFull()) { // it is time to bank
            if (bankArea.getArea().contains(getLocalPlayer())) {
                bankService.bankAllExcept("axe");
            } else {
                if (getWalking().walk(bankArea.getArea().getRandomTile())) {
                    sleep(Calculations.random(3000, 6000));
                }
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
