package shared.enums;

public enum Items {
    Coins("Coins"),
    PotOfFlour("Pot of flour"),
    JugOfWater("Jug of water"),
    PizzaBase("Pizza base"),
    ChocolateBar("Chocolate bar"),
    ChocolateDust("Chocolate dust"),
    GoldBar("Gold bar");

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
