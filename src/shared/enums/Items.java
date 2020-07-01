package shared.enums;

public enum Items {
    FEATHERS("Feathers", 314),
    BONES("Bones", 526),
    BLACK_BEAD("", 1),
    WHITE_BEAD("", 1),
    RED_BEAD("", 1),
    YELLOW_BEAD("", 1),
    BLUE_BEAD("", 1);

    public String name;
    public int id;

    Items(String name, int id) {
        this.name = name;
        this.id = id;
    }
}