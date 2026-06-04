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

// restored function (UNCHANGED)
playButton.addActionListener(e -> {
    AudioPlayer.playMusic("morningMood.wav");
    Game.navigate(Game.KITCHEN);
});

playButton.setBounds(595, 500, 160, 100);
background1.add(playButton);


// // ===== SETTINGS BUTTON (LEFT) =====
// JButton settingsButton = new JButton(new ImageIcon(getClass().getResource("/images/settings.png")));
// settingsButton.setBorderPainted(false);
// settingsButton.setContentAreaFilled(false);
// settingsButton.setFocusPainted(false);
// Buttons.addClickSound(settingsButton);

// settingsButton.addActionListener(e -> Settings.toggleSettings());

// // center PLAY x = 595, width = 160
// // settings width = 202 → half = 101
// // place it left of PLAY with gap (example: 40px)

// int playX = 595;
// int playW = 160;
// int gap = 40;

// int settingsW = 202;
// int creditsW = 208;

// // center of play
// int centerX = playX + (playW / 2);

// // SETTINGS position
// int settingsX = centerX - gap - settingsW;
// settingsButton.setBounds(settingsX, 600, 202, 100);
// background1.add(settingsButton);


// // ===== CREDITS BUTTON (RIGHT) =====
// JButton creditsButton = new JButton(new ImageIcon(getClass().getResource("/images/credits.png")));
// creditsButton.setBorderPainted(false);
// creditsButton.setContentAreaFilled(false);
// creditsButton.setFocusPainted(false);
// Buttons.addClickSound(creditsButton);

// // mirrored position
// int creditsX = centerX + gap;
// creditsButton.setBounds(creditsX, 600, 208, 100);

// creditsButton.addActionListener(e -> {

//     // switch background image to devs
//     titleScreen = new ImageIcon(getClass().getResource("/images/devs.png"));

//     background1.removeAll();
//     background1.revalidate();
//     background1.repaint();

//     JButton exitButton = new JButton("X");
//     exitButton.setBounds(1200, 20, 50, 50);

//     exitButton.setFocusPainted(false);
//     exitButton.setContentAreaFilled(false);
//     exitButton.setBorderPainted(false);

//     exitButton.addActionListener(ev -> {

//         // restore original background
//         titleScreen = new ImageIcon(getClass().getResource("/images/titlescreen.gif"));

//         background1.removeAll();
//         background1.revalidate();
//         background1.repaint();

//         // re-add UI elements (since removeAll clears them)
//         background1.add(title);
//         background1.add(playButton);
//         background1.add(settingsButton);
//         background1.add(creditsButton);
//     });

//     background1.add(exitButton);
// });

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
