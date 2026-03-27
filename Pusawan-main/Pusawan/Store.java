package Pusawan;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Store extends JFrame {
    public BufferedImage storeBackgroundImage;

    public Store() {

        setTitle("Game Store");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        getContentPane().setBackground(Color.cyan);
        setLocationRelativeTo(null);
        setCursor(playerCursor.getCustomCursor());

        loadBackgroundImage();
        BackgroundPanel storebackground = new BackgroundPanel();
        setContentPane(storebackground);

        JLabel title;
        title = new JLabel("Game Store");
        add(title);

        JButton button;
        button = new JButton("GO to GamePanel1");
        add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                new Start();
            }
        });

        button = new JButton("INVENTORY");
        add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                new Inventory();
            }
        });

        button = new JButton("Go to Cooking");
        add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                new Cooking();
            }
        });

        button = new JButton("Go to Cutting");
        add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

                new Cutting();
            }
        });
        setVisible(true);
    }

    public void loadBackgroundImage() {

        try {
            storeBackgroundImage = ImageIO.read(new File("Pusawan-main\\Pusawan\\images\\storebackground.jpg"));
        } catch (Exception e) {
            System.out.println("Background image doesn't found: " + e.getMessage());
        }

    }

    class BackgroundPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (storeBackgroundImage != null) {
                g.drawImage(storeBackgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
