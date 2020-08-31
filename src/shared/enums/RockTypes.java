package shared.enums;

public enum RockTypes {
    Clay("Clay", "Clay", 6705),
    Tin("Tin", "Tin ore", 53),
    Copper("Copper","Copper ore", 0000),
    Iron("Iron", "Iron ore", 2576),
    ;

    public String rockName;
    public String oreName;
    public Integer modelColor;

    RockTypes(String rockName, String oreName, Integer modelColor) {
        this.rockName = rockName;
        this.oreName = oreName;
        this.modelColor = modelColor;
    }
}
