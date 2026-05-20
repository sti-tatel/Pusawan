package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Game extends JFrame {
    public static final String START = "start";
    public static final String GAME = "game";
    public static final String CUTTING = "cutting";
    public static final String COOKING = "cooking";
    public static final String STORE = "store";
    public static final String SELL = "sell";

    private static Game instance;
    private CardLayout cardLayout;
    private JPanel container;

    public Game() {
        instance = this;
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setCursor(playerCursor.getCustomCursor());
        setResizable(false);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);


        Start start = new Start();
        start.setName(START);
        container.add(start, START);

        Inventory.fillDebug();
        add(container);

        setVisible(true);
        navigate(START);
    }

    public static void navigate(String screen) {
        Buttons.closeAllDropdowns();
        if (Inventory.instance != null) { Inventory.instance.dispose(); Inventory.instance = null; }
        if (Shop.instance != null) { Shop.instance.dispose(); Shop.instance = null; }
        hideOverlay();

        // lazy load screen if not yet added
        if (instance.container.getLayout() instanceof CardLayout) {
            boolean found = false;
            for (java.awt.Component c : instance.container.getComponents()) {
                if (c.getName() != null && c.getName().equals(screen)) { found = true; break; }
            }
            if (!found) {
                JPanel panel = null;
                switch (screen) {
                    case GAME:    panel = new Fishing(); break;
                    case CUTTING: panel = new Cutting(); break;
                    case COOKING: panel = new Cooking(); break;
                    case STORE:   panel = new Buy();     break;
                    case SELL:    panel = new Sell();    break;
                }
                if (panel != null) {
                    panel.setName(screen);
                    instance.container.add(panel, screen);
                }
            }
        }

        instance.cardLayout.show(instance.container, screen);
        if (screen.equals(START)) {
            AudioPlayer.playMusic("tropicalBreeze.wav");
        } else if (screen.equals(GAME)) {
            AudioPlayer.playMusic("morningMood.wav");
        } else {
            AudioPlayer.stopMusic();
        }
    }
    public static JLayeredPane layeredPane() {
        return instance.getLayeredPane();
    }
    
    private static JPanel overlay;

    public static void showOverlay() {
        if (overlay == null) {
            overlay = new JPanel() {
                protected void paintComponent(Graphics g) {
                    g.setColor(new Color(0, 0, 0, 120));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            overlay.setOpaque(false);
            overlay.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (Shop.instance != null) Shop.instance.dispose();
                    if (Inventory.instance != null) Inventory.instance.dispose();
                }
            });
            instance.getLayeredPane().add(overlay, JLayeredPane.MODAL_LAYER);
        }
        overlay.setBounds(0, 0, instance.getWidth(), instance.getHeight());
        overlay.setVisible(true);
        overlay.repaint();
    }

    public static void hideOverlay() {
        if (overlay != null) overlay.setVisible(false);
    }

    public static void hideOverlayIfNoModals() {
        if (Inventory.instance == null && Shop.instance == null) {
            hideOverlay();
        }
    }
}