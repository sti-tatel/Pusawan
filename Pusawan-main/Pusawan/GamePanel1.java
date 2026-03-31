package Pusawan;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel1 extends JFrame {

    private BufferedImage pondbackgroundImage;

    public GamePanel1() {

        setTitle("Game Panel 1");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        setCursor(playerCursor.getCustomCursor());

        loadBackgroundImage();

        BackgroundPanel background2 = new BackgroundPanel();
        setContentPane(background2);

        ImageIcon menuButtonImage = new ImageIcon(getClass().getResource("/images/menu.png"));
        JButton menuButton = new JButton(menuButtonImage);

        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);

        add(menuButton);
        menuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                new Start();
            }
        });

        ImageIcon inventoryButtonImage = new ImageIcon(getClass().getResource("/images/inventory.png"));
        JButton gotoinventorybutton = new JButton(inventoryButtonImage);
        gotoinventorybutton.setBorderPainted(false);
        gotoinventorybutton.setContentAreaFilled(false);

        add(gotoinventorybutton);

        gotoinventorybutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                new Inventory();
            }
        });

        ImageIcon cuttingButtonStatic = new ImageIcon(getClass().getResource("/images/cutIconStatic.png"));
        ImageIcon cuttingButtonHover = new ImageIcon(getClass().getResource("/images/cutIconAnimated.gif"));

        JButton cutting = new JButton(cuttingButtonStatic);
        cutting.setRolloverEnabled(true);
        cutting.setRolloverIcon(cuttingButtonHover);
        cutting.setFocusPainted(false);
        cutting.setBorderPainted(false);
        cutting.setContentAreaFilled(false);

        add(cutting);

        cutting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                new Cutting();
            }
        });

        ImageIcon cookingButtonStatic = new ImageIcon(getClass().getResource("/images/cookIconStatic.png"));
        ImageIcon cookingButtonHover = new ImageIcon(getClass().getResource("/images/cookIconAnimated.gif"));

        JButton cooking = new JButton(cookingButtonStatic);
        cooking.setRolloverEnabled(true);
        cooking.setRolloverIcon(cookingButtonHover);
        cooking.setFocusPainted(false);
        cooking.setBorderPainted(false);
        cooking.setContentAreaFilled(false);

        add(cooking);

        cooking.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                new Cooking();
            }
        });

        JButton gotostorebutton;
        gotostorebutton = new JButton("Go to Store");

        gotostorebutton.setBorderPainted(false);
        gotostorebutton.setContentAreaFilled(false);

        add(gotostorebutton);

        gotostorebutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                new Store();
            }
        });
        setVisible(true);

    }

    public void loadBackgroundImage() {
        try {
            pondbackgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/pond.jpg"));
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (pondbackgroundImage != null) {
                g.drawImage(pondbackgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
