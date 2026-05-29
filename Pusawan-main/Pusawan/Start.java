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
        JButton playButton = new JButton(new ImageIcon(getClass().getResource("/images/play.png")));
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setFocusPainted(false);
        Buttons.addClickSound(playButton);
        playButton.addActionListener(e -> {
            AudioPlayer.playMusic("morningMood.wav");
            Game.navigate(Game.KITCHEN);
        });
        playButton.setBounds(595, 500, 160, 100);
        background1.add(playButton);

        // // ===== SETTINGS BUTTON =====
        // JButton settingsButton = new JButton(new ImageIcon(getClass().getResource("/images/settings.png")));
        // settingsButton.setBorderPainted(false);
        // settingsButton.setContentAreaFilled(false);
        // settingsButton.setFocusPainted(false);
        // Buttons.addClickSound(settingsButton);
        // settingsButton.setBounds(465, 600, 202, 100);
        // background1.add(settingsButton);

        // // ===== CREDITS BUTTON =====
        // JButton creditsButton = new JButton(new ImageIcon(getClass().getResource("/images/credits.png")));
        // creditsButton.setBorderPainted(false);
        // creditsButton.setContentAreaFilled(false);
        // creditsButton.setFocusPainted(false);
        // Buttons.addClickSound(creditsButton);
        // creditsButton.setBounds(677, 600, 208, 100);
        // background1.add(creditsButton);

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
