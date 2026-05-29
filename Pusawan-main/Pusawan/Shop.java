package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Shop extends JFrame {

    private JPanel bg;
    static Shop instance;

    // ===== PRICES =====
    private static final java.util.Map<String, Integer> sellPrices = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> buyPrices  = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> buyItems = new java.util.LinkedHashMap<>();

    private boolean isBuyItemsTab = false;

    static {
        // cooked fish (highest value)
        sellPrices.put("Cooked Bass",    80);
        sellPrices.put("Cooked Catfish", 60);
        sellPrices.put("Cooked Carp",    50);
        sellPrices.put("Cooked Perch",   30);

        // raw fish
        sellPrices.put("Bass",    20);
        sellPrices.put("Catfish", 15);
        sellPrices.put("Carp",    12);
        sellPrices.put("Perch",    7);

        // cut fish
        sellPrices.put("Cut Bass",    10);
        sellPrices.put("Cut Catfish",  8);
        sellPrices.put("Cut Carp",     6);
        sellPrices.put("Cut Perch",    4);

        // bait (sell prices €” lower than buy)
        sellPrices.put("Worm Bait",   10);
        sellPrices.put("Insect Bait", 12);
        sellPrices.put("Fish Bait",   17);
        sellPrices.put("Magic Bait",  50);

        // junk
        sellPrices.put("Sandal",          5);
        sellPrices.put("Shoe",            5);
        sellPrices.put("Plastic Wrapper", 2);

        // bait buy prices
        buyPrices.put("Worm Bait",   20);
        buyPrices.put("Insect Bait", 25);
        buyPrices.put("Fish Bait",   35);
        buyPrices.put("Magic Bait",  100);

        buyItems.put("Default Rod", 0);
        buyItems.put("Bamboo Rod", 2000);
        buyItems.put("Hotdog Rod", 5000);

    }

    // ===== STATE =====
    private boolean isBuyTab      = false; // used in "both" mode only
    private boolean isSellBaitTab = false; // sell mode: bait tab active
    private boolean isSellItemsTab= false; // sell mode: items/junk tab active
    // if both false in sell mode †’ fish tab is active
    private String shopMode = "both"; // "sell", "buy", or "both"

    // ===== CONSTRUCTOR =====
    public Shop(String mode) {
        this.shopMode = mode;
        if (mode.equals("buy")) isBuyTab = true;

        setTitle("Shop");
        setSize(760, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        setCursor(playerCursor.getCustomCursor());
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 760, 680);
        add(layeredPane);

        // close when clicking outside the shop window
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!layeredPane.getBounds().contains(e.getPoint())) dispose();
            }
        });

        // ===== HEADER =====
        JPanel header = new JPanel(null);
        header.setBounds(30, 0, 700, 40);
        header.setOpaque(true);
        header.setBackground(new Color(0x1800ad));

        JLabel title = new JLabel(new ImageIcon(getClass().getResource("/images/storeHeader.png")));
        title.setBounds(0, 0, 700, 40);
        header.add(title);

        JButton closeButton = new JButton("X");
        closeButton.setBounds(650, 0, 50, 40);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        Buttons.addClickSound(closeButton);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.addActionListener(e -> dispose());
        header.add(closeButton);

        layeredPane.add(header, JLayeredPane.PALETTE_LAYER);

        // ===== COINS ROW =====
        JPanel coinsPanel = new JPanel(null);
        coinsPanel.setBounds(30, 45, 700, 45);
        coinsPanel.setBackground(new Color(50, 40, 30));

        JLabel coinsLabel = PlayerData.createMoneyLabel();
        coinsLabel.setBounds(10, 8, 300, 30);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        coinsLabel.setForeground(Color.WHITE);
        coinsPanel.add(coinsLabel);

        // "Sell All" only visible on sell tabs
        JButton sellAllButton = new JButton("Sell All");
        sellAllButton.setBounds(560, 8, 120, 30);
        Buttons.addClickSound(sellAllButton);
        sellAllButton.addActionListener(e -> { sellAll(); refreshItems(); });
        sellAllButton.setVisible(!mode.equals("buy"));
        coinsPanel.add(sellAllButton);

        layeredPane.add(coinsPanel, JLayeredPane.DEFAULT_LAYER);

        // ===== TAB ROW =====
        JPanel tabPanel = new JPanel(null);
        tabPanel.setBounds(30, 95, 700, 40);
        tabPanel.setBackground(new Color(40, 30, 20));
        layeredPane.add(tabPanel, JLayeredPane.DEFAULT_LAYER);

        // --- SELL MODE: show Fish / Bait / Items subtabs ---
        if (mode.equals("sell")) {
            JButton sellFishTab  = makeTab("Fish",  0,   175);
            JButton sellBaitTab  = makeTab("Bait",  175, 175);
            JButton sellItemsTab = makeTab("Items", 350, 175);

            // fish tab active by default
            setActive(sellFishTab);
            setInactive(sellBaitTab);
            setInactive(sellItemsTab);

            sellFishTab.addActionListener(e -> {
                isSellBaitTab = false; isSellItemsTab = false;
                setActive(sellFishTab); setInactive(sellBaitTab); setInactive(sellItemsTab);
                sellAllButton.setVisible(true);
                refreshItems();
            });
            sellBaitTab.addActionListener(e -> {
                isSellBaitTab = true; isSellItemsTab = false;
                setActive(sellBaitTab); setInactive(sellFishTab); setInactive(sellItemsTab);
                sellAllButton.setVisible(true);
                refreshItems();
            });
            sellItemsTab.addActionListener(e -> {
                isSellItemsTab = true; isSellBaitTab = false;
                setActive(sellItemsTab); setInactive(sellFishTab); setInactive(sellBaitTab);
                sellAllButton.setVisible(true);
                refreshItems();
            });

            tabPanel.add(sellFishTab);
            tabPanel.add(sellBaitTab);
            tabPanel.add(sellItemsTab);

        // --- BUY MODE: show only Buy tab ---
        } else if (mode.equals("buy")) {
            JButton buyBaitTab  = makeTab("Bait",  0,   175);
            JButton buyItemsTab = makeTab("Items", 175, 175);
            setActive(buyBaitTab);
            setInactive(buyItemsTab);

            buyBaitTab.addActionListener(e -> {
                isBuyItemsTab = false;
                setActive(buyBaitTab); setInactive(buyItemsTab);
                refreshItems();
            });
            buyItemsTab.addActionListener(e -> {
                isBuyItemsTab = true;
                setActive(buyItemsTab); setInactive(buyBaitTab);
                refreshItems();
            });

            tabPanel.add(buyBaitTab);
            tabPanel.add(buyItemsTab);

        // --- BOTH MODE: show Sell / Buy tabs ---
        } else {
            JButton sellTab = makeTab("Sell", 0,   175);
            JButton buyTab  = makeTab("Buy",  175, 175);
            setActive(sellTab);
            setInactive(buyTab);

            sellTab.addActionListener(e -> {
                if (isBuyTab) {
                    isBuyTab = false;
                    setActive(sellTab); setInactive(buyTab);
                    sellAllButton.setVisible(true);
                    refreshItems();
                }
            });
            buyTab.addActionListener(e -> {
                if (!isBuyTab) {
                    isBuyTab = true;
                    setActive(buyTab); setInactive(sellTab);
                    sellAllButton.setVisible(false);
                    refreshItems();
                }
            });

            tabPanel.add(sellTab);
            tabPanel.add(buyTab);
        }

        // ===== ITEM GRID =====
        // 4 rows x 5 cols = 20 slots
        bg = new JPanel();
        bg.setBackground(new Color(60, 45, 30));
        bg.setBounds(30, 140, 700, 510);
        bg.setLayout(new GridLayout(4, 5, 5, 5));
        bg.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        layeredPane.add(bg, JLayeredPane.DEFAULT_LAYER);

        refreshItems();
        setVisible(true);
    }

    // ===== TAB HELPERS =====
    // creates a styled tab button at the given x position with given width
    private JButton makeTab(String label, int x, int width) {
        JButton tab = new JButton(label);
        Buttons.addClickSound(tab);
        tab.setBounds(x, 0, width, 40);
        tab.setFont(new Font("Arial", Font.BOLD, 14));
        tab.setFocusPainted(false);
        tab.setBorderPainted(false);
        setInactive(tab);
        return tab;
    }

    private void setActive(JButton tab) {
        tab.setBackground(new Color(90, 70, 45));
        tab.setForeground(Color.WHITE);
    }

    private void setInactive(JButton tab) {
        tab.setBackground(new Color(50, 40, 30));
        tab.setForeground(new Color(180, 160, 120));
    }

    // ===== REFRESH GRID =====
    private void refreshItems() {
        bg.removeAll();
        int i = 0;

        if (isBuyTab || shopMode.equals("buy")) {
            java.util.Map<String, Integer> source = isBuyItemsTab ? buyItems : buyPrices;
            // --- BUY TAB: show purchasable baits ---
            for (java.util.Map.Entry<String, Integer> entry : source.entrySet()) {
                if (i >= 20) break;
                        String name  = entry.getKey();
                        int    price = entry.getValue();

                        JPanel slot = new JPanel(new BorderLayout());
                        slot.setBackground(new Color(90, 70, 45));
                        slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                        java.net.URL imgUrl = getClass().getResource(resolveImagePath(name));
                        if (imgUrl != null) {
                            ImageIcon icon = new ImageIcon(imgUrl);
                            Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                            JLabel iconLabel = new JLabel(name + "  ‚₱" + price, new ImageIcon(scaled), SwingConstants.CENTER);
                            iconLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                            iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                            iconLabel.setForeground(Color.WHITE);
                            slot.add(iconLabel, BorderLayout.CENTER);
                        } else {
                            JLabel itemLabel = new JLabel(name + "  ‚₱" + price, SwingConstants.CENTER);
                            itemLabel.setForeground(Color.WHITE);
                            itemLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                            slot.add(itemLabel, BorderLayout.CENTER);
                        }

                        boolean isRod = name.endsWith("Rod");
                        boolean owned = isRod && Inventory.items.getOrDefault(name, 0) > 0;
                        JButton buyBtn = new JButton(owned ? "Owned" : "Buy");
                        Buttons.addClickSound(buyBtn);
                        buyBtn.setEnabled(!owned);
                        buyBtn.addActionListener(e -> {
                            if (PlayerData.getMoney() >= price) {
                                PlayerData.addMoney(-price);
                                Inventory.addItem(name);
                                refreshItems();
                            }
                        });
                        slot.add(buyBtn, BorderLayout.SOUTH);

                        if (name.equals("Bamboo Rod") && PlayerData.level < 5) {
                            buyBtn.setEnabled(false);
                            buyBtn.setText("Lv 5");
                            slot = new JPanel(new BorderLayout()) {
                                private Image lockImg = new ImageIcon(imgUrl).getImage();
                                @Override
                                protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    g.drawImage(lockImg, 0, 0, getWidth(), getHeight(), this);
                                    g.setColor(new Color(0, 0, 0, 140));
                                    g.fillRect(0, 0, getWidth(), getHeight());
                                    g.setColor(Color.WHITE);
                                    g.setFont(new Font("Arial", Font.BOLD, 12));
                                    g.drawString("Lv 5", 25, 45);
                                }
                            };
                            slot.setBackground(new Color(90, 70, 45));
                            slot.setOpaque(true);
                            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                            slot.add(buyBtn, BorderLayout.SOUTH);
                        }

                        if (name.equals("Hotdog Rod") && PlayerData.level < 10) {
                            buyBtn.setEnabled(false);
                            buyBtn.setText("Lv 10");
                            slot = new JPanel(new BorderLayout()) {
                                private Image lockImg = new ImageIcon(imgUrl).getImage();
                                @Override
                                protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    g.drawImage(lockImg, 0, 0, getWidth(), getHeight(), this);
                                    g.setColor(new Color(0, 0, 0, 140));
                                    g.fillRect(0, 0, getWidth(), getHeight());
                                    g.setColor(Color.WHITE);
                                    g.setFont(new Font("Arial", Font.BOLD, 12));
                                    g.drawString("Lv 10", 22, 45);
                                }
                            };
                            slot.setBackground(new Color(90, 70, 45));
                            slot.setOpaque(true);
                            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                            slot.add(buyBtn, BorderLayout.SOUTH);
                        }

                        bg.add(slot);
                        i++;
                    }

        } else {
            // --- SELL TAB: show sellable inventory items filtered by subtab ---
            for (java.util.Map.Entry<String, Integer> entry : Inventory.items.entrySet()) {
                if (i >= 20) break;
                String name  = entry.getKey();
                int    count = entry.getValue();

                // only show items that have a sell price
                if (!sellPrices.containsKey(name)) continue;

                // sell mode subtab filter
                if (shopMode.equals("sell")) {
                    boolean isFish = name.equals("Bass") || name.equals("Catfish")
                        || name.equals("Carp") || name.equals("Perch")
                        || name.startsWith("Cut ") || name.startsWith("Cooked ");
                    boolean isBait = name.endsWith("Bait");
                    // anything else is junk (Sandal, Shoe, Plastic Wrapper)

                    if (isSellBaitTab  && !isBait) continue;
                    if (isSellItemsTab && (isFish || isBait)) continue;
                    if (!isSellBaitTab && !isSellItemsTab && !isFish) continue;
                }

                int price = sellPrices.getOrDefault(name, 0);

                JPanel slot = new JPanel(new BorderLayout());
                slot.setBackground(new Color(90, 70, 45));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                // image path resolution
                java.net.URL imgUrl = getClass().getResource(resolveImagePath(name));
                
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    slot.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);
                } else {
                    JLabel itemLabel = new JLabel(name + " ₱" + price, SwingConstants.CENTER);
                    itemLabel.setForeground(Color.WHITE);
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                    slot.add(itemLabel, BorderLayout.CENTER);
                }

                JLabel countLabel = new JLabel("x" + count + " ₱" + price, SwingConstants.RIGHT);
                countLabel.setForeground(Color.YELLOW);
                countLabel.setFont(new Font("Arial", Font.BOLD, 12));
                slot.add(countLabel, BorderLayout.NORTH);

                JButton sellOne = new JButton("Sell");
                Buttons.addClickSound(sellOne);
                sellOne.addActionListener(e -> {
                    Inventory.removeItem(name);
                    PlayerData.addMoney(price);
                    refreshItems();
                });
                slot.add(sellOne, BorderLayout.SOUTH);

                bg.add(slot);
                i++;
            }
        }

        // fill remaining slots with empty panels
        while (i < 20) {
            JPanel slot = new JPanel();
            slot.setBackground(new Color(90, 70, 45));
            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            bg.add(slot);
            i++;
        }

        bg.revalidate();
        bg.repaint();
    }

    // ===== SELL ALL =====
    // sells every item in inventory that has a sell price, respecting current subtab filter
    private void sellAll() {
        for (java.util.Map.Entry<String, Integer> entry :
                new java.util.LinkedHashMap<>(Inventory.items).entrySet()) {
            String name = entry.getKey();
            if (!sellPrices.containsKey(name)) continue;

            if (shopMode.equals("sell")) {
                boolean isFish = name.equals("Bass") || name.equals("Catfish")
                    || name.equals("Carp") || name.equals("Perch")
                    || name.startsWith("Cut ") || name.startsWith("Cooked ");
                boolean isBait = name.endsWith("Bait");
                if (isSellBaitTab  && !isBait) continue;
                if (isSellItemsTab && (isFish || isBait)) continue;
                if (!isSellBaitTab && !isSellItemsTab && !isFish) continue;
            }

            int count = entry.getValue();
            int price = sellPrices.getOrDefault(name, 0);
            PlayerData.addMoney(price * count);
            for (int i = 0; i < count; i++) Inventory.removeItem(name);
        }
    }

    // ===== DISPOSE =====
    @Override
    public void dispose() {
        super.dispose();
        instance = null;
        if (Inventory.instance == null) Game.hideOverlay();
    }

    // ===== TOGGLE =====
    // call with "sell", "buy", or "both" €” toggles the shop open/closed
    public static void toggleShop(String mode) {
        Buttons.closeAllDropdowns();
        if (instance != null && instance.isDisplayable()) {
            instance.dispose();
            return;
        }
        instance = new Shop(mode);
        Game.showOverlay();
    }

    private String resolveImagePath(String name) {
        if (name.startsWith("Cooked "))
            return "/images/cooked_" + name.replace("Cooked ", "").toLowerCase() + ".png";
        if (name.startsWith("Cut "))
            return "/images/cut_" + name.replace("Cut ", "").toLowerCase() + ".png";
        if (name.endsWith("Bait"))
            return "/images/" + Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", "") + ".png";
        // default: camelCase
        return "/images/" + Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", "") + ".png";
    }
}