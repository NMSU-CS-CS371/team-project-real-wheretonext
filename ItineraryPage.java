import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;


public class ItineraryPage extends JPanel {

    private final List<String> businesses = new ArrayList<>();
    private JPanel cardListPanel;
    private JLabel countLabel;

    private JFrame parent;
    private JPanel previousPanel;

    public ItineraryPage(JFrame parent) {
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.addActionListener(e -> {
            parent.setContentPane(previousPanel);
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

    public void setPreviousPanel(JPanel panel) {
        this.previousPanel = panel;
    }

    public void addBusiness(String bizInfo) {
        if (!businesses.contains(bizInfo)) {
            businesses.add(bizInfo);
            refresh();
        } else {
            JOptionPane.showMessageDialog(this,
                "This place is already in your itinerary.",
                "Already saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

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

            String name = lines.length > 0 ? lines[0] : "Unknown";
            String details = "";
            String imageUrl = "";

            for (String line : lines) {
                if (line.startsWith("ImageURL:")) {
                    imageUrl = line.replace("ImageURL:", "").trim();
                    continue;
                }
                if (details.isEmpty() && !line.equals(name)) {
                    details = line;
                }
            }

            JPanel card = new JPanel(new BorderLayout(12, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                )
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);

            // ── IMAGE ─────────────────────────────────────────────
            JLabel imgLabel = new JLabel();
            imgLabel.setPreferredSize(new Dimension(90, 80));
            imgLabel.setOpaque(true);
            imgLabel.setBackground(new Color(240, 240, 240));

            if (!imageUrl.isEmpty()) {
                final String finalUrl = imageUrl;

                new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        BufferedImage img = ImageIO.read(new URL(finalUrl));
                        Image scaled = img.getScaledInstance(90, 80, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }

                    @Override
                    protected void done() {
                        try {
                            imgLabel.setIcon(get());
                        } catch (Exception ignored) {}
                    }
                }.execute();
            }

            card.add(imgLabel, BorderLayout.WEST);

            // ── TEXT ───────────────────────────────────────────────
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

            // ── REMOVE BUTTON ─────────────────────────────────────
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