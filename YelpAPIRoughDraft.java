import java.net.URI;
import java.net.http.*;
import java.io.IOException;
import java.util.Scanner;
import com.google.gson.*;


// Compile with: javac -cp gson-2.10.1.jar YelpAPIRoughDraft.java
// Run with: -cp .:gson-2.10.1.jar YelpAPIRoughDraft
// Need gson-2.10.1.jar in the same directory 


public class YelpAPIRoughDraft {
    public static void main(String[] args) throws IOException, InterruptedException {

        // Get Yelp API key from environment variable
        String apiKey = System.getenv("YELP_API_KEY");

        // If the API key is missing, stop the program
        if (apiKey == null) {
            System.out.println("API key not found! Set YELP_API_KEY environment variable.");
            return;
        }

        // Scanner for reading user input
        Scanner scanner = new Scanner(System.in);

        // Ask user for city
        System.out.print("Enter city: ");
        String city = scanner.nextLine();

        // Ask user for a term
        System.out.print("Enter search term (e.g., restaurants, pizza): ");
        String term = scanner.nextLine();

        // Replace spaces with %20 so the URL is valid
        city = city.replace(" ", "%20");
        term = term.replace(" ", "%20");

        // Build Yelp API request URL
        // limit=20 → get up to 20 businesses
        // sort_by=rating → highest rated businesses first
        String url = "https://api.yelp.com/v3/businesses/search?location=" + city + "&term=" + term + "&limit=20&sort_by=rating";

        // Create HTTP client to send request
        HttpClient client = HttpClient.newHttpClient();

        // Build HTTP GET request with authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        // Send request to Yelp API and store response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Convert the JSON response into a JsonObject using Gson
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

        // Check if the response contains businesses
        if (json.has("businesses") && !json.get("businesses").isJsonNull()) {
            JsonArray businesses = json.getAsJsonArray("businesses");
            
        // Counter to ensure we only print 5 businesses
        int printed = 0;
        
        // Loop through each business returned from Yelp
        for (JsonElement b : businesses) {

            // Convert element to a JSON object
            JsonObject biz = b.getAsJsonObject();

            // Get number of reviews (default = 0 if missing)
            int reviewCount = biz.has("review_count") ? biz.get("review_count").getAsInt() : 0;

            // Skip businesses with 20 or fewer reviews
            if (reviewCount <= 20) {
                continue;
            }
            // Extract business name
            String name = biz.has("name") ? biz.get("name").getAsString() : "N/A";
            
            // Extract business rating
            double rating = biz.has("rating") ? biz.get("rating").getAsDouble() : 0.0;
            
            // Extract price level if available
            String price = biz.has("price") ? biz.get("price").getAsString() : "N/A";

            // Store full address as a single string
            String address = "";

            // Check if location data exists
            if (biz.has("location") && !biz.get("location").isJsonNull()) {
                JsonObject location = biz.getAsJsonObject("location");
                if (location.has("display_address")) {
                    JsonArray displayAddress = location.getAsJsonArray("display_address");
                    for (JsonElement line : displayAddress) {
                        address += line.getAsString() + " ";
                    }
                }
            }

            // Print business information
            System.out.println("Name: " + name);
            System.out.println("Rating: " + rating);
            System.out.println("Price: " + price);
            System.out.println("Reviews: " + reviewCount);
            System.out.println("Address: " + address.trim());
            System.out.println("--------------------------");

            // Increase number of printed businesses
            printed++;

            // Stop at 5 businesses
            if (printed == 5) {
                break;
            }
        }

        // If no businesses were returned
        } else {
            System.out.println("No businesses found for this city/term!");
        }

        scanner.close();
    }
}
