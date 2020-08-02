package shared.enums;

import org.dreambot.api.methods.map.Area;

public enum Areas {

    AlKharidBank(new Area(3269, 3170, 3271, 3166, 0)),
    AlKharidTanner(new Area(3277, 3193, 3274, 3191, 0)),

    BarbarianVillageCenter(new Area(3077, 3423, 3084, 3416, 0)),
    BarbarianVillageFlyFishing(new Area(3101, 3424, 3110, 3434, 0)),

    DraynorVillageBank(new Area(3092, 3240, 3097, 3246, 0)),
    DraynorVillageTrees(new Area(3074, 3265, 3083, 3274, 0)),
    DraynorVillageWillowTrees(new Area(3083, 3239, 3090, 3226, 0)),
    DraynorVillageFishingSpots(new Area(3087, 3227, 3084, 3234, 0)),

    DwarvenMineIceMountainTop(new Area(3015, 3454, 3019, 3447, 0)),
    DwarvenMineIceMountainBottom(new Area(3017, 9851, 3021, 9848, 0)),
    DwarvenMineDrogoShop(new Area(3025, 9847, 3039, 9844, 0)),

    EdgevilleBank(new Area(3091, 3488, 3098, 3496, 0)),
    EdgevilleFurnace(new Area(3109, 3500, 3105, 3498, 0)),
    EdgevilleBankNorth(new Area(3095, 3494, 3098, 3496, 0)),
    EdgevilleBankToFurnace(new Area(3110, 3501, 3094, 3494, 0)),

    FaladorSouthChickens(new Area(3014, 3294, 3020, 3282, 0)),

    GrandExchange(new Area(3160, 3494, 3170, 3484, 0)),
    GrandExchangeCloseToEastBank(new Area(3169, 3488, 3170, 3491, 0)),
    GrandExchangeNorthFiremake(new Area(3176, 3496, 3177, 3497, 0)),

    KaramjaToPortSarimBoat(new Area(3033, 3218, 3032, 3215, 1)),
    KaramjaToPortSarim(new Area(2950, 3146, 2955, 3149, 0)),
    KaramjaVolcanoWest(new Area(2821, 3188, 2836, 3158, 0)),

    LumbridgeBank(new Area(3208, 3220, 3209, 3216, 2)),
    LumbridgeEastCowPen(new Area(3253, 3255, 3265, 3296, 0)),

    PortSarimToKaramja(new Area(3026, 3225, 3029, 3213, 0)),
    PortSarimDepositBox(new Area(3041, 3236, 3046, 3234, 0)),
    PortSarimToKaramjaBoat(new Area(2957, 3142, 2955, 3143, 1));

    private final Area area;

    Areas(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

}
