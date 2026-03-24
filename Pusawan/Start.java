package Pusawan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Start extends JFrame {
    public BufferedImage backgroundImage;

    public Start() {
        setTitle("PUSAWAN");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        loadBackgroundImage();

        BackgroundPanel background = new BackgroundPanel();
        setContentPane(background);
        
        JLabel title = new JLabel("PUSAWAN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.BLACK); 
        title.setOpaque(false);
        
        JButton button = new JButton("START GAME");
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(200, 60));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setForeground(Color.BLACK);
        
         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new GamePanel1();
            }
        });

        background.setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(20, 20, 20, 20);
        
        grid.gridx = 0; grid.gridy = 0; grid.gridwidth = 2;
        background.add(title, grid);
        
        grid.gridy = 1;
        background.add(button, grid);

        setVisible(true);
    }

    public void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("Pusawan/craiyon_002441_______.png"));
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(),this);
            }
        }
    }

    public static void main(String[] args) {
         new Start();
    }
}