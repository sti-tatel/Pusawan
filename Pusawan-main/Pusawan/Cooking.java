package Pusawan;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Cooking extends JFrame {
        private BufferedImage cookingBackgroundImage;
        
    public Cooking(){

        setTitle("Game Cooking");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        getContentPane().setBackground(Color.BLUE);

       loadBackgroundImage();
       BackgroundPanel cookingbackground = new BackgroundPanel();
       setContentPane(cookingbackground); 

        JLabel title;
        title = new JLabel("Game Cooking");
        add(title);

        JButton gotostorebutton;
        gotostorebutton = new JButton("Back Main Menu");
        add(gotostorebutton);
        gotostorebutton.setBounds(100,500,100,100);

        gotostorebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Start();
            }
        });

        
        gotostorebutton = new JButton("INVENTORY");
        add(gotostorebutton);

         gotostorebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();
                new Inventory();
            }
        });

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
    private void loadBackgroundImage() {
        try {
            cookingBackgroundImage = ImageIO.read(new File("Pusawan\\Cooking.jpg"));
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
        }
        
    }
    class BackgroundPanel extends JPanel{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if(cookingBackgroundImage != null){
                g.drawImage(cookingBackgroundImage,0,0,getWidth(),getHeight(),this);
            }
        }
    }
}
