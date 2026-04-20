/*****************************************************************************************************
ResultsPanel.java
This class is used to display the search results for hotels, restaurants, and activities.
It shows the results in tabs and allows users to view details of each business.
 
Connections with other classes:
    - WhereToNextUI: switches to this panel after a search is made.
    - BusinessDetailsPage: opens a new window with detailed info when "View" is clicked.
    - SearchController: passes the search results here to display.
    - YelpApiClient: indirectly provides the data that shows up here.
****************************************************************************************************/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ResultsPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel hotelsPanel;
    private JPanel restaurantsPanel;
    private JPanel activitiesPanel;
    private JLabel cityLabel;
    private ItineraryPage itinerary;

    

    // Constructor: set up layout, top bar, tabs, and back button
    public ResultsPanel(JFrame parent, JPanel mainPanel, ItineraryPage itinerary) {
        this.itinerary = itinerary;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top bar with back button and title
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);

        // Back button to go to main panel
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            parent.setContentPane(mainPanel);
            parent.revalidate();
        });
        topBar.add(backButton, BorderLayout.WEST);

        // Itinerary button
        JButton itineraryBtn = new JButton("📋 Itinerary");
        itineraryBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        itineraryBtn.setForeground(new Color(50, 120, 200));
        itineraryBtn.setBackground(new Color(235, 244, 255));
        itineraryBtn.setOpaque(true);
        itineraryBtn.setBorderPainted(false);
        itineraryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itineraryBtn.addActionListener(e -> {
            parent.setContentPane(itinerary);
            parent.revalidate();
        });

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftButtons.setBackground(Color.WHITE);
        leftButtons.add(backButton);
        leftButtons.add(Box.createHorizontalStrut(8));
        leftButtons.add(itineraryBtn);

        topBar.add(leftButtons, BorderLayout.WEST);


        // Title of results
        JLabel title = new JLabel("Top Results", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        topBar.add(title, BorderLayout.CENTER);

        // City label under title
        cityLabel = new JLabel("", SwingConstants.CENTER);
        cityLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        cityLabel.setForeground(new Color(50, 120, 200));
        topBar.add(cityLabel, BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);

        // Tabs for Hotels, Restaurants, Activities
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);

        hotelsPanel = createListPanel();
        restaurantsPanel = createListPanel();
        activitiesPanel = createListPanel();

        JScrollPane hotelsScroll = new JScrollPane(hotelsPanel);
        hotelsScroll.getVerticalScrollBar().setUnitIncrement(20); // increase scroll speed
        
        JScrollPane restaurantsScroll = new JScrollPane(restaurantsPanel);
        restaurantsScroll.getVerticalScrollBar().setUnitIncrement(20);
        
        JScrollPane activitiesScroll = new JScrollPane(activitiesPanel);
        activitiesScroll.getVerticalScrollBar().setUnitIncrement(20);
        
        tabbedPane.addTab("Hotels", hotelsScroll);
        tabbedPane.addTab("Restaurants", restaurantsScroll);
        tabbedPane.addTab("Activities", activitiesScroll);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Update city name label
    public void setCity(String city) {
        cityLabel.setText(city);
    }

    // Helper method to create a panel for a tab
    private JPanel createListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    // Show the results passed from SearchController
    public void showResults(Map<String, String> resultsByCategory) {
        hotelsPanel.removeAll();
        restaurantsPanel.removeAll();
        activitiesPanel.removeAll();

        addButtonsToPanel(hotelsPanel, resultsByCategory.getOrDefault("hotels", ""));
        addButtonsToPanel(restaurantsPanel, resultsByCategory.getOrDefault("restaurants", ""));
        addButtonsToPanel(activitiesPanel, resultsByCategory.getOrDefault("activities", ""));

        revalidate();
        repaint();
    }
    
    // Add each business as a card with a thumbnail, name, details, and a "View" button
    private void addButtonsToPanel(JPanel panel, String data) {
        panel.removeAll();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        String[] businesses = data.split("\n\n");
        for (String bizInfo : businesses) {
            String[] lines = bizInfo.split("\n");
            String name = lines.length > 0 ? lines[0] : "Unknown";
            String details = "";
            String extra = "";
            String imageUrl = "";

            // Extract details and image URL
            for (String line : lines) {
                if (line.startsWith("ImageURL:")) {
                    imageUrl = line.replace("ImageURL:", "").trim();
                    continue;
                }
                if (details.isEmpty() && !line.equals(name)) {
                    details = line;
                } else if (extra.isEmpty() && !line.equals(name) && !line.equals(details)) {
                    extra = line;
                }
            }

            // Card panel for each business
            JPanel card = new JPanel();
            card.setLayout(new BorderLayout(12, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 0, 6, 0),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                )
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            card.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Thumbnail image
            JLabel thumbLabel = new JLabel();
            thumbLabel.setPreferredSize(new Dimension(80, 70));
            thumbLabel.setHorizontalAlignment(SwingConstants.CENTER);
            thumbLabel.setBackground(new Color(240, 240, 240));
            thumbLabel.setOpaque(true);

            if (!imageUrl.isEmpty()) {
                final String finalUrl = imageUrl;
                new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        BufferedImage img = ImageIO.read(new URL(finalUrl));
                        Image scaled = img.getScaledInstance(80, 70, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                    @Override
                    protected void done() {
                        try {
                            thumbLabel.setIcon(get());
                            thumbLabel.revalidate();
                            thumbLabel.repaint();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            }

            card.add(thumbLabel, BorderLayout.WEST);

            // Name, details, extra info
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            nameLabel.setForeground(new Color(30, 30, 30));

            JLabel detailsLabel = new JLabel(details);
            detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            detailsLabel.setForeground(new Color(120, 120, 120));

            // Right panel with extra info and "View" button
            JLabel extraLabel = new JLabel(extra);
            extraLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            extraLabel.setForeground(new Color(50, 120, 200));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(Color.WHITE);
            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(detailsLabel);

            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(Color.WHITE);
            rightPanel.add(extraLabel, BorderLayout.NORTH);

            JButton viewBtn = new JButton("View →");
            viewBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            viewBtn.setForeground(new Color(50, 120, 200));
            viewBtn.setBackground(new Color(235, 244, 255));
            viewBtn.setBorderPainted(false);
            viewBtn.setOpaque(true);
            viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Open business details in a new window
            viewBtn.addActionListener(e -> {
                
BusinessDetailsPage detailsPage = new BusinessDetailsPage(bizInfo, itinerary);
                JFrame frame = new JFrame("Business Details");
                frame.setContentPane(detailsPage);
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
            rightPanel.add(viewBtn, BorderLayout.SOUTH);

            card.add(textPanel, BorderLayout.CENTER);
            card.add(rightPanel, BorderLayout.EAST);

            // Mouse hover effect
            card.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(248, 250, 255));
                    textPanel.setBackground(new Color(248, 250, 255));
                    rightPanel.setBackground(new Color(248, 250, 255));
                }
                public void mouseExited(MouseEvent e) {
                    card.setBackground(Color.WHITE);
                    textPanel.setBackground(Color.WHITE);
                    rightPanel.setBackground(Color.WHITE);
                }
            });

            panel.add(card);
        }
    }
}
