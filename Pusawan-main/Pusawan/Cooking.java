package Pusawan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Cooking extends JPanel {

    private boolean hovering = false;
    private boolean cooking = false;

    private ImageIcon grillGif;
    private ImageIcon cookingGif;
    private Image grillSelected;

    private Rectangle grillZone = new Rectangle(588, 254, 172, 283);

    private String popupText = "";
    private long popupEndTime = 0;

    private Timer cookTimer;

    private static Cooking instance;

    public static void playCookGif(String fishName) {
        if (instance == null) return;
        instance.cooking = true;
        instance.showPopup("Cooking...");
        instance.repaint();
        new Timer(8000, e -> {
            instance.cooking = false;
            String cooked = "Cooked " + fishName.replace("Cut ", "");
            instance.showPopup("Added: " + cooked);
            instance.repaint();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    public Cooking() {
        instance = this;
        loadImages();

        

        BackgroundPanel panel = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setLayout(null);


        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        panel.add(menuButton);

        JButton inventoryButton = Buttons.toInventory();
        inventoryButton.setBounds(20, 505, 100, 100);
        panel.add(inventoryButton);

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hovering = grillZone.contains(e.getPoint());
                repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (grillZone.contains(e.getPoint())) {
                    Inventory.toggleWithMode("cook");
                } else {
                    if (Inventory.instance != null) Inventory.instance.dispose();
                }
                if (Shop.instance != null) Shop.instance.dispose();         
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });

        new javax.swing.Timer(24, e -> { if (isShowing()) repaint(); }).start();

    }

    private void showPopup(String text) {
        popupText = text;
        popupEndTime = System.currentTimeMillis() + 2000;
    }

    private void loadImages() {
        try {
            grillGif = new ImageIcon(getClass().getResource("/images/grill.gif"));
            grillSelected = new ImageIcon(getClass().getResource("/images/grillSelected.png")).getImage();
            cookingGif = new ImageIcon(getClass().getResource("/images/cooking.gif"));
        } catch (Exception e) {
            System.out.println("Image load error: " + e.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(grillGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            grillGif.setImageObserver(this);

            if (hovering) {
                g.drawImage(grillSelected, 0, 0, getWidth(), getHeight(), this);
            }

            if (cooking) {
                g.drawImage(cookingGif.getImage(), 0, 0, getWidth(), getHeight(), this);
                cookingGif.setImageObserver(this);
            }

            if (System.currentTimeMillis() < popupEndTime) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRoundRect(450, 650, 450, 50, 20, 20);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString(popupText, 470, 680);
            }
        }
    }
}