package scriptz.stronghold_of_security;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import shared.enums.Areas;

enum Interactions {
    Floor0Entrance("Entrance", "Climb-down",
            Areas.StrongholdOfSecurityTop.getArea(),
            new Tile(3081, 3420, 0)
    ),
    Floor1Door0("Gate of War", "Open",
            new Area(1858, 5244, 1862, 5241),
            new Tile(1859, 5238, 0)
    ),
    Floor1Door1("Gate of War", "Open",
            new Area(1858, 5238, 1859, 5236),
            new Tile(1858, 5235, 0)
    ),
    Floor1Door2("Gate of War", "Open",
            new Area(1860, 5215, 1861, 5213),
            new Tile(1861, 5212)
    ),
    Floor1Door3("Gate of War", "Open",
            new Area(1860, 5212, 1861, 5210),
            new Tile(1861, 5209)
    );

    String objectName;
    String interactionType;
    Tile interactionObjectTile;
    Area interactionArea;

    Interactions(String objectName, String interactionType, Area interactionArea, Tile interactionObjectTile) {
        this.objectName = objectName;
        this.interactionType = interactionType;
        this.interactionObjectTile = interactionObjectTile;
        this.interactionArea = interactionArea;
    }
}