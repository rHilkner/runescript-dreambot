package scriptz.woodcutting;

import org.dreambot.api.methods.skills.Skill;
import scriptz.RunescriptAbstractContext;
import shared.enums.Areas;
import shared.enums.GameStyle;
import shared.enums.Items;
import shared.enums.Trees;
import shared.services.BankService;
import shared.services.WoodcuttingService;

public abstract class WoodcuttingAbstractScript extends RunescriptAbstractContext {

    private Trees tree;
    private Areas treeArea;
    private Areas bankArea;

    GameStyle originalGameStyle;
    private WoodcuttingService woodcuttingService;
    private BankService bankService;

    public WoodcuttingAbstractScript() {

    }

    /** LOOP FUNCTIONS */

    public void onStart(Trees tree, Areas treeArea, Areas bankArea) {
        super.onStart();
        logScript("Starting WoodcuttingAbstractScript for cutting " + tree.getTreeName() + " at " + treeArea.name());
        this.tree = tree;
        this.treeArea = treeArea;
        this.bankArea = bankArea;
        originalGameStyle = ctx.getGameStyle();
        woodcuttingService = WoodcuttingService.getInstance();
        bankService = BankService.getInstance();
        antibanService.setSkillsToHover(Skill.WOODCUTTING);
    }

    @Override
    public int onLoop() {
        super.onLoop();

        if (!getInventory().isFull()) {
            setGameStyle(originalGameStyle);
            if (treeArea.getArea().contains(getLocalPlayer())) {
                woodcuttingService.chopTree(tree);
            } else {
                sharedService.walkTo(treeArea);
            }
        } else {
            if (bankArea.getArea().contains(getLocalPlayer())) {
                setGameStyle(GameStyle.Normal);
                bankService.bankAllExcept(true, Items.BronzeAxe.name, Items.IronAxe.name, Items.SteelAxe.name,
                        Items.BlackAxe.name, Items.MithrilAxe.name, Items.AdamantAxe.name, Items.RuneAxe.name, Items.DragonAxe.name);
            } else {
                sharedService.walkTo(bankArea);
            }
        }

        return 0;
    }

}
