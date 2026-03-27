package Pusawan;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Start extends JFrame {
        private BufferedImage pondbackgroundImage;
        
    public Start(){

        setTitle("Game Panel 1");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        setCursor(playerCursor.getCustomCursor());
        
        loadBackgroundImage();
        
        BackgroundPanel background1 = new BackgroundPanel();
        setContentPane(background1);

        JLabel title;
        title = new JLabel("Game Panel 1");
        add(title);

        JButton gotoGamePanel1Button;
        gotoGamePanel1Button = new JButton("Go to Game Panel 1");
        add(gotoGamePanel1Button);
        gotoGamePanel1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new GamePanel1();
            }
        });
    
        setVisible(true);
        
    }
    public void loadBackgroundImage() {
        try {
            pondbackgroundImage = ImageIO.read(new File("Pusawan-main\\Pusawan\\images\\titlescreen.png"));
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
        }
    }

    class BackgroundPanel extends JPanel{
        public void paintComponent(Graphics g) {
                super.paintComponent(g);
            if (pondbackgroundImage != null) {
                g.drawImage(pondbackgroundImage, 0, 0, getWidth(), getHeight(),this);
            }
        }
    }    
}
