import java.net.URI;
import java.net.http.*;
import java.io.IOException;
import java.util.Scanner;
import com.google.gson.*;

// Compile with: javac -cp gson-2.8.9.jar YelpAPIRoughDraft.java
// Run with: java -cp .:gson-2.10.1.jar YelpAPIRoughDraft
// Need gson-2.8.9.jar in the same directory 


public class YelpAPIRoughDraft {
    public static void main(String[] args) throws IOException, InterruptedException {


        //API key  
        String apiKey = "CM3X05XSHLW07MREbVm4tT0VuGKI6qSelY5rOo11z6i2Xj8e7ptPqV4VPZ8T2g8-gz_r5HeSRENmlT_nvqjA5sBG1AQK1hdFKlxgUXdsrnTGgIDDgHpKbEkcQA-WaXYx";



        //Code if we want to read API key from environment variable instead of hardcoding it in the code (for security reasons)
/*
        // Read API key from environment variable
        String apiKey = System.getenv("YELP_API_KEY");
        if (apiKey == null) {
            System.out.println("API key not found! Set YELP_API_KEY environment variable.");
            return;
        }
*/

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter city: ");
        String city = scanner.nextLine();

        System.out.print("Enter search term (e.g., restaurants, pizza): ");
        String term = scanner.nextLine();
<<<<<<< Updated upstream

        // Encode spaces
=======
        // Replace spaces with %20 so the URL is valid
>>>>>>> Stashed changes
        city = city.replace(" ", "%20");
        term = term.replace(" ", "%20");

        String url = "https://api.yelp.com/v3/businesses/search?location=" + city + "&term=" + term + "&limit=5";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

        if (json.has("businesses") && !json.get("businesses").isJsonNull()) {
            JsonArray businesses = json.getAsJsonArray("businesses");

            for (JsonElement b : businesses) {
                JsonObject biz = b.getAsJsonObject();

                String name = biz.has("name") ? biz.get("name").getAsString() : "N/A";
                double rating = biz.has("rating") ? biz.get("rating").getAsDouble() : 0.0;
                String price = biz.has("price") ? biz.get("price").getAsString() : "N/A";

                String address = "";
                if (biz.has("location") && !biz.get("location").isJsonNull()) {
                    JsonObject location = biz.getAsJsonObject("location");
                    if (location.has("display_address") && !location.get("display_address").isJsonNull()) {
                        JsonArray displayAddress = location.getAsJsonArray("display_address");
                        for (JsonElement line : displayAddress) {
                            address += line.getAsString() + " ";
                        }
                        address = address.trim();
                    }
                }

                System.out.println("Name: " + name);
                System.out.println("Rating: " + rating);
                System.out.println("Price: " + price);
                System.out.println("Address: " + address);
                System.out.println("--------------------------");
            }
        } else {
            System.out.println("No businesses found for this city/term!");
        }

        scanner.close();
    }
}
