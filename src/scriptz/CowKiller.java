//package scriptz.cow_killer;
//
//import shared.enums.ActionType;
//import shared.services.SharedServices;
//import shared.Util;
//import org.dreambot.api.methods.Calculations;
//import org.dreambot.api.methods.map.Area;
//import org.dreambot.api.methods.map.Tile;
//import org.dreambot.api.script.AbstractScript;
//import org.dreambot.api.script.Category;
//import org.dreambot.api.script.ScriptManifest;
//import org.dreambot.api.wrappers.interactive.GameObject;
//import org.dreambot.api.wrappers.interactive.NPC;
//import org.dreambot.api.wrappers.items.GroundItem;
//
//@ScriptManifest(author = "fractal", name = "FractalCowKiller", category = Category.COMBAT, version = 1.0, description = "Kills cows, loots their hides, and banks")
//
//public class scriptz.cow_killer extends AbstractScript {
//
//    int walkingShape;
//    int runningShape;
//    int interactiveShape;
//    int spamShape;
//    int scale;
//    int reaction;   // minimum wait between actions
//    int patience;   // minimum wait between running to a new spot while still running. patience is doubled while Walking
//    int latency = 250;  // how long does it take the game to register your actions? change this depending on CPU/RAM/lag
//    Area cowPen = new Area(new Tile(3265, 3296), new Tile(3253, 3255));
//    int cowHide = 1739;
//    int gate = 1558;
//
//    @Override
//    public int onLoop() {
//        farmHides();
//        return 0;
//    }
//
//    public void onStart(){
//        reaction = Calculations.random(200,400);
//        patience = Calculations.random(1500, 3000);
//        walkingShape = Calculations.random(2,5);
//        runningShape = Calculations.random(2,4);
//        interactiveShape = Calculations.random(2,4);
//        spamShape = 1;
//        scale = Calculations.random(400,1000);  // proxy for variables in your physical environment affecting your attention
//    }
//
//    public int farmHides() {
//        String[] targets = {"Cow", "Cow calf"};
//        Integer[] loot = {cowHide};
//        if (getInventory().isFull()) {
//            SharedServices.bankItems(loot, false);
//        } else {
//            combatLoot(targets, loot, cowPen);
//        }
//        return 1;
//    }
//
//    public void combatLoot(String[] targets, Integer[] loot, Area area) {
//        if (!area.contains(getLocalPlayer())) {
//            SharedServices.walkTo(area);
//        } else if (!getLocalPlayer().isInCombat()){
//            NPC target = getNpcs().closest(t -> t!= null && !t.isInCombat() && Util.isElementInList(t.getName(), targets));
//            GroundItem prize = getGroundItems().closest(loot);
//            if (prize != null && target != null && prize.distance(getLocalPlayer()) < target.distance(getLocalPlayer())) {
//                prize.interact("Take");
//                sleep(latency);
//                sleepUntil(()-> !getLocalPlayer().isMoving(), 60000);
//                SharedServices.antibanSleep(ActionType.FastPace);
//            } else if (target != null && !getLocalPlayer().isInCombat()) {
//                GameObject g = getGameObjects().closest(o -> o.getID() == gate);
//                if (!area.contains(target) && g.hasAction("Open")) {
//                    g.interact("Open");
//                } else if (target.interact("Attack")) {
//                    sleepUntil(() -> getLocalPlayer().isInCombat(), 60000);
//                    sleepUntil(() -> !getLocalPlayer().isInCombat(), 60000);
//                }
//                SharedServices.antibanSleep(ActionType.FastPace);
//            }
//        } else {
//            sleepUntil(() -> !getLocalPlayer().isInCombat(), 60000);
//        }
//    }
//
//}