package scriptz.cooking;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;

@ScriptManifest(author = "Renormalize", name = "Pizza Dough", version = 1.0, description = "Makes Pizza Dough", category = Category.COOKING)
public class PizzaDough extends AbstractScript {

    public void onStart() {
        log("Starting Dough Script!");
    }

    public void onExit() {
        log("Ending Dough Script!");
    }

    @Override
    public int onLoop() {
        if (!getInventory().contains("Pot of flour") || !getInventory().contains("Jug of water")) {

            getBank().open();

            int counter = 0;
            while (!getBank().isOpen()) {
                sleep(300);

                // Needed in case bank doesn't open
                counter++;
                if (counter > 5)
                    return Calculations.random(500, 600);
            }

            sleep(350);
            getBank().depositAllItems();
            sleep(1200);

            if (getBank().count("Pot of flour") == 0 && getBank().count("Jug of water") == 0 && getBank().isOpen()) {
                sleep(100000);

                // No Flour or water, should end script here
                return 100000;
            }

            // Withdraw Flour and Water
            sleep(Calculations.random(200, 600));
            getBank().withdraw("Pot of flour", 9);
            sleep(1200);
            getBank().withdraw("Jug of water", 9);
            sleep(Calculations.random(1000, 1300));
            getBank().close();
            sleep(Calculations.random(200, 600));
        } else {
            Item dough = getInventory().get("Pot of flour");
            Item water = getInventory().get("Jug of water");
            dough.useOn(water);
            sleep(1000);

            if (getWidgets().getWidget(219).getChild(0).getChild(3) == null) {
                log("First Interface is null, redo loop.");
                return Calculations.random(700, 1200);
            } else {
                getWidgets().getWidget(219).getChild(0).getChild(3).interact();
                sleep(950);
            }
            if (getWidgets().getWidget(309).getChild(6) == null) {
                log("Second Interface is null, redo loop.");
                return Calculations.random(700, 1200);
            } else {
                getWidgets().getWidget(309).getChild(6).interact("Make All");
            }

            int i = 0;
            while (getInventory().contains("Jug of water") && i < 20) {
                sleep(Calculations.random(700, 1200));
                i++;
                // i variable is there to make sure script doesn't get stuck if
                // dough isn't being made
            }
        }
        return Calculations.random(500, 600);
    }
}