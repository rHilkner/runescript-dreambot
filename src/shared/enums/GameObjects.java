package shared.enums;

public enum GameObjects {
    Fire("Fire"),
    Cow("Cow"),
    Furnace("Furnace"),
    ;

    public String name;

    GameObjects(String name) {
        this.name = name;
    }
}
