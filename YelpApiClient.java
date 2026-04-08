/*****************************************************************************************************
YelpApiClient.java
Handles requests to the Yelp API for business search.

This class:
    - Uses an API key to authenticate requests.
    - Sends a search to Yelp for a given city and term (hotels, restaurants, activities).
    - Returns results as a JsonArray and passed to SearchController to process and display.

*****************************************************************************************************/

import java.net.URI;
import java.net.http.*;
import java.io.IOException;
import com.google.gson.*;

public class YelpApiClient {
    private String apiKey;
    private HttpClient client;

    // Constructor: sets the API key and creates a client
    public YelpApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
    }

    // Perform a search on Yelp for a given city and term
    public JsonArray search(String city, String term) {
        try {
            String url = "https://api.yelp.com/v3/businesses/search?location="
                    + city.replace(" ", "%20")
                    + "&term=" + term
                    + "&limit=50&sort_by=rating";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            if (json.has("businesses")) {
                return json.getAsJsonArray("businesses");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
        // Return empty array if there was an error
        return new JsonArray();
    }
}