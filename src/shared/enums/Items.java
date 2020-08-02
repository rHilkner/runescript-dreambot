package shared.enums;

public enum Items {
    Coins("Coins"),
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

    PotOfFlour("Pot of flour"),
    JugOfWater("Jug of water"),
    PizzaBase("Pizza base"),

    ChocolateBar("Chocolate bar"),
    ChocolateDust("Chocolate dust"),
    Knife("Knife"),

    GoldBar("Gold bar"),

    Emerald("Emerald"),

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
