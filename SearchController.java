// SearchController.java
import com.google.gson.*;
import java.util.*;

public class SearchController {
    private YelpApiClient apiClient;
    private ResultsPanel resultsPanel;

    public SearchController(YelpApiClient apiClient, ResultsPanel resultsPanel) {
        this.apiClient = apiClient;
        this.resultsPanel = resultsPanel;
    }

    public void onSearch(String city, List<String> terms) {
        Map<String, String> results = new HashMap<>();

        for (String term : terms) {
            JsonArray businesses = apiClient.search(city, term);
            StringBuilder sb = new StringBuilder();
            int printed = 0;

            for (JsonElement b : businesses) {
                JsonObject biz = b.getAsJsonObject();
                int reviewCount = biz.has("review_count") ? biz.get("review_count").getAsInt() : 0;
                if (reviewCount <= 20) continue;

                String name = biz.has("name") ? biz.get("name").getAsString() : "N/A";
                double rating = biz.has("rating") ? biz.get("rating").getAsDouble() : 0.0;
                String address = "";
                if (biz.has("location") && !biz.get("location").isJsonNull()) {
                    JsonObject location = biz.getAsJsonObject("location");
                    if (location.has("display_address")) {
                        for (JsonElement line : location.getAsJsonArray("display_address")) {
                            address += line.getAsString() + " ";
                        }
                    }
                }

            sb.append(name)
            .append("\nImageURL: ").append(biz.has("image_url") ? biz.get("image_url").getAsString() : "")
            .append("\n📍 ").append(address.trim())
            .append("\nRating: ").append(rating)
            .append(" | Reviews: ").append(reviewCount)
            .append("\n\n");

                if (++printed == 10) break;
            }

            results.put(term, sb.toString());
        }

        resultsPanel.showResults(results);
    }
}