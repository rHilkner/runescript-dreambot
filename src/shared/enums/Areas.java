package shared.enums;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum Areas {

    EDGEVILLE_BANK(new Area(3092, 3240, 3097, 3246, 0)),
    EDGEVILLE_TREES(new Area(3074, 3265, 3083, 3274, 0)),
    FALADOR_SOUTH_CHICKENS(new Area(new Tile(3014, 3294), new Tile(3020, 3282))),
    FaladorSouthCowPen(new Area(new Tile(3265, 3296), new Tile(3253, 3255)));

    private Area area;

    Areas(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

}
