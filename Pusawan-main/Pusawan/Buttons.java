package Pusawan;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Buttons extends JPanel {

    public static JButton inventoryButtonRef;
    private static final java.util.List<JButton> inventoryButtons = new java.util.ArrayList<>();

    public static JButton toDropdown() {
        ImageIcon menuButtonImage = new ImageIcon(Buttons.class.getResource("/images/menu.png"));
        JButton menuButton = new JButton(menuButtonImage);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setOpaque(false);
        popupMenu.setBorder(BorderFactory.createEmptyBorder());
        popupMenu.setBackground(new Color(0, 0, 0, 0));

        JButton mainMenu = new JButton(new ImageIcon(Buttons.class.getResource("/images/mainmenu.png")));
        JButton cutting  = new JButton(new ImageIcon(Buttons.class.getResource("/images/cutIconStatic.png")));
        JButton cooking  = new JButton(new ImageIcon(Buttons.class.getResource("/images/cookIconStatic.png")));
        JButton store    = new JButton(new ImageIcon(Buttons.class.getResource("/images/store.png")));
        JButton fishing  = new JButton(new ImageIcon(Buttons.class.getResource("/images/fishing.png")));
        JButton sell     = new JButton(new ImageIcon(Buttons.class.getResource("/images/sell.png")));

        Insets zero = new Insets(0, 0, 0, 0);
        for (JButton b : new JButton[]{mainMenu, cutting, cooking, store, fishing, sell}) {
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setMargin(zero);
        }

        mainMenu.addActionListener(e -> { popupMenu.setVisible(false); Game.navigate(Game.START); });
        fishing.addActionListener(e ->  { popupMenu.setVisible(false); Game.navigate(Game.GAME); });
        cutting.addActionListener(e ->  { popupMenu.setVisible(false); Game.navigate(Game.CUTTING); });
        cooking.addActionListener(e ->  { popupMenu.setVisible(false); Game.navigate(Game.COOKING); });
        store.addActionListener(e ->    { popupMenu.setVisible(false); Game.navigate(Game.STORE); });
        sell.addActionListener(e ->     { popupMenu.setVisible(false); Game.navigate(Game.SELL); });

        int itemHeight = 64;
        int totalItems = 6;
        int fullHeight = itemHeight * totalItems;

        JPanel inner = new JPanel(new GridLayout(totalItems, 1, 0, 0));
        inner.setOpaque(true);
        inner.setBackground(new Color(0, 0, 0, 180));
        inner.add(mainMenu);
        inner.add(fishing);
        inner.add(cutting);
        inner.add(cooking);
        inner.add(store);
        inner.add(sell);

        JPanel wrapper = new JPanel(null) {
            @Override
            public boolean isOptimizedDrawingEnabled() { return false; }
        };
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new java.awt.Dimension(64, fullHeight));
        inner.setBounds(0, 0, 64, fullHeight);
        wrapper.add(inner);
        popupMenu.add(wrapper);

        boolean[] open = {false};
        long[] lastToggle = {0};
        int[] currentH = {0};
        Timer[] animTimer = {null};

        Runnable startAnim = () -> {
            if (animTimer[0] != null) animTimer[0].stop();
            boolean opening = open[0];
            animTimer[0] = new Timer(8, null);
            animTimer[0].addActionListener(ev -> {
                if (opening) {
                    currentH[0] = Math.min(currentH[0] + 40, fullHeight);
                } else {
                    currentH[0] = Math.max(currentH[0] - 40, 0);
                }
                inner.setBounds(0, currentH[0] - fullHeight, 64, fullHeight);
                wrapper.setPreferredSize(new java.awt.Dimension(64, currentH[0] == 0 ? 1 : currentH[0]));
                wrapper.revalidate();
                popupMenu.pack();
                if ((opening && currentH[0] >= fullHeight) || (!opening && currentH[0] <= 0)) {
                    animTimer[0].stop();
                    if (!opening) popupMenu.setVisible(false);
                }
            });
            animTimer[0].start();
        };

        menuButton.addActionListener(e -> {
            long now = System.currentTimeMillis();
            if (now - lastToggle[0] < 200) return;
            lastToggle[0] = now;
            if (open[0]) {
                open[0] = false;
                startAnim.run();
            } else {
                currentH[0] = 0;
                inner.setBounds(0, -fullHeight, 64, fullHeight);
                wrapper.setPreferredSize(new java.awt.Dimension(64, 1));
                open[0] = true;
                popupMenu.show(menuButton, 0, menuButton.getHeight() - 20);
                startAnim.run();
            }
        });

        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                open[0] = false;
                lastToggle[0] = System.currentTimeMillis();
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            public void popupMenuCanceled(PopupMenuEvent e) { open[0] = false; }
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
}