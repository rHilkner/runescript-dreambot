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
    EdgevilleHayStacks(new Area(3091, 3507, 3100, 3513, 0)),

    EdgevilleDungeonTop(new Area(3093, 3468, 3097, 3471, 0)),
    EdgevilleDungeonVarrockTop(new Area(3115, 3450, 3117, 3453, 0)),
    EdgevilleDungeonVarrockTopOutside(new Area(3114, 3449, 3119, 3446, 0)),
    EdgevilleDungeonHillGiants(new Area(3095, 9824, 3125, 9860, 0)),
    EdgevilleDungeonHillGiantsLadder(new Area(3115, 9850, 3118, 9853, 0)),

    FaladorEastBank(new Area(3010, 3357, 3015, 3355, 0)),
    FaladorSouthChickens(new Area(3014, 3294, 3020, 3282, 0)),

    GrandExchange(new Area(3160, 3494, 3170, 3484, 0)),
    GrandExchangeCloseToEastBank(new Area(3169, 3488, 3170, 3491, 0)),
    GrandExchangeNorthFiremake(new Area(3176, 3501, 3177, 3506, 0)),

    KaramjaToPortSarimBoat(new Area(3033, 3218, 3032, 3215, 1)),
    KaramjaToPortSarim(new Area(2950, 3146, 2955, 3149, 0)),
    KaramjaVolcanoWest(new Area(2821, 3188, 2836, 3158, 0)),

    LumbridgeBank(new Area(3208, 3220, 3209, 3216, 2)),
    LumbridgeEastCowPen(new Area(3253, 3255, 3265, 3296, 0)),

    PortSarimToKaramja(new Area(3026, 3225, 3029, 3213, 0)),
    PortSarimDepositBox(new Area(3041, 3236, 3046, 3234, 0)),
    PortSarimToKaramjaBoat(new Area(2957, 3142, 2955, 3143, 1)),

    VarrockWestBank(new Area(3180, 3433, 3185, 3445, 0)),
    VarrockSouthWestMineClay(new Area(3179, 3371, 3180, 3372, 0)),
    VarrockSouthWestMineTin(new Area(3181, 3375, 3182, 3377, 0)),
    VarrockSouthWestMineIron(new Area(3174, 3366, 3175, 3368, 0)),

    AirAltarOutside(new Area(2977, 3288, 2989, 3295, 0)),
    AirAltarInside(new Area(2839,4826, 2846, 4836, 0)),

    SandiCrahbNortheast(new Area(1773, 3424, 1782, 3434, 0)),

    ArdougneBank(new Area(2649, 3280, 2655, 3287, 0))

    ;

    private final Area area;

    Areas(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

}
