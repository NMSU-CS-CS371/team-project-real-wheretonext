/*****************************************************************************************************
BackgroundPanel.java
A JPanel that displays a background image stretched to fill the panel.

This class is used in:
    - WhereToNextUI: as the main panel background.
    - Any other panel where we need a custom image as the background.

*****************************************************************************************************/

import java.awt.*;
import javax.swing.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    // Constructor: loads the image from a file path
    public BackgroundPanel(String path) {
        try {
            backgroundImage = Toolkit.getDefaultToolkit().getImage(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Paint the image stretched to fit the panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}