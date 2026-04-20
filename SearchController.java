/*****************************************************************************************************
SearchController.java
Controller class that handles searches from the GUI and processes Yelp API results.

This class connects:
    - YelpApiClient: makes API requests for businesses in a given city.
    - ResultsPanel: displays the search results in categorized tabs (Hotels, Restaurants, Activities).

It filters out businesses with low reviews, formats the results, and sends them to the ResultsPanel.
*****************************************************************************************************/

import com.google.gson.*;
import java.util.*;

public class SearchController {
    private YelpApiClient apiClient;
    private ResultsPanel resultsPanel;

    // Constructor: links the API client and the results panel
    public SearchController(YelpApiClient apiClient, ResultsPanel resultsPanel) {
        this.apiClient = apiClient;
        this.resultsPanel = resultsPanel;
    }

    // Called when a search is performed
    public void onSearch(String city, List<String> terms) {
        Map<String, String> results = new HashMap<>();
        
        // Get businesses for this category
        for (String term : terms) {
            JsonArray businesses = apiClient.search(city, term);
            StringBuilder sb = new StringBuilder();
            int printed = 0;

            for (JsonElement b : businesses) {
                JsonObject biz = b.getAsJsonObject();
                int reviewCount = biz.has("review_count") ? biz.get("review_count").getAsInt() : 0;
                // Skip businesses with <= 20 reviews to ensure quality results
                if (reviewCount <= 20) continue;

                // Extract business info
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

            // Format the info for the ResultsPanel
            sb.append(name)
            .append("\nImageURL: ").append(biz.has("image_url") ? biz.get("image_url").getAsString() : "")
            .append("\n📍 ").append(address.trim())
            .append("\nRating: ").append(rating)
            .append(" | Reviews: ").append(reviewCount)
            .append("\n\n");

            // Limit to 10 businesses per category
            if (++printed == 10) break;
            }

            results.put(term, sb.toString());
        }

        // Show results in the panel
        resultsPanel.showResults(results);
    }
}