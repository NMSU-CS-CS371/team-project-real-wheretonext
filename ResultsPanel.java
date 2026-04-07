import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;

public class ResultsPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel hotelsPanel;
    private JPanel restaurantsPanel;
    private JPanel activitiesPanel;
    private JLabel cityLabel;

    public ResultsPanel(JFrame parent, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            parent.setContentPane(mainPanel);
            parent.revalidate();
        });
        topBar.add(backButton, BorderLayout.WEST);

        JLabel title = new JLabel("Top Results", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        topBar.add(title, BorderLayout.CENTER);

        cityLabel = new JLabel("", SwingConstants.CENTER);
        cityLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        cityLabel.setForeground(new Color(50, 120, 200));
        topBar.add(cityLabel, BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);

        hotelsPanel = createListPanel();
        restaurantsPanel = createListPanel();
        activitiesPanel = createListPanel();

        tabbedPane.addTab("Hotels", new JScrollPane(hotelsPanel));
        tabbedPane.addTab("Restaurants", new JScrollPane(restaurantsPanel));
        tabbedPane.addTab("Activities", new JScrollPane(activitiesPanel));

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void setCity(String city) {
        cityLabel.setText(city);
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
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

            for (String line : lines) {
                if (line.startsWith("ImageURL:")) continue;
                if (details.isEmpty() && !line.equals(name)) {
                    details = line;
                } else if (extra.isEmpty() && !line.equals(name) && !line.equals(details)) {
                    extra = line;
                }
            }

            JPanel card = new JPanel();
            card.setLayout(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 0, 6, 0),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(14, 18, 14, 18)
                )
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            card.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            nameLabel.setForeground(new Color(30, 30, 30));

            JLabel detailsLabel = new JLabel(details);
            detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            detailsLabel.setForeground(new Color(120, 120, 120));

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
            viewBtn.addActionListener(e -> {
                BusinessDetailsPage detailsPage = new BusinessDetailsPage(bizInfo);
                JFrame frame = new JFrame("Business Details");
                frame.setContentPane(detailsPage);
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
            rightPanel.add(viewBtn, BorderLayout.SOUTH);

            card.add(textPanel, BorderLayout.CENTER);
            card.add(rightPanel, BorderLayout.EAST);

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