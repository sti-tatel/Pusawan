package Pusawan;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.*;

public class Buttons extends JPanel {

    public static final java.util.List<Runnable> dropdownClosers = new java.util.ArrayList<>();
    
    public static JButton inventoryButtonRef;
    
    private static final java.util.List<JButton> inventoryButtons = new java.util.ArrayList<>();

    public static JButton toDropdown() {
        ImageIcon menuButtonImage = new ImageIcon(Buttons.class.getResource("/images/menu.png"));
        JButton menuButton = new JButton(menuButtonImage);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);

        int itemHeight = 64;
        int totalItems = 6;
        int fullHeight = itemHeight * totalItems;

        JPanel inner = new JPanel(new GridLayout(totalItems, 1, 0, 0));
        inner.setBackground(new Color(0, 0, 0, 180));
        inner.setOpaque(true);

        JPanel clipPanel = new JPanel(null);
        clipPanel.setOpaque(false);
        inner.setBounds(0, 0, 64, fullHeight);
        clipPanel.add(inner);

        JButton mainMenu = new JButton(new ImageIcon(Buttons.class.getResource("/images/mainmenu.png")));
        JButton fishing  = new JButton(new ImageIcon(Buttons.class.getResource("/images/fishing.png")));
        JButton cutting  = new JButton(new ImageIcon(Buttons.class.getResource("/images/cutIconStatic.png")));
        JButton cooking  = new JButton(new ImageIcon(Buttons.class.getResource("/images/cookIconStatic.png")));
        JButton store    = new JButton(new ImageIcon(Buttons.class.getResource("/images/store.png")));
        JButton sell     = new JButton(new ImageIcon(Buttons.class.getResource("/images/sell.png")));

        Insets zero = new Insets(0, 0, 0, 0);
        for (JButton b : new JButton[]{mainMenu, fishing, cutting, cooking, store, sell}) {
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setMargin(zero);
        }

        inner.add(mainMenu);
        inner.add(fishing);
        inner.add(cutting);
        inner.add(cooking);
        inner.add(store);
        inner.add(sell);

        boolean[] open = {false};
        // int[] currentH = {0};
        // Timer[] animTimer = {null};

        Runnable close = () -> {
            if (!open[0]) return;
            open[0] = false;
            if (Inventory.instance != null) Inventory.instance.dispose();
            if (Shop.instance != null) Shop.instance.dispose();
            clipPanel.setVisible(false);
            if (clipPanel.getParent() != null) {
                JLayeredPane lp = (JLayeredPane) clipPanel.getParent();
                lp.remove(clipPanel);
                lp.repaint(clipPanel.getX(), clipPanel.getY(), 64, fullHeight);
            }
        };

        dropdownClosers.add(close);


        mainMenu.addActionListener(e -> { close.run(); Game.navigate(Game.START); });
        fishing.addActionListener(e ->  { close.run(); Game.navigate(Game.GAME); });
        cutting.addActionListener(e ->  { close.run(); Game.navigate(Game.CUTTING); });
        cooking.addActionListener(e ->  { close.run(); Game.navigate(Game.COOKING); });
        store.addActionListener(e ->    { close.run(); Game.navigate(Game.STORE); });
        sell.addActionListener(e ->     { close.run(); Game.navigate(Game.SELL); });

        menuButton.addActionListener(e -> {
            if (open[0]) { close.run(); return; }
            JLayeredPane lp = Game.layeredPane();
            if (lp == null) return;
            Point p = SwingUtilities.convertPoint(menuButton.getParent(), menuButton.getLocation(), lp);
            inner.setBounds(0, 0, 64, fullHeight);
            clipPanel.setBounds(p.x, p.y + menuButton.getHeight(), 64, fullHeight);
            clipPanel.setVisible(true);
            lp.add(clipPanel, JLayeredPane.POPUP_LAYER);
            lp.revalidate();
            lp.repaint(clipPanel.getX(), clipPanel.getY(), 64, fullHeight);
            open[0] = true;
        });

        return menuButton;
    }

    public static JButton toStart(Runnable action) {
        ImageIcon startButtonImage = new ImageIcon(Buttons.class.getResource("/images/startbutton.png"));
        JButton startButton = new JButton(startButtonImage);
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setSize(startButtonImage.getIconWidth(), startButtonImage.getIconHeight());
        startButton.addActionListener(e -> action.run());
        return startButton;
    }

    public static JButton toMainMenu() {
        ImageIcon menuButtonImage = new ImageIcon(Buttons.class.getResource("/images/mainmenu.png"));
        JButton menuButton = new JButton(menuButtonImage);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> Game.navigate(Game.START));
        return menuButton;
    }

    public static JButton toInventory() {
        ImageIcon closedIcon = new ImageIcon(Buttons.class.getResource("/images/inventory.png"));
        ImageIcon openedIcon = new ImageIcon(Buttons.class.getResource("/images/inventoryOpened.png"));

        JButton inventoryButton = new JButton(closedIcon);
        inventoryButton.setBorderPainted(false);
        inventoryButton.setContentAreaFilled(false);
        inventoryButton.setFocusPainted(false);

        inventoryButton.addActionListener(e -> {
            Inventory.toggleInventory();
            inventoryButton.setIcon(Inventory.instance != null ? openedIcon : closedIcon);
        });

        inventoryButtonRef = inventoryButton;
        inventoryButtons.add(inventoryButton);
        return inventoryButton;
    }

    public static void updateInventoryIcon() {
        
        if (inventoryButtons.isEmpty()) return;
        ImageIcon closedIcon = new ImageIcon(Buttons.class.getResource("/images/inventory.png"));
        ImageIcon openedIcon = new ImageIcon(Buttons.class.getResource("/images/inventoryOpened.png"));
        for (JButton button : inventoryButtons) {
            button.setIcon(Inventory.instance != null ? openedIcon : closedIcon);
        }
    }

    public static void clearInventoryButtons() {
        inventoryButtons.removeIf(b -> !b.isDisplayable());
        inventoryButtonRef = inventoryButtons.isEmpty() ? null : inventoryButtons.get(inventoryButtons.size() - 1);
    }

    public static JButton toCutting() {
        ImageIcon cuttingButtonImage = new ImageIcon(Buttons.class.getResource("/images/cutIconStatic.png"));
        ImageIcon cuttingButtonHoverImage = new ImageIcon(Buttons.class.getResource("/images/cutIconAnimated.gif"));
        JButton cuttingButton = new JButton(cuttingButtonImage);
        cuttingButton.setRolloverEnabled(true);
        cuttingButton.setRolloverIcon(cuttingButtonHoverImage);
        cuttingButton.setBorderPainted(false);
        cuttingButton.setContentAreaFilled(false);
        cuttingButton.setFocusPainted(false);
        cuttingButton.addActionListener(e -> Game.navigate(Game.CUTTING));
        return cuttingButton;
    }

    public static JButton toCooking() {
        ImageIcon cookingButtonImage = new ImageIcon(Buttons.class.getResource("/images/cookIconStatic.png"));
        ImageIcon cookingButtonHoverImage = new ImageIcon(Buttons.class.getResource("/images/cookIconAnimated.gif"));
        JButton cookingButton = new JButton(cookingButtonImage);
        cookingButton.setRolloverEnabled(true);
        cookingButton.setRolloverIcon(cookingButtonHoverImage);
        cookingButton.setBorderPainted(false);
        cookingButton.setContentAreaFilled(false);
        cookingButton.setFocusPainted(false);
        cookingButton.addActionListener(e -> Game.navigate(Game.COOKING));
        return cookingButton;
    }

    public static JButton toStore() {
        ImageIcon storeButtonImage = new ImageIcon(Buttons.class.getResource("/images/store.png"));
        JButton storeButton = new JButton(storeButtonImage);
        storeButton.setBorderPainted(false);
        storeButton.setContentAreaFilled(false);
        storeButton.setFocusPainted(false);
        storeButton.addActionListener(e -> Game.navigate(Game.STORE));
        return storeButton;
    }

    public static void closeAllDropdowns() {
        for (Runnable r : dropdownClosers) r.run();
    }
}