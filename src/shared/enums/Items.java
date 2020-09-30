package shared.enums;

public enum Items {
    Coins("Coins"),
    CoinPouch("Coin pouch"),
    Bones("Bones"),
    Cowhide("Cowhide"),
    Feather("Feather"),

    BronzeAxe("Bronze axe"),
    IronAxe("Iron axe"),
    SteelAxe("Steel axe"),
    BlackAxe("Black axe"),
    MithrilAxe("Mithril axe"),
    AdamantAxe("Adamant axe"),
    RuneAxe("Rune axe"),
    DragonAxe("Dragon axe"),

    BronzePickaxe("Bronze pickaxe"),
    IronPickaxe("Iron pickaxe"),
    SteelPickaxe("Steel pickaxe"),
    BlackPickaxe("Black pickaxe"),
    MithrilPickaxe("Mithril pickaxe"),
    AdamantPickaxe("Adamant pickaxe"),
    RunePickaxe("Rune pickaxe"),
    DragonPickaxe("Dragon pickaxe"),

    PotOfFlour("Pot of flour"),
    JugOfWater("Jug of water"),
    PizzaBase("Pizza base"),

    ChocolateBar("Chocolate bar"),
    ChocolateDust("Chocolate dust"),
    Knife("Knife"),

    GoldBar("Gold bar"),

    Emerald("Emerald"),

    EmeraldRing("Emerald ring"),
    RingOfForging("Ring of forging"),
    RingOfDueling8("Ring of dueling(8)"),

    GoldNecklace("Gold necklace"),
    EmeraldAmulet("Emerald amulet"),
    EmeraldAmuletU("Emerald amulet (u)"),
    NecklaceMould("Necklace mould"),
    AmuletMould("Amulet mould"),

    Tinderbox("Tinderbox"),
    Logs("Logs"),
    OakLogs("Oak logs"),
    WillowLogs("Willow logs"),
    MapleLogs("Maple logs"),
    YewLogs("Yew logs"),
    MagicLogs("Magic logs"),

    RawShrimps("Raw shrimps"),
    RawAnchovies("Raw anchovies"),
    RawTrout("Raw trout"),
    RawSalmon("Raw salmon"),

    Shrimps("Shrimps"),
    Anchovies("Anchovies"),
    Trout("Trout"),
    Salmon("Salmon"),

    Egg("Egg"),
    Pot("Pot"),
    Flour("Flour"),
    Bucket("Bucket"),
    BucketOfMilk("Bucket of milk"),
    Wheat("Wheat"),
    Grain("Grain"),

    Cake("Cake"),
    ChocolateCake("Chocolate cake"),

    IronArrow("Iron arrow"),

    NatureRune("Nature rune"),
    LawRune("Law rune"),
    CosmicRune("Cosmic rune"),
    ChaosRune("Chaos rune"),
    DeathRune("Death rune"),

    StaffOfAir("Staff of air"),
    StaffOfEarth("Staff of earth"),
    StaffOfWater("Staff of water"),
    StaffOfFire("Staff of fire"),

    UncutSapphire("Uncut sapphire"),
    UncutEmerald("Uncut emerald"),
    UncutRuby("Uncut ruby"),
    UncutDiamond("Uncut diamond"),

    BrassKey("Brass key"),
    GiantKey("Giant key"),

    ArrowShaft("Arrow shaft"),
    HeadlessArrow("Headless arrow"),
    BowString("Bow string"),

    MapleLongbowU("Maple longbow (u)"),
    YewShortbowU("Yew shortbow (u)"),
    YewLongbowU("Yew longbow (u)"),

    YewLongbow("Yew longbow"),

    TinOre("Tin ore"),
    CopperOre("Copper ore"),
    IronOre("Iron ore"),

    GrimyGuamLeaf("Grimy guam leaf"),
    GrimyMarrentill("Grimy marrentill"),
    GrimyTarromin("Grimy tarromin"),
    GrimyHarralander("Grimy harralander"),
    GrimyRanarr("Grimy ranarr"),
    GrimyToadflax("Grimy toadflax"),
    GrimyIritLeaf("Grimy irit leaf"),
    GrimyAvantoe("Grimy avantoe"),
    GrimyKwuarm("Grimy kwuarm"),

    GuamLeaf("Guam leaf"),
    Marrentill("Marrentill"),
    Tarromin("Tarromin"),
    Harralander("Harralander"),
    Ranarr("Ranarr"),
    Toadflax("Toadflax"),
    IritLeaf("Irit leaf"),
    Avantoe("Avantoe"),
    Kwuarm("Kwuarm"),

    VialOfWater("Vial of water"),

    ;

    public final String name;
    public final Integer id;

    Items(String name) {
        this.name = name;
        this.id = null;
    }

    Items(String name, Integer id) {
        this.name = name;
        this.id = id;
    }
}
