package Pusawan;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Cutting extends JPanel {

    private BufferedImage bg;
    private Image hover;

    // HITBOX
    private boolean hovering = false;
    private boolean cutting = false;
    private Rectangle knifeZone = new Rectangle(360, 205, 77, 669);
    private Rectangle boardZone = new Rectangle(528, 298, 550, 518);

    private String popupText = "";
    private long popupEndTime = 0;

    private Timer cutTimer;

    private static Cutting instance;

    public static void playCutGif(String fishName) {
        if (instance == null)
            return;
        instance.cutting = true;
        instance.showPopup("Consumed: " + fishName + " → Added: Cut " + fishName);
        instance.repaint();
        new Timer(8000, e -> {
            instance.cutting = false;
            instance.repaint();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    public Cutting() {

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

        // HOVER
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                hovering = knifeZone.contains(p) || boardZone.contains(p);
                repaint();
            }
        });

        // CLICK
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();

            if (knifeZone.contains(p) || boardZone.contains(p)) {
                Inventory.toggleWithMode("cut");
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

        // smooth repaint loop (UI refresh + popup timing)
        new Timer(8000, e -> repaint()).start();

    }

    // ================= POPUP =================

    private void showPopup(String text) {
        popupText = text;
        popupEndTime = System.currentTimeMillis() + 2000;
    }

    // ================= LOAD =================

    private void loadImages() {
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/images/cuttingBoard.png"));
            hover = new ImageIcon(getClass().getResource("/images/cuttingBoardSelected.png")).getImage();
        } catch (Exception e) {
            System.out.println("Image load error: " + e.getMessage());
        }
    }

    // ================= RENDER =================

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);

            if (hovering) {
                g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
            }

            if (cutting) {
                Image gifFrame = new ImageIcon(getClass().getResource("/images/cut.gif")).getImage();

                g.drawImage(gifFrame, 0, 0, getWidth(), getHeight(), this);
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