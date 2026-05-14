package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Sell extends JPanel {

    private ImageIcon sellBackgroundGif;
    private Image hover;
    private boolean hovering = false;
    private Rectangle sellZone = new Rectangle(180, 65, 830, 585);

    public Sell() {
        sellBackgroundGif = new ImageIcon(getClass().getResource("/images/sell.gif"));
        hover = new ImageIcon(getClass().getResource("/images/sellSelected.png")).getImage();

        new javax.swing.Timer(24, e -> repaint()).start();

        BackgroundPanel bg = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(bg, BorderLayout.CENTER);
        bg.setLayout(null);

        bg.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                hovering = sellZone.contains(e.getPoint());
                repaint();
            }
        });

        bg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (sellZone.contains(e.getPoint())) {
                    Shop.toggleShop("sell");
                } else {
                    if (Inventory.instance != null) { Inventory.instance.dispose(); Inventory.instance = null; }
                    if (Shop.instance != null) { Shop.instance.dispose(); Shop.instance = null; }
                }
            }
        });

        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        bg.add(menuButton);

        JButton inventoryButton = Buttons.toInventory();
        inventoryButton.setBounds(20, 505, 100, 100);
        bg.add(inventoryButton);
    }

    class BackgroundPanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(sellBackgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            if (hovering) g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
        }
    }
}