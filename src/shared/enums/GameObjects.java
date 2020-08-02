package shared.enums;

public enum GameObjects {
    Fire("Fire"),
    Cow("Cow"),
    Furnace("Furnace"),
    DairyCow("Dairy cow"),
    FlourBin("Flour bin"),
    LargeDoor("Large door"),
    Ladder("Ladder"),
    Gate("Gate"),
    Trapdoor("Trapdoor"),
    Hopper("Hopper"),
    HopperControls("Hopper controls"),
    ;

    public String name;

    GameObjects(String name) {
        this.name = name;
    }
}
