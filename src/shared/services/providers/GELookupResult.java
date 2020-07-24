package shared.services.providers;

/**
 * A class representing a result from an API lookup.
 */
public final class GELookupResult {

    public final String smallIconUrl, largeIconUrl, type, typeIcon, name, itemDescription;
    public final boolean isMembers;
    public final int id, price;
    public final long lastUpdatedAt;

    public GELookupResult(String smallIconUrl, String largeIconUrl, String type, String typeIcon, String name, String itemDescription,
                          boolean isMembers, int id, int price) {
        this.smallIconUrl = smallIconUrl;
        this.largeIconUrl = largeIconUrl;
        this.type = type;
        this.typeIcon = typeIcon;
        this.name = name;
        this.itemDescription = itemDescription;
        this.isMembers = isMembers;
        this.id = id;
        this.price = price;
        this.lastUpdatedAt = System.currentTimeMillis();
    }
}