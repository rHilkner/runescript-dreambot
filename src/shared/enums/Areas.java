package shared.enums;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum Areas {

    FALADOR_SOUTH_CHICKENS(new Area(new Tile(3014, 3294), new Tile(3020, 3282))),
    FaladorSouthCowPen(new Area(new Tile(3265, 3296), new Tile(3253, 3255)));

    public Area area;

    Areas(Area area) {
        this.area = area;
    }

}
