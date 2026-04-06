import java.awt.*;
import java.util.Map;
import javax.swing.*;

public class ResultsPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JPanel hotelsPanel;
    private JPanel restaurantsPanel;
    private JPanel activitiesPanel;

    public ResultsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // modern minimal white

        // Title
        JLabel title = new JLabel("Top Results", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);

        hotelsPanel = createListPanel();
        restaurantsPanel = createListPanel();
        activitiesPanel = createListPanel();

        tabbedPane.addTab("Hotels", new JScrollPane(hotelsPanel));
        tabbedPane.addTab("Restaurants", new JScrollPane(restaurantsPanel));
        tabbedPane.addTab("Activities", new JScrollPane(activitiesPanel)); // renamed

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE); // white background
        return panel;
    }

    public void showResults(Map<String, String> resultsByCategory) {
        hotelsPanel.removeAll();
        restaurantsPanel.removeAll();
        activitiesPanel.removeAll();

        addButtonsToPanel(hotelsPanel, resultsByCategory.getOrDefault("hotels", ""));
        addButtonsToPanel(restaurantsPanel, resultsByCategory.getOrDefault("restaurants", ""));
        addButtonsToPanel(activitiesPanel, resultsByCategory.getOrDefault("activities", "")); // changed key

        revalidate();
        repaint();
    }

    private void addButtonsToPanel(JPanel panel, String data) {
        panel.removeAll();
        String[] businesses = data.split("\n\n");
        for (String bizInfo : businesses) {
    String name = bizInfo.split("\n")[0];
    JButton btn = new JButton(name);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            btn.setBackground(new Color(221, 221, 221)); // light gray
            btn.setForeground(new Color(51, 51, 51)); // dark gray text
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16));

            btn.addActionListener(e -> {
                BusinessDetailsPage detailsPage = new BusinessDetailsPage(bizInfo);
                JFrame frame = new JFrame("Business Details");
                frame.setContentPane(detailsPage);
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });

            panel.add(Box.createVerticalStrut(5));
            panel.add(btn);
        }
    }
}