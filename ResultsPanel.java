import java.awt.*;
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

    public ResultsPanel(JFrame parent, JPanel mainPanel, ItineraryPage itinerary) {
        this.itinerary = itinerary;

        // ── BACKGROUND ─────────────────────────────────────
        setLayout(new BorderLayout());
        BackgroundPanel bgPanel = new BackgroundPanel("images/banner.jpg");
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel, BorderLayout.CENTER);

        // ── TOP BAR ────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);

        // BACK BUTTON (LEFT)
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            parent.setContentPane(mainPanel);
            parent.revalidate();
        });
        topBar.add(backButton, BorderLayout.WEST);

        // ITINERARY BUTTON (RIGHT)
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
        topBar.add(itineraryBtn, BorderLayout.EAST);

        // TITLE (CENTER)
        JLabel title = new JLabel("Top Results", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        topBar.add(title, BorderLayout.CENTER);

        // CITY LABEL (BOTTOM)
        cityLabel = new JLabel("", SwingConstants.CENTER);
        cityLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        cityLabel.setForeground(Color.BLACK);
        topBar.add(cityLabel, BorderLayout.SOUTH);

        // ── ADD TOP BAR ────────────────────────────────────
        bgPanel.add(topBar, BorderLayout.NORTH);

        // ── TABS ────────────────────────────────────────────
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);

        hotelsPanel = createListPanel();
        restaurantsPanel = createListPanel();
        activitiesPanel = createListPanel();

        JScrollPane hotelsScroll = new JScrollPane(hotelsPanel);
        hotelsScroll.getVerticalScrollBar().setUnitIncrement(20);

        JScrollPane restaurantsScroll = new JScrollPane(restaurantsPanel);
        restaurantsScroll.getVerticalScrollBar().setUnitIncrement(20);

        JScrollPane activitiesScroll = new JScrollPane(activitiesPanel);
        activitiesScroll.getVerticalScrollBar().setUnitIncrement(20);

        tabbedPane.addTab("Hotels", hotelsScroll);
        tabbedPane.addTab("Restaurants", restaurantsScroll);
        tabbedPane.addTab("Activities", activitiesScroll);

        // ── ADD TABS ────────────────────────────────────────
        bgPanel.add(tabbedPane, BorderLayout.CENTER);

        // ── TRANSPARENCY FIXES ─────────────────────────────
        topBar.setOpaque(false);
        tabbedPane.setOpaque(false);
        tabbedPane.setBackground(new Color(255, 255, 255, 220));
        hotelsScroll.setOpaque(false);
        hotelsScroll.getViewport().setOpaque(false);

        restaurantsScroll.setOpaque(false);
        restaurantsScroll.getViewport().setOpaque(false);

        activitiesScroll.setOpaque(false);
        activitiesScroll.getViewport().setOpaque(false);
    }

    public void setCity(String city) {
        cityLabel.setText(city);
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }

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

            JPanel card = new JPanel(new BorderLayout(12, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 0, 6, 0),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                )
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            JLabel thumbLabel = new JLabel();
            thumbLabel.setPreferredSize(new Dimension(80, 70));
            thumbLabel.setOpaque(true);
            thumbLabel.setBackground(new Color(240, 240, 240));

            if (!imageUrl.isEmpty()) {
                final String finalUrl = imageUrl;

                new SwingWorker<ImageIcon, Void>() {
                    protected ImageIcon doInBackground() throws Exception {
                        BufferedImage img = ImageIO.read(new URL(finalUrl));
                        Image scaled = img.getScaledInstance(80, 70, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }

                    protected void done() {
                        try {
                            thumbLabel.setIcon(get());
                        } catch (Exception ignored) {}
                    }
                }.execute();
            }

            card.add(thumbLabel, BorderLayout.WEST);

            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

            JLabel detailsLabel = new JLabel(details);
            detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JLabel extraLabel = new JLabel(extra);
            extraLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(Color.WHITE);
            textPanel.add(nameLabel);
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

            panel.add(card);
        }
    }
}