import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URI;
import java.net.http.*;
import java.io.IOException;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;

// Custom panel to paint a background image stretched to fill the window
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String path) {
        try {
            backgroundImage = Toolkit.getDefaultToolkit().getImage(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class WhereToNextUI extends JFrame {

    public WhereToNextUI() {
        setTitle("WhereToNext");
        setSize(900, 700); // smaller window
        setLocationRelativeTo(null); // center window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with background
        BackgroundPanel mainPanel = new BackgroundPanel("images/background.jpg");
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Spacer to move title down
        mainPanel.add(Box.createVerticalStrut(80));

        // Title label
        JLabel titleLabel = new JLabel("🌍  Where To Next?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(50));

        // Input panel with solid background
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(255, 255, 255, 220)); // solid background
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.setMaximumSize(new Dimension(500, 400));

        // Prompt label
        JLabel promptLabel = new JLabel("Enter your destination city:");
        promptLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text field
        JTextField cityField = new JTextField();
        cityField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        cityField.setMaximumSize(new Dimension(400, 40));
        cityField.setHorizontalAlignment(JTextField.CENTER);

        // Checkboxes
        JCheckBox hotelsCheckBox = new JCheckBox("Hotels");
        JCheckBox restaurantsCheckBox = new JCheckBox("Restaurants");
        JCheckBox eventsCheckBox = new JCheckBox("Events");

        hotelsCheckBox.setSelected(true); // auto-select hotels
        hotelsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        restaurantsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Search button
        JButton goButton = new JButton("Let's Go →");
        goButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        goButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to input panel
        inputPanel.add(promptLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(cityField);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(hotelsCheckBox);
        inputPanel.add(restaurantsCheckBox);
        inputPanel.add(eventsCheckBox);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(goButton);

        // Add input panel to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
        setVisible(true);

        // Action listener for search
        ActionListener searchAction = e -> {
            String city = cityField.getText().trim();
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a city!");
                return;
            }
            List<String> terms = new ArrayList<>();
            if (hotelsCheckBox.isSelected()) terms.add("hotels");
            if (restaurantsCheckBox.isSelected()) terms.add("restaurants");
            if (eventsCheckBox.isSelected()) terms.add("events");

            if (terms.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one category!");
                return;
            }

            fetchTopBusinesses(city, terms);
        };

        goButton.addActionListener(searchAction);

        // Trigger search on Enter key
        cityField.addActionListener(searchAction);
    }

    private void showResultsUI(String resultsText) {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(new Color(123, 50, 250));

        JLabel title = new JLabel("Top Results", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        resultsPanel.add(title, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(resultsText);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(resultsPanel);
        revalidate();
    }

    private void fetchTopBusinesses(String city, List<String> terms) {
        String apiKey = "08ZFB6tYGsw2aek1E-PKQlME7pCTqnwwEe8qiDBa_JTmFUgS7IzHCgAxCYh2UF0MGdCKXHR_8qlMLuUUQQ3j_Si1cJgNoeV8liAmgNhrnOknAaVOlJXgy1iZa6bBaXYx";


        StringBuilder results = new StringBuilder();

        for (String term : terms) {
            try {
                String url = "https://api.yelp.com/v3/businesses/search?location="
                        + city.replace(" ", "%20")
                        + "&term=" + term
                        + "&limit=50&sort_by=rating";

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + apiKey)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                if (json.has("businesses")) {
                    JsonArray businesses = json.getAsJsonArray("businesses");
                    results.append("---- Top ").append(term).append(" in ").append(city).append(" ----\n");

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
                                JsonArray displayAddress = location.getAsJsonArray("display_address");
                                for (JsonElement line : displayAddress) {
                                    address += line.getAsString() + " ";
                                }
                            }
                        }

                        results.append(name)
                               .append(" | Rating: ").append(rating)
                               .append(" | Reviews: ").append(reviewCount)
                               .append("\n📍 ").append(address.trim())
                               .append("\n\n");

                        printed++;
                        if (printed == 10) break;
                    }
                    results.append("\n");
                } else {
                    results.append("No ").append(term).append(" found in ").append(city).append("\n\n");
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        showResultsUI(results.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhereToNextUI());
    }
}