package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Start extends JPanel {

    private ImageIcon titleScreen;

    public Start() {

        loadBackgroundImage();
        new javax.swing.Timer(24, e -> { if (isShowing()) repaint(); }).start();

        BackgroundPanel background1 = new BackgroundPanel();
        background1.setLayout(null); // IMPORTANT
        setLayout(new BorderLayout());
        add(background1, BorderLayout.CENTER);

        // ===== TITLE =====
        ImageIcon titleImage = new ImageIcon(
                getClass().getResource("/images/title.png")
        );

        JLabel title = new JLabel(titleImage);

        int titleWidth = 600;
        int titleHeight = 330;

        int titleX = (1350 - titleWidth) / 2;

        title.setBounds(titleX, -50, titleWidth, titleHeight);
        background1.add(title);

        // ===== PLAY BUTTON =====
        JButton playButton = Buttons.toStart(() -> Game.navigate(Game.GAME));

        int buttonWidth = 143;
        int buttonHeight = 80;

        int playX = (1350 - buttonWidth) / 2;
        int playY = (1150 - buttonHeight) / 2;

        playButton.setBounds(playX, playY, buttonWidth, buttonHeight);
        background1.add(playButton);

    }

    public void loadBackgroundImage() {
        try {
            titleScreen = new ImageIcon(getClass().getResource("/images/titlescreen.gif"));
            
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (titleScreen != null) {
                g.drawImage(titleScreen.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
