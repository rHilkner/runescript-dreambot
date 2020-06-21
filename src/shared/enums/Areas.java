package shared.enums;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum Areas {

    EdgevilleBank(new Area(3092, 3240, 3097, 3246, 0)),
    EdgevilleTrees(new Area(3074, 3265, 3083, 3274, 0)),
    EdgevilleWillowTrees(new Area(3083, 3239, 3090, 3226, 0)),
    FaladorSouthChickens(new Area(new Tile(3014, 3294), new Tile(3020, 3282))),
    FaladorSouthCowPen(new Area(new Tile(3265, 3296), new Tile(3253, 3255)));

    private Area area;

    Areas(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

}
