package shared.enums;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum Areas {

    BarbarianVillageFlyFishing(new Area(3101, 3424, 3110, 3434, 0)),
    EdgevilleBank(new Area(3091, 3488, 3098, 3496, 0)),

    DraynorVillageBank(new Area(3092, 3240, 3097, 3246, 0)),
    DraynorVillageTrees(new Area(3074, 3265, 3083, 3274, 0)),
    DraynorVillageWillowTrees(new Area(3083, 3239, 3090, 3226, 0)),
    DraynorVillageFishingSpots(new Area(3087, 3227, 3084, 3234, 0)),

    FaladorSouthChickens(new Area(3014, 3294, 3020, 3282, 0)),
    FaladorSouthCowPen(new Area(3265, 3296, 3253, 3255, 0)),

    StrongholdOfSecurityTop(new Area(3077, 3423, 3084, 3416, 0)),
    StrongholdOfSecurityFirstFloor(new Area(1858, 5244, 1900, 5300, 0)),
    StrongholdOfSecurityFirstFloorSouth(new Area(1858, 5220, 1859, 5221, 0)),
    StrongholdOfSecurityFirstFloorEnd(new Area(1858, 5220, 1859, 5221, 0));

    private Area area;

    Areas(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

}
