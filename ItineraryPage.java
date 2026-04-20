/************************************************************************************
 * ItineraryPage.java
 * Displays the user's saved itinerary — a list of businesses added from
 * BusinessDetailsPage. Shows each business as a card with a Remove button
 * and a "Clear All" button in the top bar.
 *
 * Connections:
 *   - BusinessDetailsPage: calls addBusiness() to add items here.
 *   - WhereToNextUI: navigation can switch to this panel.
 ************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ItineraryPage extends JPanel {

    private final List<String> businesses = new ArrayList<>();
    private JPanel cardListPanel;
    private JLabel countLabel;

    public ItineraryPage(JFrame parent, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            parent.setContentPane(mainPanel);
            parent.revalidate();
        });
        topBar.add(backButton, BorderLayout.WEST);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(Color.WHITE);

        JLabel title = new JLabel("My Itinerary", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        countLabel = new JLabel("0 places saved", SwingConstants.CENTER);
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        countLabel.setForeground(new Color(120, 120, 120));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBlock.add(title);
        titleBlock.add(countLabel);
        topBar.add(titleBlock, BorderLayout.CENTER);

        JButton clearButton = new JButton("Clear all");
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        clearButton.setForeground(new Color(180, 50, 50));
        clearButton.addActionListener(e -> {
            businesses.clear();
            refresh();
        });
        topBar.add(clearButton, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ── Scrollable card list ──────────────────────────────────────────────
        cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(cardListPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    /** Called from BusinessDetailsPage to save a business. */
    public void addBusiness(String bizInfo) {
        if (!businesses.contains(bizInfo)) {   // avoid duplicates
            businesses.add(bizInfo);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this,
                "This place is already in your itinerary.",
                "Already saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Rebuild the card list from the current businesses list. */
    private void refresh() {
        cardListPanel.removeAll();
        cardListPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        int n = businesses.size();
        countLabel.setText(n + (n == 1 ? " place saved" : " places saved"));

        if (n == 0) {
            JLabel empty = new JLabel("No places saved yet. Hit \"Add to Itinerary\" on any business.");
            empty.setFont(new Font("SansSerif", Font.PLAIN, 14));
            empty.setForeground(new Color(150, 150, 150));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardListPanel.add(Box.createVerticalStrut(60));
            cardListPanel.add(empty);
        }

        for (int i = 0; i < n; i++) {
            final int idx = i;
            String bizInfo = businesses.get(i);
            String[] lines = bizInfo.split("\n");

            String name    = lines.length > 0 ? lines[0] : "Unknown";
            String details = "";
            String extra   = "";

            for (String line : lines) {
                if (line.startsWith("ImageURL:")) continue;
                if (details.isEmpty() && !line.equals(name))           details = line;
                else if (extra.isEmpty() && !line.equals(name) && !line.equals(details)) extra = line;
            }

            // ── Card ────────────────────────────────────────────────────────
            JPanel card = new JPanel(new BorderLayout(12, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                )
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Number badge
            JLabel badge = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            badge.setPreferredSize(new Dimension(32, 32));
            badge.setFont(new Font("SansSerif", Font.BOLD, 14));
            badge.setForeground(new Color(50, 120, 200));
            badge.setOpaque(true);
            badge.setBackground(new Color(235, 244, 255));
            badge.setBorder(BorderFactory.createLineBorder(new Color(180, 210, 245), 1, true));
            card.add(badge, BorderLayout.WEST);

            // Text
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

            JLabel detailLabel = new JLabel(details);
            detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            detailLabel.setForeground(new Color(120, 120, 120));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(Color.WHITE);
            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(3));
            textPanel.add(detailLabel);
            card.add(textPanel, BorderLayout.CENTER);

            // Remove button
            JButton removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
            removeBtn.setForeground(new Color(180, 50, 50));
            removeBtn.setBackground(new Color(255, 240, 240));
            removeBtn.setBorderPainted(false);
            removeBtn.setOpaque(true);
            removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            removeBtn.addActionListener(e -> {
                businesses.remove(idx);
                refresh();
            });
            card.add(removeBtn, BorderLayout.EAST);

            cardListPanel.add(card);
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
    }
}