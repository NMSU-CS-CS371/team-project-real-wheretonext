import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class ItineraryPage extends JPanel {

    private final List<String> businesses = new ArrayList<>();
    private JPanel cardListPanel;
    private JLabel countLabel;
    private JLabel cityLabel;
    private JScrollPane scroll;

    private JFrame parent;
    private JPanel previousPanel;

    private static final String SAVE_FILE = "itinerary.dat";
    private static final String DELIMITER = "===ENTRY===";

    public ItineraryPage(JFrame parent) {
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(245, 247, 250));
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            parent.setContentPane(previousPanel);
            parent.revalidate();
        });
        topBar.add(backButton, BorderLayout.WEST);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("My Itinerary", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cityLabel = new JLabel("No destination selected", SwingConstants.CENTER);
        cityLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cityLabel.setForeground(new Color(100, 100, 100));
        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        countLabel = new JLabel("0 places saved", SwingConstants.CENTER);
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        countLabel.setForeground(new Color(120, 120, 120));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBlock.add(title);
        titleBlock.add(cityLabel);
        titleBlock.add(countLabel);

        topBar.add(titleBlock, BorderLayout.CENTER);

        JButton clearButton = new JButton("Clear all");
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearButton.setForeground(new Color(180, 50, 50));
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> {
            businesses.clear();
            saveToFile();
            refresh();
        });
        topBar.add(clearButton, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        cardListPanel = new JPanel();
        cardListPanel.setBackground(new Color(245, 247, 250));

        scroll = new JScrollPane(cardListPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);

        loadFromFile();
        refresh();
    }

    public void setCity(String city) {
        cityLabel.setText("Trip to: " + city);
    }

    public void setPreviousPanel(JPanel panel) {
        this.previousPanel = panel;
    }

    public void addBusiness(String bizInfo) {
        if (!businesses.contains(bizInfo)) {
            businesses.add(bizInfo);
            saveToFile();
            refresh();
        } else {
            JOptionPane.showMessageDialog(this,
                "This place is already in your itinerary.",
                "Already saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void clearAll() {
        businesses.clear();
        saveToFile();
        refresh();
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            for (int i = 0; i < businesses.size(); i++) {
                writer.write(businesses.get(i));
                if (i < businesses.size() - 1) {
                    writer.write("\n" + DELIMITER + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            if (content.isBlank()) return;

            String[] entries = content.split("\n" + DELIMITER + "\n");
            for (String entry : entries) {
                String trimmed = entry.trim();
                if (!trimmed.isEmpty()) {
                    businesses.add(trimmed);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        cardListPanel.removeAll();

        int n = businesses.size();
        countLabel.setText(n + (n == 1 ? " place saved" : " places saved"));

        if (n == 0) {
            cardListPanel.setLayout(new BorderLayout());
            JLabel empty = new JLabel("No places saved yet. Use \"+ Itinerary\" on any business.");
            empty.setFont(new Font("SansSerif", Font.PLAIN, 14));
            empty.setForeground(new Color(150, 150, 150));
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            cardListPanel.add(empty, BorderLayout.CENTER);
            cardListPanel.revalidate();
            cardListPanel.repaint();
            return;
        }

        Map<String, List<Integer>> dayGroups = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            String biz = businesses.get(i);
            String day = biz.startsWith("Day ") ? biz.split(" \\| ")[0] : "Other";
            dayGroups.computeIfAbsent(day, k -> new ArrayList<>()).add(i);
        }

        List<String> sortedDays = new ArrayList<>(dayGroups.keySet());
        sortedDays.sort((a, b) -> {
            try { return Integer.compare(Integer.parseInt(a.replace("Day ", "")), Integer.parseInt(b.replace("Day ", ""))); }
            catch (Exception e) { return a.compareTo(b); }
        });

        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.X_AXIS));
        cardListPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        Color[] dayColors = {
            new Color(210, 228, 255),
            new Color(210, 245, 220),
            new Color(255, 235, 210),
            new Color(245, 210, 255),
            new Color(255, 210, 210),
            new Color(210, 248, 255),
            new Color(255, 255, 200)
        };

        int colorIdx = 0;
        for (int i = 0; i < sortedDays.size(); i++) {
            String day = sortedDays.get(i);
            Color headerColor = dayColors[colorIdx % dayColors.length];
            colorIdx++;

            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setBackground(Color.WHITE);
            column.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)
            ));

            int columnWidth = 320;
            column.setPreferredSize(new Dimension(columnWidth, Integer.MAX_VALUE));
            column.setMinimumSize(new Dimension(columnWidth, 0));
            column.setMaximumSize(new Dimension(columnWidth, Integer.MAX_VALUE));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(headerColor);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel dayLabel = new JLabel(day);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            dayLabel.setForeground(new Color(40, 40, 40));

            int count = dayGroups.get(day).size();
            JLabel countLbl = new JLabel(count + (count == 1 ? " stop" : " stops"));
            countLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
            countLbl.setForeground(new Color(90, 90, 90));

            headerPanel.add(dayLabel, BorderLayout.WEST);
            headerPanel.add(countLbl, BorderLayout.EAST);
            column.add(headerPanel);

            int stopNum = 1;
            for (int idx : dayGroups.get(day)) {
                final int finalIdx = idx;
                String bizInfo = businesses.get(idx);
                String displayInfo = bizInfo.contains(" | ") ? bizInfo.split(" \\| ", 2)[1] : bizInfo;
                String[] lines = displayInfo.split("\n");

                String name = lines.length > 0 ? lines[0] : "Unknown";

                String address = "";
                boolean skippedFirstInfo = false;
                for (String line : lines) {
                    if (line.startsWith("ImageURL:")) continue;
                    if (line.equals(name)) continue;
                    if (!skippedFirstInfo) { skippedFirstInfo = true; continue; }
                    address = line;
                    break;
                }

                JPanel card = new JPanel(new BorderLayout(8, 0));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel badge = new JLabel(String.valueOf(stopNum++));
                badge.setFont(new Font("SansSerif", Font.BOLD, 11));
                badge.setForeground(Color.WHITE);
                badge.setBackground(new Color(100, 140, 200));
                badge.setOpaque(true);
                badge.setPreferredSize(new Dimension(22, 22));
                badge.setHorizontalAlignment(SwingConstants.CENTER);
                badge.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
                card.add(badge, BorderLayout.WEST);

                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
                nameLabel.setForeground(new Color(50, 120, 200));
                nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                final String finalDisplayInfo = displayInfo;
                nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        BusinessDetailsPage detailsPage = new BusinessDetailsPage(finalDisplayInfo, ItineraryPage.this);
                        JFrame frame = new JFrame("Business Details");
                        frame.setContentPane(detailsPage);
                        frame.setSize(500, 450);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    }
                });

                JLabel addressLabel = new JLabel(address);
                addressLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                addressLabel.setForeground(new Color(120, 120, 120));

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setBackground(Color.WHITE);
                textPanel.add(nameLabel);
                textPanel.add(Box.createVerticalStrut(2));
                textPanel.add(addressLabel);
                card.add(textPanel, BorderLayout.CENTER);

                JButton removeBtn = new JButton("✕");
                removeBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
                removeBtn.setForeground(new Color(180, 50, 50));
                removeBtn.setBackground(Color.WHITE);
                removeBtn.setBorderPainted(false);
                removeBtn.setOpaque(false);
                removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                removeBtn.addActionListener(e -> {
                    businesses.remove(finalIdx);
                    saveToFile();
                    refresh();
                });
                card.add(removeBtn, BorderLayout.EAST);

                column.add(card);
            }

            column.add(Box.createVerticalGlue());
            cardListPanel.add(column);

            if (i < sortedDays.size() - 1) {
                cardListPanel.add(Box.createHorizontalStrut(12));
            }
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
    }
}
