package shared.enums;

public enum FishingType {
    SmallFishingNet("Small Net", "Small fishing net");

    private String interactionType;
    private String equipmentName;

    FishingType(String interactionType, String equipmentName) {
        this.interactionType = interactionType;
        this.equipmentName = equipmentName;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public String getEquipmentName() {
        return equipmentName;
    }
}
