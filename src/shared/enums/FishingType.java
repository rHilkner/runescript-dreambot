package shared.enums;

public enum FishingType {
    SmallNet("Fishing spot", "Small Net", "Small fishing net"),
    FlyFishingRod("Rod Fishing spot", "Lure", "Fly fishing rod");

    private String fishingSpot;
    private String interactionType;
    private String equipmentName;

    FishingType(String fishingSpot, String interactionType, String equipmentName) {
        this.fishingSpot = fishingSpot;
        this.interactionType = interactionType;
        this.equipmentName = equipmentName;
    }

    public String getFishingSpot() {
        return fishingSpot;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public String getEquipmentName() {
        return equipmentName;
    }
}
