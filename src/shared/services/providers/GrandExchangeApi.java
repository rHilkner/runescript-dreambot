package shared.services.providers;

import scriptz.RunescriptAbstractContext;

import java.io.IOException;
import java.io.InputStream;
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

    private static GrandExchangeApi instance;
    private static RunescriptAbstractContext ctx;

    private static final String API_LOCATION = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=%d";
    private static final long TEN_MINUTES = 600000;
    private final Map<Integer, GELookupResult> cache;

    /**
     * Caching-enabled default constructor
     */
    private GrandExchangeApi() {
        this(true);
        ctx = RunescriptAbstractContext.ctx;
    }

    /**
     * Creates a new Grand Exchange API instance. Starts cache-refresh timer.
     *
     * @param cache Whether to enable caching of results.
     */
    private GrandExchangeApi(boolean cache) {
        this.cache = cache ? new HashMap<>() : null;
    }

    public static GrandExchangeApi getInstance() {
        if (instance == null)
            instance = new GrandExchangeApi();
        return instance;
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
                ctx.logScript("Found result for item with id [" + itemId + "] on GE API: " + result);
                return result;
            }
        }


        String json = null;
        try {
            URL url = new URL(String.format(API_LOCATION, itemId));
            InputStream inputStream = url.openStream();
            Scanner scan = new Scanner(inputStream).useDelimiter("\\A");
            ctx.logScript("inputStream: " + inputStream);
            if (scan.hasNext()) {
                json = scan.next();
            }
            scan.close();
        } catch (IOException e) {
            return null;
        }

        GELookupResult result = null;
        if (json != null) {
            result = parse(itemId, json);
        }

        if (cache != null && result != null) {
            cache.put(itemId, result);
        }

        ctx.logScript("Found result for item with id [" + itemId + "] on GE API: " + result);
        return result;
    }

    /**
     * If caching is enabled, clears the cache so that new results are fetched on lookup.
     */
    private void flushCache() {
        if (cache == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        cache.forEach((id, geLookupResult) -> {
            if ((currentTime - TEN_MINUTES) > geLookupResult.lastUpdatedAt) {
                cache.remove(id); // item is old. it needs to be refreshed
            }
        });
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
        Matcher matcher = pattern.matcher(json);
        Map<String, String> results = new HashMap<>();

        while (matcher.find()) {
            results.put(matcher.group("key"), matcher.group("value"));
        }

        int price = 0;
        Matcher priceMatcher = Pattern.compile("\"price\":(?<price>\\d+)").matcher(json);
        if (priceMatcher.find()) {
            price = Integer.parseInt(priceMatcher.group("price"));
        }

        return new GELookupResult(results.get("icon"), results.get("icon_large"), results.get("type"), results.get("typeIcon"),
                results.get("name"), results.get("description"), Boolean.parseBoolean(results.get("members")), itemId, price);
    }
}