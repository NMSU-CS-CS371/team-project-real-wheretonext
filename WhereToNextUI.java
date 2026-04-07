// WhereToNextUI.java
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

        BackgroundPanel mainPanel = new BackgroundPanel("images/background.jpg");
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalStrut(80));

        JLabel titleLabel = new JLabel("Where To Next?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
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

        JButton goButton = new JButton("Let's Go →");
        goButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        goButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(promptLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(cityField);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(goButton);

        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalGlue());
        add(mainPanel);
        setVisible(true);

        YelpApiClient apiClient = new YelpApiClient("08ZFB6tYGsw2aek1E-PKQlME7pCTqnwwEe8qiDBa_JTmFUgS7IzHCgAxCYh2UF0MGdCKXHR_8qlMLuUUQQ3j_Si1cJgNoeV8liAmgNhrnOknAaVOlJXgy1iZa6bBaXYx");
        ResultsPanel resultsPanel = new ResultsPanel(this, mainPanel);
        SearchController controller = new SearchController(apiClient, resultsPanel);

        ActionListener searchAction = e -> {
            String city = cityField.getText().trim();
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a city!");
                return;
            }
            List<String> terms = List.of("hotels", "restaurants", "activities");

            controller.onSearch(city, terms);
            resultsPanel.setCity(city);
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