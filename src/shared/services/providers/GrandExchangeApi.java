package shared.services.providers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for access to the Grand Exchange API.
 */
public class GrandExchangeApi {

    private static final String API_LOCATION = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=%d";
    private static final long TEN_MINUTES = 600000;
    private final Map<Integer, GELookupResult> cache;

    /**
     * Caching-enabled default constructor
     */
    public GrandExchangeApi() {
        this(true);
    }

    /**
     * Creates a new Grand Exchange API instance. Starts cache-refresh timer.
     *
     * @param cache Whether to enable caching of results.
     */
    private GrandExchangeApi(boolean cache) {
        this.cache = cache ? new HashMap<>() : null;
    }

    /**
     * Looks up an item using the Grand Exchange API. This method blocks while waiting for the API result.
     *
     * @param itemId the id to look up.
     * @return the result returned by the api. May be null if an error has occurred.
     */
    public GELookupResult lookup(int itemId) {
        flushCache();
        if (cache != null && !cache.isEmpty()) {
            GELookupResult result = cache.get(itemId);
            if (result != null) {
                return result;
            }
        }
        String json;
        try {
            URL url = new URL(String.format(API_LOCATION, itemId));
            Scanner scan = new Scanner(url.openStream()).useDelimiter("\\A");
            json = scan.next();
            scan.close();
        } catch (IOException e) {
            return null;
        }
        GELookupResult result = parse(itemId, json);
        if (cache != null) {
            cache.put(itemId, result);
        }
        return result;
    }

    /**
     * If caching is enabled, clears the cache so that new results are fetched on lookup.
     */
    private void flushCache() {
        long currentTime = System.currentTimeMillis();
        if (cache != null) {
            cache.forEach((id, geLookupResult) -> {
                if ((currentTime - TEN_MINUTES) > geLookupResult.lastUpdatedAt) {
                    cache.remove(id); // item is old. it needs to be refreshed
                }
            });
        }
    }

    /**
     * Parses a GELookupResult from the JSON returned by the API.
     *
     * @param itemId The item ID.
     * @param json   The JSON returned by the RuneScape's API.
     * @return The serialized result.
     */
    private static GELookupResult parse(int itemId, String json) {
        Pattern pattern = Pattern.compile("\"(?<key>[^\"]+)\":\"(?<value>[^\"]+)\"");
        Matcher m = pattern.matcher(json);
        Map<String, String> results = new HashMap<>();
        while (m.find()) {
            results.put(m.group("key"), m.group("value"));
        }
        int price = 0;
        Matcher priceMatcher = Pattern.compile("\"price\":(?<price>\\d+)").matcher(json);
        if (priceMatcher.find()) {
            price = Integer.parseInt(priceMatcher.group("price"));
        }
        return new GELookupResult(
                results.get("icon"),
                results.get("icon_large"),
                results.get("type"),
                results.get("typeIcon"),
                results.get("name"),
                results.get("description"),
                Boolean.parseBoolean(results.get("members")),
                itemId,
                price
        );
    }

    /**
     * A class representing a result from an API lookup.
     */
    public static final class GELookupResult {

        final String smallIconUrl, largeIconUrl, type, typeIcon, name, itemDescription;
        final boolean isMembers;
        final int id, price;
        final long lastUpdatedAt;

        private GELookupResult(String smallIconUrl, String largeIconUrl, String type, String typeIcon, String name, String itemDescription,
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
}