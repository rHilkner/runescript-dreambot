package shared.enums;

public enum Items {
    FEATHERS("Feathers", 314),
    BONES("Bones", 526);

    public String name;
    public int id;

    Items(String name, int id) {
        this.name = name;
        this.id = id;
    }
}