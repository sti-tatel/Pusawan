package Pusawan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage; // High-quality scalable images , holds an image data
import java.io.File; //File
import javax.imageio.ImageIO; //load PNG/JPG
import javax.swing.*;


public class Start extends JFrame {
    public BufferedImage backgroundImage;

    public Start() {

        setTitle("PUSAWAN");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        loadBackgroundImage(); // Gets image from Pusawan folder

        BackgroundPanel background = new BackgroundPanel();
        setContentPane(background);
        
        JLabel title = new JLabel("PUSAWAN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.BLACK); 
        title.setOpaque(true); // Transparency background
  
        JButton button = new JButton("START GAME");
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(200, 60)); 
        button.setOpaque(true);
        button.setContentAreaFilled(true);// Fill button with color
        button.setBorderPainted(false);// Show button border
        button.setForeground(Color.BLACK);// Black text

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new GamePanel1();
            }
        });

        background.setLayout(new BorderLayout());

        JPanel Panel = new JPanel();
        Panel.setLayout(new BoxLayout(Panel, BoxLayout.Y_AXIS));
        Panel.setOpaque(false); // Transparent so background 

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Box -> Invisible layout helper that make centering automatic
        Panel.add(Box.createVerticalGlue());//Pushes Down from top
        Panel.add(title);
        Panel.add(Box.createRigidArea(new Dimension(0, 40)));//Gap between the title and button
        Panel.add(button);
        Panel.add(Box.createVerticalGlue());//Pushes Up from down

        background.add(Panel, BorderLayout.CENTER);
        
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

}