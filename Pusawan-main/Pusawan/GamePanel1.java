package Pusawan;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel1 extends JFrame {
        private BufferedImage pondbackgroundImage;
        
    public GamePanel1(){
        
        setTitle("Game Panel 1");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        setCursor(playerCursor.getCustomCursor());
        
        loadBackgroundImage();
        
        BackgroundPanel background2 = new BackgroundPanel();
        setContentPane(background2);

        JLabel title;
        title = new JLabel("Game Panel 1");
        add(title);

        JButton gotostartbutton;
        gotostartbutton = new JButton("Back to Main Menu");
        add(gotostartbutton);
        gotostartbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Start();
            }
        });
        JButton gotoinventorybutton;
        gotoinventorybutton = new JButton("INVENTORY");
        add(gotoinventorybutton);

         gotoinventorybutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                new Inventory();
            }
        });

        
        JButton gotocuttingbutton;
        gotocuttingbutton = new JButton("Go to Cutting");  
        add(gotocuttingbutton);

         gotocuttingbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Cutting();
            }
        });
        JButton gotocookingbutton;
        gotocookingbutton = new JButton("Go to Cooking");
        add(gotocookingbutton);

         gotocookingbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Cooking();
            }
        });

        JButton gotostorebutton;
        gotostorebutton = new JButton("Go to Store");
        add(gotostorebutton);

         gotostorebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Store();
            }
        });
        setVisible(true);

        
    }
    public void loadBackgroundImage() {
        try {
            pondbackgroundImage = ImageIO.read(new File("Pusawan-main\\Pusawan\\images\\pond.jpg"));
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
