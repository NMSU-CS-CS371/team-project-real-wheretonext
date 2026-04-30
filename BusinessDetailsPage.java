/*****************************************************************************************************
BusinessDetailsPage.java
Displays detailed information about a single business.

This class creates a panel showing the business name, an image if available, 
and the detailed information provided by the Yelp API. It also provides a 
back button to close the details view.

The class is connected to:
    - ResultsPanel: opens this page when the "View →" button is clicked.

*****************************************************************************************************/

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BusinessDetailsPage extends JPanel {

    public BusinessDetailsPage(String info, ItineraryPage itinerary) {
        // Set up layout and background
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String[] lines = info.split("\n");
        String businessName = lines[0];

        // Title
        JLabel title = new JLabel(businessName, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Single scrollable panel for image + info
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Extract image URL from info lines
        String imageUrl = null;
        for (String line : lines) {
            if (line.startsWith("ImageURL:")) {
                imageUrl = line.replace("ImageURL:", "").trim();
                break;
            }
        }

        // keeps the original aspect ratio for images
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                URL url = new URL(imageUrl);
                BufferedImage img = ImageIO.read(url);
                int imgWidth = img.getWidth();
                int imgHeight = img.getHeight();
                int maxWidth = 400;
                int maxHeight = 300;
                double scale = Math.min((double) maxWidth / imgWidth, (double) maxHeight / imgHeight);
                int newWidth = (int) (imgWidth * scale);
                int newHeight = (int) (imgHeight * scale);
                Image scaled = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        contentPanel.add(imageLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Info labels
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Skip the first line (business name) and the image URL line, display the rest
        for (String line : lines) {
            if (line.equals(businessName) || line.startsWith("ImageURL:")) continue;
            JLabel infoLabel = new JLabel(line);
            infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            infoLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoLabel.setForeground(Color.BLACK);
            infoPanel.add(infoLabel);
        }

        contentPanel.add(infoPanel);

        // Make content scrollable
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with just back button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);

        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(new Color(221, 221, 221));
        backButton.setForeground(new Color(51, 51, 51));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}