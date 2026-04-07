import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BusinessDetailsPage extends JPanel {

    public BusinessDetailsPage(String info) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // white background

        // Split info to extract first line as name
        String[] lines = info.split("\n");
        String businessName = lines[0];

        // Title
        JLabel title = new JLabel(businessName, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Panel for image and details
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // If image_url is in the info (assume second line), load it
        // You might want to pass image_url separately from YelpApiClient for real data
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);

        // Example: extract image_url from the info string if you append it in YelpApiClient
        String imageUrl = null; // default no image
        for (String line : lines) {
            if (line.startsWith("ImageURL:")) {
                imageUrl = line.replace("ImageURL:", "").trim();
                break;
            }
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                URL url = new URL(imageUrl);
                BufferedImage img = ImageIO.read(url);
                Image scaled = img.getScaledInstance(400, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        centerPanel.add(imageLabel, BorderLayout.NORTH);

        // Text area for details
        //JTextArea detailsArea = new JTextArea(info);
        String displayText = String.join("\n", java.util.Arrays.stream(lines)
         .filter(l -> !l.startsWith("ImageURL:"))
         .toArray(String[]::new));
        JTextArea detailsArea = new JTextArea(displayText);
        
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        detailsArea.setBackground(Color.WHITE);
        detailsArea.setForeground(Color.BLACK);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        backButton.setBackground(new Color(221, 221, 221)); // light gray
        backButton.setForeground(new Color(51, 51, 51)); // dark gray
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose(); // close this window
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}