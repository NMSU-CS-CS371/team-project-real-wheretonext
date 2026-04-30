/*****************************************************************************************************
SearchController.java
Controller class that processes search queries and updates the results panel.
This class is responsible for:
    - Receiving search input (city and categories) from the UI.
    - Calling the YelpApiClient to fetch business data based on the search criteria.
    - Formatting the results into a readable string format.
    - Updating the ResultsPanel with the new search results.
The class is connected to:
    - WhereToNextUI: receives search input from the UI and triggers the search process.
    - YelpApiClient: interacts with this class to fetch data from the Yelp API.
    - ResultsPanel: updates this panel with the formatted search results for display.
*****************************************************************************************************/

import com.google.gson.*;
import java.util.*;

public class SearchController {
    private YelpApiClient apiClient;
    private ResultsPanel resultsPanel;

    // Constructor to initialize the API client and results panel
    public SearchController(YelpApiClient apiClient, ResultsPanel resultsPanel) {
        this.apiClient = apiClient;
        this.resultsPanel = resultsPanel;
    }

    // Method to handle search logic based on city and categories
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

                // Extract phone and price
                String phone = biz.has("display_phone") ? biz.get("display_phone").getAsString() : "N/A";
                String price = (biz.has("price") && !biz.get("price").isJsonNull()) ? biz.get("price").getAsString() : "Not listed";

                // Extract categories
                String categories = "";
                if (biz.has("categories")) {
                    for (JsonElement cat : biz.getAsJsonArray("categories")) {
                        categories += cat.getAsJsonObject().get("title").getAsString() + ", ";
                    }
                    if (!categories.isEmpty()) categories = categories.substring(0, categories.length() - 2);
                }

                // Format the business information into a readable string
                sb.append(name)
                    .append("\nImageURL: ").append(biz.has("image_url") ? biz.get("image_url").getAsString() : "")
                    .append("\nRating: ").append(rating)
                    .append("\n📍 ").append(address.trim())
                    .append("\n📞 ").append(phone)
                    .append("\n💲 Price: ").append(price)
                    .append("\n⭐ Reviews: ").append(reviewCount)
                    .append("\n\n");

                if (++printed == 10) break;
            }

            results.put(term, sb.toString());
        }

        resultsPanel.showResults(results);
    }
}