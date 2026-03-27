package Pusawan;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Cutting extends JFrame {
    private BufferedImage kitchenBackgroundImage;
        
    public Cutting(){

        setTitle("Cutting");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        
        loadBackgroundImage();
        
        BackgroundPanel kitchenbackground = new BackgroundPanel();
        setContentPane(kitchenbackground);


        JLabel title;
        title = new JLabel("Game Panel 2");
        add(title);

        JButton gotostartbuttonbutton;
        gotostartbuttonbutton = new JButton("Back Main Menu");
        add(gotostartbuttonbutton);
        gotostartbuttonbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Start();
            }
        });

        
        JButton gotoinventorybutton = new JButton("INVENTORY");
        add(gotoinventorybutton);

         gotoinventorybutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();
                new Inventory();
            }
        });

        JButton gotocookingbutton = new JButton("Go Cooking");
        add(gotocookingbutton);

         gotocookingbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Cooking();
            }
        });

        JButton gotostorebutton = new JButton("Go to Store");
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
            kitchenBackgroundImage = ImageIO.read(new File("Pusawan\\Cutting board.jpg"));
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
        }
    }
    class BackgroundPanel extends JPanel{
         @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (kitchenBackgroundImage != null) {
                g.drawImage(kitchenBackgroundImage, 0, 0, getWidth(), getHeight(),this);
            }
        }
}
}
