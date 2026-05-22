package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Sell extends JPanel {

    private ImageIcon sellBackgroundGif;
    private Image hover;
    private boolean hovering = false;
    private Rectangle sellZone = new Rectangle(180, 65, 830, 585);

    private JButton inventoryButton;

    public Sell() {
        sellBackgroundGif = new ImageIcon(getClass().getResource("/images/sell.gif"));
        hover = new ImageIcon(getClass().getResource("/images/sellSelected.png")).getImage();

        new javax.swing.Timer(24, e -> { if (isShowing()) repaint(); }).start();

        BackgroundPanel bg = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(bg, BorderLayout.CENTER);
        bg.setLayout(null);

        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        bg.add(menuButton);

        JButton arrowLeft = new JButton(new ImageIcon(getClass().getResource("/images/arrowLeft.png")));
        arrowLeft.setBounds(20, 400, 64, 64);
        arrowLeft.setBorderPainted(false);
        arrowLeft.setContentAreaFilled(false);
        arrowLeft.setFocusPainted(false);
        Buttons.addClickSound(arrowLeft);
        arrowLeft.addActionListener(e -> Game.navigate(Game.STORE));
        bg.add(arrowLeft);

        // intercept ALL presses before any button responds
        bg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (Shop.instance != null) {
                    Shop.instance.dispose();
                    e.consume();
                    return;
                }
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (Shop.instance != null) return;
                if (sellZone.contains(e.getPoint())) {
                    hovering = false;
                    repaint();
                    Buttons.closeAllDropdowns();
                    Shop.toggleShop("sell");
                } else {
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                }
            }
        });

        bg.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                hovering = sellZone.contains(e.getPoint()) && Shop.instance == null;
                repaint();
            }
        });

        inventoryButton = Buttons.createInventoryButton();
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