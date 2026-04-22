/*****************************************************************************************************
WhereToNextUI.java
Main GUI class for the WhereToNext application.
 
This class creates the main window where users can enter a city to search for hotels, restaurants,
and activities. It connects with the following classes:
    - YelpApiClient: handles the API requests to Yelp for search results.
    - SearchController: processes searches and filters results.
    - ResultsPanel: displays search results in tabs and allows viewing details.
    - BackgroundPanel: displays the background image for the main panel.

 The class sets up the layout, input fields, buttons, and handles user interactions.
*****************************************************************************************************/

// Imports 
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class WhereToNextUI extends JFrame {

    public WhereToNextUI() {
        // Set up main window
        setTitle("WhereToNext");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with background image
        BackgroundPanel mainPanel = new BackgroundPanel("images/background2.jpg");
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalStrut(80));

        // Title label
        JLabel titleLabel = new JLabel("Where To Next?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(50));

        // Input panel for city entry
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(255, 255, 255, 220));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.setMaximumSize(new Dimension(500, 400));

        // Label prompting user to enter city
        JLabel promptLabel = new JLabel("Enter your destination city:");
        promptLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text field for city input
        JTextField cityField = new JTextField();
        cityField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        cityField.setMaximumSize(new Dimension(400, 40));
        cityField.setHorizontalAlignment(JTextField.CENTER);

        // Button to start search
        JButton goButton = new JButton("Let's Go →");
        goButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        goButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to input panel
        inputPanel.add(promptLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(cityField);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(goButton);

        // Add input panel to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalGlue());
        add(mainPanel);
        setVisible(true);

        // Set up Yelp API client, results panel, and search controller
        YelpApiClient apiClient = new YelpApiClient("08ZFB6tYGsw2aek1E-PKQlME7pCTqnwwEe8qiDBa_JTmFUgS7IzHCgAxCYh2UF0MGdCKXHR_8qlMLuUUQQ3j_Si1cJgNoeV8liAmgNhrnOknAaVOlJXgy1iZa6bBaXYx");
        ItineraryPage itineraryPage = new ItineraryPage(this);
        ResultsPanel resultsPanel = new ResultsPanel(this, mainPanel, itineraryPage);
        itineraryPage.setPreviousPanel(resultsPanel);
        SearchController controller = new SearchController(apiClient, resultsPanel);
        
        // Action for searching when user clicks button or presses Enter
        ActionListener searchAction = e -> {
            String city = cityField.getText().trim();
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a city!");
                return;
            }

            // Categories to search
            List<String> terms = List.of("hotels", "restaurants", "activities");

            // Perform search and update results panel
            controller.onSearch(city, terms);
            resultsPanel.setCity(city);
            setContentPane(resultsPanel);
            revalidate();
        };

        // Attach search action to button and text field
        goButton.addActionListener(searchAction);
        cityField.addActionListener(searchAction);
    }

    public static void main(String[] args) {
        // Start GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new WhereToNextUI());
    }
}