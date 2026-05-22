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
                Buttons.closeAllDropdowns();
                if (storeZone.contains(e.getPoint())) {
                    Shop.toggleShop("buy");
                } else {
                    Buttons.closeAllDropdowns();
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                    if (Shop.instance != null) Shop.instance.dispose();
                }
            }
        });

        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        storebackground.add(menuButton);

        JButton arrowRight = new JButton(new ImageIcon(getClass().getResource("/images/arrowRight.png")));
        arrowRight.setBounds(1266, 400, 64, 64);
        arrowRight.setBorderPainted(false);
        arrowRight.setContentAreaFilled(false);
        arrowRight.setFocusPainted(false);
        Buttons.addClickSound(arrowRight);
        arrowRight.addActionListener(e -> Game.navigate(Game.SELL));
        storebackground.add(arrowRight);
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