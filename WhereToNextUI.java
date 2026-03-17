import java.awt.*;              // GUI layouts, colors, fonts
import javax.swing.*;          // Swing UI components (JFrame, JButton, etc.)
import java.net.URI;           // For building URLs
import java.net.http.*;        // Java HTTP client for sending requests
import java.io.IOException;    // Handle input/output exceptions
import com.google.gson.*;      // Parse JSON responses

public class WhereToNextUI extends JFrame {

    // ────────────────────────────────────────────────
    // Constructor → Builds the first screen (city input)
    // ────────────────────────────────────────────────
    public WhereToNextUI() {

        // ── Window setup ──
        setTitle("WhereToNext");                   // Window title
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Open fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit app when closed

        // ── Main panel (root container for all UI) ──
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));  // Light gray
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // Padding

        // ── Title label at the top ──
        JLabel titleLabel = new JLabel("🌍  Where To Next?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50)); // Large font
        mainPanel.add(titleLabel, BorderLayout.NORTH); // Place at top

        // ── Center panel for input (city field + button) ──
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Stack vertically
        centerPanel.setBackground(new Color(245, 245, 245));

        // Prompt label
        JLabel promptLabel = new JLabel("Enter your destination city:");
        promptLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Text field for user to type city
        JTextField cityField = new JTextField();
        cityField.setFont(new Font("SansSerif", Font.PLAIN, 20)); // Larger text
        cityField.setMaximumSize(new Dimension(600, 50));          // Limit width
        cityField.setHorizontalAlignment(JTextField.CENTER);      // Center text

        // Button to fetch top businesses
        JButton goButton = new JButton("Let's Go →");
        goButton.setMaximumSize(new Dimension(220, 55));          // Button size
        goButton.setFont(new Font("SansSerif", Font.BOLD, 18));  // Bold text
        goButton.setAlignmentX(Component.CENTER_ALIGNMENT);      // Center button

        // Add components with spacing
        centerPanel.add(Box.createVerticalGlue());               // Push content toward center
        centerPanel.add(promptLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space
        centerPanel.add(cityField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        centerPanel.add(goButton);
        centerPanel.add(Box.createVerticalGlue());

        // Add center panel to main panel
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add main panel to the JFrame
        add(mainPanel);
        setVisible(true); // Show window

        // ── Button click action ──
        goButton.addActionListener(e -> {
            String city = cityField.getText().trim(); // Get typed city
            if (city.isEmpty()) {                    // Check for empty input
                JOptionPane.showMessageDialog(this, "Please enter a city!");
                return;
            }
            fetchTopBusinesses(city);               // Call API method
        });
    }

    // ────────────────────────────────────────────────
    // Show results screen
    // Replace input screen with a scrollable text area
    // ────────────────────────────────────────────────
    private void showResultsUI(String resultsText) {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(new Color(245, 245, 245));

        // Title at top
        JLabel title = new JLabel("Top Results", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        resultsPanel.add(title, BorderLayout.NORTH);

        // Scrollable text area to display results
        JTextArea textArea = new JTextArea(resultsText);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Keep columns aligned
        textArea.setEditable(false);    // User cannot edit
        textArea.setLineWrap(true);     // Wrap long lines
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Swap the content to the results panel
        setContentPane(resultsPanel);
        revalidate(); // Refresh the window
    }

    // ────────────────────────────────────────────────
    // Fetch businesses from Yelp API
    // - Sends request for each category (hotels, food, activities)
    // - Filters out low-rated or low-review businesses
    // - Keeps only top 5 per category
    // - Extracts name, rating, reviews, address
    // ────────────────────────────────────────────────
    private void fetchTopBusinesses(String city) {

        String apiKey = System.getenv("YELP_API_KEY"); // Get API key
        if (apiKey == null) {
            JOptionPane.showMessageDialog(this, "API key not found!");
            return;
        }

        String[] terms = {"hotels", "food", "activities"}; // Search categories
        StringBuilder results = new StringBuilder();       // Store final text

        // Loop through each category
        for (String term : terms) {
            try {
                // Build API URL
                String url = "https://api.yelp.com/v3/businesses/search?location="
                        + city.replace(" ", "%20")  // Encode spaces
                        + "&term=" + term
                        + "&limit=50&sort_by=rating"; // Get up to 50, sorted by rating

                // Create HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + apiKey) // Auth header
                        .GET()
                        .build();

                // Send request and get response as string
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse JSON
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                if (json.has("businesses")) {
                    JsonArray businesses = json.getAsJsonArray("businesses");

                    results.append("---- Top ").append(term).append(" in ").append(city).append(" ----\n");

                    int printed = 0; // Limit top 5

                    for (JsonElement b : businesses) {
                        JsonObject biz = b.getAsJsonObject();

                        // Get review count
                        int reviewCount = biz.has("review_count") ? biz.get("review_count").getAsInt() : 0;
                        if (reviewCount <= 50) continue; // Skip if too few reviews

                        // Get name
                        String name = biz.has("name") ? biz.get("name").getAsString() : "N/A";

                        // Get rating
                        double rating = biz.has("rating") ? biz.get("rating").getAsDouble() : 0.0;

                        // Get full address
                        String address = "";
                        if (biz.has("location") && !biz.get("location").isJsonNull()) {
                            JsonObject location = biz.getAsJsonObject("location");
                            if (location.has("display_address")) {
                                JsonArray displayAddress = location.getAsJsonArray("display_address");
                                for (JsonElement line : displayAddress) {
                                    address += line.getAsString() + " ";
                                }
                            }
                        }

                        // Add to results string
                        results.append(name)
                               .append(" | Rating: ").append(rating)
                               .append(" | Reviews: ").append(reviewCount)
                               .append("\n📍 ").append(address.trim())
                               .append("\n\n");

                        printed++;
                        if (printed == 5) break; // Stop after 5
                    }
                    results.append("\n");

                } else {
                    results.append("No ").append(term).append(" found in ").append(city).append("\n\n");
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace(); // Print errors if API fails
            }
        }

        // Show all results in UI
        showResultsUI(results.toString());
    }

    // ────────────────────────────────────────────────
    // Main entry point
    // ────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhereToNextUI()); // Start UI on correct thread
    }
}