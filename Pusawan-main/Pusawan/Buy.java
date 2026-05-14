package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Buy extends JPanel {

    public ImageIcon storeBackgroundGif;
    private Image hover;
    private boolean hovering = false;
    private Rectangle storeZone = new Rectangle(530, 200, 480, 480);

    public Buy() {

        loadBackgroundImage();

        new javax.swing.Timer(24, e -> repaint()).start();

        BackgroundPanel storebackground = new BackgroundPanel();
        hover = new ImageIcon(getClass().getResource("/images/storeBackgroundSelected.png")).getImage();
        setLayout(new BorderLayout());
        add(storebackground, BorderLayout.CENTER);

        storebackground.setLayout(null);

        storebackground.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                hovering = storeZone.contains(e.getPoint()) && Shop.instance == null;
                repaint();
            }
        });

        storebackground.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (storeZone.contains(e.getPoint())) {
                    Shop.toggleShop("buy");
                } else {
                    if (Inventory.instance != null) {
                        Inventory.instance.dispose();
                        Inventory.instance = null;
                    }
                    if (Shop.instance != null) {
                        Shop.instance.dispose();
                        Shop.instance = null;
                    }
                }
            }
        });

        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        storebackground.add(menuButton);

        JButton inventoryButton = Buttons.toInventory();
        inventoryButton.setBounds(20, 505, 100, 100);
        storebackground.add(inventoryButton);
    }

    public void loadBackgroundImage() {
        storeBackgroundGif = new ImageIcon(getClass().getResource("/images/storeBackground.gif"));
    }

    class BackgroundPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(storeBackgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            if (hovering) g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
        }
    }
}