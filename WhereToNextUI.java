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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class WhereToNextUI extends JFrame {

    public WhereToNextUI() {
        setTitle("WhereToNext");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel mainPanel = new BackgroundPanel("images/background2.jpg");
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalStrut(80));

        JLabel titleLabel = new JLabel("Where To Next?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(50));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(255, 255, 255, 220));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.setMaximumSize(new Dimension(500, 400));

        JLabel promptLabel = new JLabel("Enter your destination city:");
        promptLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField cityField = new JTextField();
        cityField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        cityField.setMaximumSize(new Dimension(400, 40));
        cityField.setHorizontalAlignment(JTextField.CENTER);

        JLabel daysLabel = new JLabel("Select number of days:");
        daysLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        daysLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Integer[] dayOptions = {1, 2, 3, 4, 5, 6, 7};
        JComboBox<Integer> daysDropdown = new JComboBox<>(dayOptions);
        daysDropdown.setFont(new Font("SansSerif", Font.PLAIN, 18));
        daysDropdown.setMaximumSize(new Dimension(100, 30));
        daysDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton goButton = new JButton("Let's Go →");
        goButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        goButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // View Itinerary button
        JButton viewItineraryBtn = new JButton("📋 View My Itinerary");
        viewItineraryBtn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        viewItineraryBtn.setForeground(new Color(50, 120, 200));
        viewItineraryBtn.setBackground(new Color(235, 244, 255));
        viewItineraryBtn.setOpaque(true);
        viewItineraryBtn.setBorderPainted(false);
        viewItineraryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewItineraryBtn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        inputPanel.add(promptLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(cityField);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(daysLabel);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(daysDropdown);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(goButton);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(viewItineraryBtn);
        inputPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalGlue());
        add(mainPanel);
        setVisible(true);

        YelpApiClient apiClient = new YelpApiClient("32abfY09xBAtrWF5QIVDaSht--bcVmUFrudLnR1iXccggHVYovU1Do3TD3uAV6ZL4ppOzv3-aIZjAshQUJcF8eeuDuE0QGXUKhG8GgboB1P_W6BYIOkvp1MY_LnvaXYx");
        ItineraryPage itineraryPage = new ItineraryPage(this);
        ResultsPanel resultsPanel = new ResultsPanel(this, mainPanel, itineraryPage);
        itineraryPage.setPreviousPanel(resultsPanel);
        SearchController controller = new SearchController(apiClient, resultsPanel);

        // View Itinerary button goes directly to itinerary from main screen
        // Back from itinerary will return to main panel in this case
        viewItineraryBtn.addActionListener(e -> {
            itineraryPage.setPreviousPanel(mainPanel);
            setContentPane(itineraryPage);
            revalidate();
        });

        ActionListener searchAction = e -> {
            String city = cityField.getText().trim();
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a city!");
                return;
            }

            List<String> terms = List.of("hotels", "restaurants", "activities");
            int days = (Integer) daysDropdown.getSelectedItem();
            resultsPanel.setDays(days);

            controller.onSearch(city, terms);
            resultsPanel.setCity(city);

            // When coming from search, itinerary back button returns to results
            itineraryPage.setPreviousPanel(resultsPanel);

            setContentPane(resultsPanel);
            revalidate();
        };

        goButton.addActionListener(searchAction);
        cityField.addActionListener(searchAction);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhereToNextUI());
    }
}