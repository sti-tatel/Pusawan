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
        setResizable(true);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        container.add(new Start(), START);
        container.add(new Fishing(), GAME);
        container.add(new Cutting(), CUTTING);
        container.add(new Cooking(), COOKING);
        container.add(new Store(), STORE);
        container.add(new Sell(), SELL);

        Inventory.fillDebug();
        add(container);
        setVisible(true);
        navigate(START);
    }

    public static void navigate(String screen) {
        if (Inventory.instance != null) { Inventory.instance.dispose(); }
        if (Shop.instance != null) { Shop.instance.dispose(); }
        hideOverlay();
        instance.cardLayout.show(instance.container, screen);
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