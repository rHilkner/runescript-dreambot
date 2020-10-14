package scriptz.combat;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Player;
import scriptz.RunescriptAbstractContext;
import shared.Constants;
import shared.Util;
import shared.enums.Areas;
import shared.services.AntibanService;
import shared.services.CombatService;
import shared.services.WorldHopService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ScriptManifest(author = "xpt", name = "Sand Crab Killer", category = Category.COMBAT, version = 1.0, description = "Sand Crab Killer")
public class SandCrabKiller extends RunescriptAbstractContext {

    enum State { KEEP_KILLIN, WALK_AWAY, GO_TO_SANDCRAB_TILES, EAT }

    private CombatService combatService;
    private AntibanService antibanService;
    private WorldHopService worldHopService;

    private final String SAND_CRAB = "Sand Crab";
    private final List<Tile> SAND_CRABS_TILES = Arrays.asList(
            new Tile(1749, 3412, 0),
            new Tile(1751, 3425, 0),
            new Tile(1764, 3445, 0),
            new Tile(1758, 3439, 0)
    );
    private final Area FAR_AWAY_AREA = Areas.SandiCrahbNortheast.getArea();

    private boolean shouldWalkAway = false;

    private State getState() {

        if (getLocalPlayer().getHealthPercent() < 40) {
            return State.EAT;
        }

        if (getLocalPlayer().isInCombat()) {
            return State.KEEP_KILLIN;
        }

        boolean isPlayerInSandCrabTiles = false;
        for (Tile tile : SAND_CRABS_TILES) {
            if (Objects.equals(getLocalPlayer().getTile(), tile)) {
                isPlayerInSandCrabTiles = true;
                break;
            }
        }

        if (isPlayerInSandCrabTiles) {
            logScript("Player in SandCrab tile already... Waiting to see if the player will get attacked");
            if (sleepUntil(() -> getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL)) {
                return State.KEEP_KILLIN;
            } else {
                shouldWalkAway = true;
                return State.WALK_AWAY;
            }
        }

        if (shouldWalkAway) {
            return State.WALK_AWAY;
        }

        return State.GO_TO_SANDCRAB_TILES;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.combatService = CombatService.getInstance();
        this.antibanService = AntibanService.getInstance();
        this.worldHopService = WorldHopService.getInstance();
        antibanService.setSkillsToHover(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE);
        logScript("Sand Crab Killer starting - creditz to XpT ø*ø");
    }

    @Override
    public int onLoop() {

        super.onLoop();
        State currentState = getState();
        logScript("-- Current state: " + currentState.name());

        switch (currentState) {
            case EAT:
                // eat twice
                sharedService.eatAnything();
                sharedService.eatAnything();
                break;
            case KEEP_KILLIN:
                sleepUntil(() -> !getLocalPlayer().isInCombat(), Constants.MAX_SLEEP_UNTIL);
                break;
            case WALK_AWAY:
                sharedService.walkTo(FAR_AWAY_AREA);
                if (FAR_AWAY_AREA.contains(getLocalPlayer())) {
                    shouldWalkAway = false;
                }
                break;
            case GO_TO_SANDCRAB_TILES:
                Tile freeTile = null;
                for (Tile tile : SAND_CRABS_TILES) {
                    logScript("Going to check if " + tile + " is already occupied");
                    sharedService.walkToClosest(tile.getArea(Calculations.random(8, 12)));
                    boolean isTileFree = true;
                    for (Player player : getPlayers().all()) {
                        if (tile.getArea(3).contains(player)) {
                            isTileFree = false;
                        }
                    }
                    if (isTileFree) {
                        freeTile = tile;
                        break;
                    } else {
                        logScript("Tile " + tile + " is already occupied, looking for another one");
                    }
                }

                if (freeTile == null) {
                    if (getLocalPlayer().isInteractedWith()) {
                        logScript("Just waiting until player is not in combat to hop world");
                        Util.sleepUntil(() -> !getLocalPlayer().isInteractedWith() && !getLocalPlayer().isInCombat(), 59876);
                        Util.sleep(Calculations.random(10000,11000));
                    }
                    logScript("Looks like there is no free tile... hopping world!");
                    worldHopService.hopNext(true, false, true);
                } else {
                    logScript("Looks like tile " + freeTile + " is free, going there to kill some crab0rz");
                    sharedService.walkTo(freeTile);
                }
                break;
        }

        return 0;
    }

}