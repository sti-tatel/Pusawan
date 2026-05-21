package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Inventory extends JPanel {

    private JPanel bg;

    static Inventory instance;
    private String mode = "inventory";
    private boolean isItemsTab = false;
    private boolean isBaitTab = false;
    private JLabel descName;
    private JTextArea descText;
    private JLabel itemDisplayLabel;

    private static final java.util.Set<String> FISH_NAMES = new java.util.HashSet<>(java.util.Arrays.asList("Carp", "Catfish", "Bass", "Perch"));

    private static final java.util.Map<String, String> descriptions = new java.util.LinkedHashMap<>();

    static {
        descriptions.put("Bass", "A strong freshwater fish.\nSells well raw.");
        descriptions.put("Carp", "A common river fish.\nNot the tastiest.");
        descriptions.put("Catfish", "Bottom-dweller.\nCooks up nicely.");
        descriptions.put("Perch", "Small but plentiful.\nGood for a quick meal.");
        descriptions.put("Cut Bass", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cut Carp", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cut Catfish", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cut Perch", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cooked Bass", "Grilled to perfection.\nFetches a good price.");
        descriptions.put("Cooked Carp", "Tender and flaky.\nSmells amazing.");
        descriptions.put("Cooked Catfish", "Crispy on the outside.\nJuicy inside.");
        descriptions.put("Cooked Perch", "Simple but satisfying.\nA fisherman's staple.");
        descriptions.put("Sandal", "Someone lost this.\nNot worth much.");
        descriptions.put("Shoe", "Waterlogged and worn.\nStill sellable.");
        descriptions.put("Plastic Wrapper", "Trash from the river.\nA few coins maybe.");
        descriptions.put("Worm Bait", "Wriggly and effective.\nAttracts common fish.");
        descriptions.put("Insect Bait", "Buzzing with potential.\nGood for mid-tier fish.");
        descriptions.put("Fish Bait", "Bait made from fish.\nAttracts bigger catches.");
        descriptions.put("Magic Bait", "Mysterious and powerful.\nWhat will you catch?");
    }

    // ================= SHARED INVENTORY =================
    public static java.util.Map<String, Integer> items = new java.util.LinkedHashMap<>();

    // fills inventory with 100 of every item for testing
    public static void fillDebug() {
        String[] allItems = {"Perch", "Carp", "Catfish", "Bass", "Cut Perch", "Cut Carp", "Cut Catfish", "Cut Bass",
            "Cooked Perch", "Cooked Carp", "Cooked Catfish", "Cooked Bass", "Sandal", "Shoe", "Plastic Wrapper",
            "Worm Bait", "Insect Bait", "Fish Bait", "Magic Bait"};
        for (String item : allItems) {
            items.put(item, 1000000);
        }
    }

    // ================= CONSTRUCTOR =================
    public Inventory(String mode) {
        this.mode = mode;
        if (mode.equals("bait")) {
            isBaitTab = true;
        }

        setLayout(null);
        setOpaque(false);
        // panel is wider than bg image to allow tabs to stick out left
        // tab area = 60px on the left, bg image = 998px wide
        setBounds(0, 0, 1058, 580);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1058, 580);
        add(layeredPane);

        // ================= BACKGROUND IMAGE =================
        JPanel bgPanel = new JPanel() {
            private Image bgImg = new ImageIcon(getClass().getResource("/images/inventorybg.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setOpaque(true);
        // bg image starts 60px from left, leaving room for tabs
        bgPanel.setBounds(60, 0, 998, 580);
        layeredPane.add(bgPanel, Integer.valueOf(-1));

        // ================= CLOSE BUTTON =================
        JButton closeBtn = new JButton();
        closeBtn.setBounds(1005, 24, 53, 53);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(false);
        closeBtn.addActionListener(e -> {
            AudioPlayer.playSound("click.wav");
            closeInventory();
        });
        layeredPane.add(closeBtn, JLayeredPane.MODAL_LAYER);

        // ================= TAB BUTTONS =================
        // tabs stick out to the left of the background image
        int tabX = 0;    // left edge of panel — sits left of the bg image
        int tabY = 150;
        int tabGap = 5;

        JButton fishTab = makeImageTab("/images/fishTab.png", tabX, tabY);
        JButton baitTab = makeImageTab("/images/baitTab.png", tabX, tabY + 64 + tabGap);
        JButton itemsTab = makeImageTab("/images/itemsTab.png", tabX, tabY + (64 + tabGap) * 2);
        Buttons.addClickSound(fishTab);
        Buttons.addClickSound(baitTab);
        Buttons.addClickSound(itemsTab);

        if (mode.equals("cut") || mode.equals("cook")) {
            itemsTab.setEnabled(false);
            baitTab.setEnabled(false);
        }
        if (mode.equals("bait")) {
            fishTab.setEnabled(false);
            itemsTab.setEnabled(false);
        }

        if (mode.equals("bait")) {
            baitTab.setOpaque(true);
            baitTab.setBackground(new Color(255, 255, 255, 80));
        }

        fishTab.addActionListener(e -> {
            if (!isItemsTab && !isBaitTab) {
                return;
            }
            isItemsTab = false;
            isBaitTab = false;
            refreshItems();
        });
        baitTab.addActionListener(e -> {
            if (isBaitTab) {
                return;
            }
            isBaitTab = true;
            isItemsTab = false;
            refreshItems();
        });
        itemsTab.addActionListener(e -> {
            if (isItemsTab) {
                return;
            }
            isItemsTab = true;
            isBaitTab = false;
            refreshItems();
        });

        layeredPane.add(fishTab, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(baitTab, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(itemsTab, JLayeredPane.PALETTE_LAYER);

        // ================= ITEM GRID =================
        bg = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        bg.setOpaque(false);
        bg.setBorder(BorderFactory.createEmptyBorder(9, 9, 10, 10));

        JPanel bgWrapper = new JPanel(new BorderLayout());
        bgWrapper.setOpaque(false);
        bgWrapper.add(bg, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(bgWrapper);
        scrollPane.setBounds(93, 100, 840, 440);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        // ================= DESCRIPTION PANEL =================
        JPanel descPanel = new JPanel(null);
        descPanel.setBounds(860, 265, 172, 280);
        //descPanel.setBackground(new Color(45, 28, 12));
        descPanel.setOpaque(true);

        descName = new JLabel("", SwingConstants.CENTER);
        descName.setBounds(5, 10, 162, 30);
        descName.setForeground(new Color(255, 220, 130));
        descName.setFont(new Font("Arial", Font.BOLD, 13));
        descPanel.add(descName);

        descText = new JTextArea("Select an item");
        descText.setBounds(5, 50, 162, 380);
        descText.setForeground(new Color(0, 0, 0));
        descText.setFont(new Font("Arial", Font.PLAIN, 11));
        descText.setOpaque(false);
        descText.setEditable(false);
        descText.setLineWrap(true);
        descText.setWrapStyleWord(true);
        descText.setFocusable(false);
        descPanel.add(descText);

        layeredPane.add(descPanel, JLayeredPane.DEFAULT_LAYER);

JLayeredPane itemDisplay = new JLayeredPane();
itemDisplay.setBounds(860, 95, 172, 172);

JLabel bgLabel = new JLabel(new ImageIcon(getClass().getResource("/images/selected.png")));
bgLabel.setBounds(0, 0, 172, 172);
itemDisplay.add(bgLabel, JLayeredPane.DEFAULT_LAYER);

itemDisplayLabel = new JLabel();
itemDisplayLabel.setBounds(10, 10, 152, 152);
itemDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
itemDisplayLabel.setVerticalAlignment(SwingConstants.CENTER);
itemDisplay.add(itemDisplayLabel, JLayeredPane.PALETTE_LAYER);
layeredPane.add(itemDisplay, JLayeredPane.PALETTE_LAYER);

        refreshItems();

    }

    // ================= CLOSE =================
    void closeInventory() {
        JLayeredPane lp = Game.layeredPane();
        if (lp != null) {
            lp.remove(this);
        }
        instance = null;
        Game.hideOverlayIfNoModals();
        Buttons.updateInventoryIcon();
        if (lp != null) {
            lp.revalidate();
            lp.repaint();
        }
    }

    // creates a borderless image button at given position, 64x64
    private JButton makeImageTab(String imagePath, int x, int y) {
        JButton btn = new JButton(new ImageIcon(getClass().getResource(imagePath)));
        btn.setBounds(x, y, 64, 64);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }

    // ================= REFRESH GRID =================
    private void refreshItems() {
        bg.removeAll();
        int i = 0;

        for (java.util.Map.Entry<String, Integer> entry : items.entrySet()) {
            if (i >= 20) {
                break;
            }

            final String itemName = entry.getKey();
            int count = entry.getValue();

            boolean isFish = FISH_NAMES.contains(itemName)
                    || itemName.startsWith("Cut ")
                    || itemName.startsWith("Cooked ");

            if (mode.equals("bait")) {
                if (!itemName.endsWith("Bait")) {
                    continue;
                }
            } else if (mode.equals("cut")) {
                if (!FISH_NAMES.contains(itemName)) {
                    continue;
                }
            } else if (mode.equals("cook")) {
                if (!itemName.startsWith("Cut ")) {
                    continue;
                }
            } else {
                if (isBaitTab && !itemName.endsWith("Bait")) {
                    continue;
                }
                if (isItemsTab && (isFish || itemName.endsWith("Bait"))) {
                    continue;
                }
                if (!isItemsTab && !isBaitTab && (!isFish || itemName.endsWith("Bait"))) {
                    continue;
                }
            }

            JPanel slot = new JPanel(new BorderLayout()) {
                private Image slotImg = new ImageIcon(getClass().getResource("/images/slotbg.png")).getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            slot.setOpaque(false);
            slot.setPreferredSize(new Dimension(80, 80));
            slot.setBorder(null);

            String imagePath;
            if (itemName.startsWith("Cut ")) {
                imagePath = "/images/cut_" + itemName.replace("Cut ", "").toLowerCase() + ".png";
            } else if (itemName.startsWith("Cooked ")) {
                imagePath = "/images/cooked_" + itemName.replace("Cooked ", "").toLowerCase() + ".png";
            } else if (itemName.endsWith("Bait")) {
                switch (itemName) {
                    case "Worm Bait":   imagePath = "/images/wormBait.png";   break;
                    case "Insect Bait": imagePath = "/images/insectBait.png"; break;
                    case "Fish Bait":   imagePath = "/images/fishBait.png";   break;
                    case "Magic Bait":  imagePath = "/images/magicBait.png";  break;
                    default: imagePath = "/images/" + itemName.replace(" ", "") + ".png"; break;
                }
            } else {
                imagePath = "/images/" + itemName.toLowerCase().replace(" ", "") + ".png";
            }
            final String finalImagePath = imagePath;

            slot.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    slot.setBackground(Color.WHITE);
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    slot.setBackground(new Color(200, 180, 120));
                }

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    slot.setBackground(Color.YELLOW);
                    descName.setText(itemName);
                    java.net.URL dispUrl = getClass().getResource(finalImagePath);
                    if (dispUrl != null) {
                        Image dispImg = new ImageIcon(dispUrl).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        itemDisplayLabel.setIcon(new ImageIcon(dispImg));
                    } else {
                        itemDisplayLabel.setIcon(null);
                    }
                    String desc = descriptions.getOrDefault(itemName, "No description.");
                    descText.setText(desc);

                    if (mode.equals("inventory")) {
                        JPopupMenu popup = new JPopupMenu();
                        boolean isRod = itemName.endsWith("Rod");

                        if (isRod) {
                            boolean isEquipped = itemName.equals(PlayerData.equippedRod);
                            if (isEquipped) {
                                JButton unequipBtn = new JButton("Unequip");
                                unequipBtn.addActionListener(ev -> {
                                    PlayerData.equippedRod = null;
                                    popup.setVisible(false);
                                    refreshItems();
                                });
                                popup.add(unequipBtn);
                            } else {
                                JButton equipBtn = new JButton("Equip");
                                equipBtn.addActionListener(ev -> {
                                    PlayerData.equippedRod = itemName;
                                    popup.setVisible(false);
                                    refreshItems();
                                });
                                popup.add(equipBtn);
                            }
                        }

                        JButton deleteBtn = new JButton("Delete");
                        deleteBtn.addActionListener(ev -> {
                            if (itemName.equals(PlayerData.equippedRod)) {
                                PlayerData.equippedRod = null;
                            }
                            Inventory.removeItem(itemName);
                            popup.setVisible(false);
                            refreshItems();
                        });
                        popup.add(deleteBtn);
                        popup.show(slot, slot.getWidth(), 0);
                        return;
                    }

                    if (mode.equals("cut")) {
                        Inventory.removeItem(itemName);
                        Inventory.addItem("Cut " + itemName);
                        Cutting.playCutGif(itemName);
                        closeInventory();
                    }
                    if (mode.equals("cook")) {
                        Inventory.removeItem(itemName);
                        String cooked = "Cooked " + itemName.replace("Cut ", "");
                        Cooking.playCookGif(itemName);
                        Inventory.addItem(cooked);
                        closeInventory();
                    }
                    if (mode.equals("bait")) {
                        if (Fishing.selectedBait.equals(itemName)) {
                            Fishing.setSelectedBait("No Bait");
                        } else {
                            Fishing.setSelectedBait(itemName);
                        }
                        closeInventory();
                    }
                }
            });

            java.net.URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl == null) {
                imgUrl = getClass().getResource("/images/" + itemName.replace(" ", "") + ".png");
            }
            if (imgUrl == null && itemName.endsWith("Bait")) {
                imgUrl = getClass().getResource("/images/wormBait.png");
            }

            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image scaled = icon.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
// CHANGE: Create the label with ONLY the image, leaving the text out:
                JLabel iconLabel = new JLabel(new ImageIcon(scaled));
                // iconLabel.setVerticalTextPosition(SwingConstants.CENTER);
                // iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                slot.add(iconLabel, BorderLayout.CENTER);
            } else {
                slot.add(new JLabel(itemName, SwingConstants.CENTER), BorderLayout.CENTER);
            }

            if (itemName.equals(PlayerData.equippedRod)) {
                JLabel equippedLabel = new JLabel("Equipped");
                equippedLabel.setFont(new Font("Arial", Font.BOLD, 10));
                equippedLabel.setForeground(Color.GREEN);
                equippedLabel.setHorizontalAlignment(SwingConstants.CENTER);
                slot.add(equippedLabel, BorderLayout.NORTH);
            }

            // if (count > 1) {
            //     JLabel countLabel = new JLabel("x" + count);
            //     countLabel.setFont(new Font("Arial", Font.BOLD, 12));
            //     countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            //     slot.add(countLabel, BorderLayout.NORTH);
            // }
            bg.add(slot);
            i++;
        }

        while (i < 20) {
            JPanel slot = new JPanel() {
                private Image slotImg = new ImageIcon(getClass().getResource("/images/slotbg.png")).getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            slot.setOpaque(false);
            slot.setPreferredSize(new Dimension(80, 80));
            slot.setBorder(null);
            bg.add(slot);
            i++;
        }

        bg.revalidate();
        bg.repaint();
    }

    // ================= STATIC HELPERS =================
    public static void addItem(String itemName) {
        items.put(itemName, items.getOrDefault(itemName, 0) + 1);
        if (instance != null) {
            instance.refreshItems();
        }
    }

    public static void removeItem(String itemName) {
        if (!items.containsKey(itemName)) {
            return;
        }
        int count = items.get(itemName);
        if (count <= 1) {
            items.remove(itemName); 
        }else {
            items.put(itemName, count - 1);
        }
        if (instance != null) {
            instance.refreshItems();
        }
    }

    public static String getFirstFish() {
        for (String item : items.keySet()) {
            if (items.getOrDefault(item, 0) > 0 && FISH_NAMES.contains(item)) {
                return item;
            }
        }
        return null;
    }

    public static String getFirstCutFish() {
        for (String item : items.keySet()) {
            if (item.startsWith("Cut ") && items.getOrDefault(item, 0) > 0) {
                return item;
            }
        }
        return null;
    }

    // ================= TOGGLE =================
    public static void toggleInventory() {
        Buttons.closeAllDropdowns();
        if (instance != null) {
            instance.closeInventory();
            return;
        }
        instance = new Inventory("inventory");
        JLayeredPane lp = Game.layeredPane();
        // center it within the game window, offset left by 60 so tabs overhang
        int x = (1350 - 1058) / 2 - 60;
        int y = (750 - 580) / 2;
        instance.setBounds(x, y, 1058, 580);
        lp.add(instance, JLayeredPane.POPUP_LAYER);
        lp.revalidate();
        lp.repaint();
        Game.showOverlay();
        Buttons.updateInventoryIcon();
    }

    public static void toggleWithMode(String mode) {
        Buttons.closeAllDropdowns();
        if (instance != null) {
            instance.closeInventory();
            return;
        }
        instance = new Inventory(mode);
        JLayeredPane lp = Game.layeredPane();
        int x = (1350 - 1058) / 2 - 60;
        int y = (750 - 580) / 2;
        instance.setBounds(x, y, 1058, 580);
        lp.add(instance, JLayeredPane.POPUP_LAYER);
        lp.revalidate();
        lp.repaint();
        Game.showOverlay();
        Buttons.updateInventoryIcon();
    }
}
