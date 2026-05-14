package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Shop extends JFrame {

    private JPanel bg;
    static Shop instance;

    private static final java.util.Map<String, Integer> sellPrices = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> buyPrices  = new java.util.LinkedHashMap<>();

    static {
        sellPrices.put("Cooked Bass",    80);
        sellPrices.put("Cooked Catfish", 60);
        sellPrices.put("Cooked Carp",    50);
        sellPrices.put("Cooked Perch",   30);
        sellPrices.put("Bass",    20);
        sellPrices.put("Catfish", 15);
        sellPrices.put("Carp",    12);
        sellPrices.put("Perch",    7);
        buyPrices.put("Worm Bait",   20);
        buyPrices.put("Insect Bait", 25);
        buyPrices.put("Fish Bait",   35);
        buyPrices.put("Magic Bait",  100);
        sellPrices.put("Sandal", 5);
        sellPrices.put("Shoe", 5);
        sellPrices.put("Plastic Wrapper", 2);
    }

    private boolean isBuyTab = false;
    private String shopMode = "both";

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

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!layeredPane.getBounds().contains(e.getPoint())) {
                    dispose();
                }
            }
        });

        add(layeredPane);

        // HEADER
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
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.addActionListener(e -> { dispose(); });
        header.add(closeButton);

        layeredPane.add(header, JLayeredPane.PALETTE_LAYER);

        // COINS
        JPanel coinsPanel = new JPanel(null);
        coinsPanel.setBounds(30, 45, 700, 45);
        coinsPanel.setBackground(new Color(50, 40, 30));

        JLabel coinsLabel = PlayerData.createMoneyLabel();
        coinsLabel.setBounds(10, 8, 300, 30);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        coinsLabel.setForeground(Color.WHITE);
        coinsPanel.add(coinsLabel);

        JButton sellAllButton = new JButton("Sell All");
        sellAllButton.setBounds(560, 8, 120, 30);
        sellAllButton.addActionListener(e -> { sellAll(); refreshItems(); });
        sellAllButton.setVisible(!mode.equals("buy"));
        coinsPanel.add(sellAllButton);

        layeredPane.add(coinsPanel, JLayeredPane.DEFAULT_LAYER);

        // TABS
        JPanel tabPanel = new JPanel(null);
        tabPanel.setBounds(30, 95, 700, 40);
        tabPanel.setBackground(new Color(40, 30, 20));

        boolean showSell = !mode.equals("buy");
        boolean showBuy  = !mode.equals("sell");

        JButton sellTab = new JButton("Sell");
        sellTab.setBounds(0, 0, 175, 40);
        sellTab.setFont(new Font("Arial", Font.BOLD, 14));
        sellTab.setBackground(new Color(90, 70, 45));
        sellTab.setForeground(Color.WHITE);
        sellTab.setFocusPainted(false);
        sellTab.setBorderPainted(false);

        JButton buyTab = new JButton("Buy");
        buyTab.setBounds(mode.equals("buy") ? 0 : 175, 0, 175, 40);
        buyTab.setFont(new Font("Arial", Font.BOLD, 14));
        buyTab.setBackground(new Color(50, 40, 30));
        buyTab.setForeground(new Color(180, 160, 120));
        buyTab.setFocusPainted(false);
        buyTab.setBorderPainted(false);

        if (showSell) tabPanel.add(sellTab);
        if (showBuy)  tabPanel.add(buyTab);

        if (mode.equals("buy")) {
            buyTab.setBackground(new Color(90, 70, 45));
            buyTab.setForeground(Color.WHITE);
        }

        sellTab.addActionListener(e -> {
            if (isBuyTab) {
                isBuyTab = false;
                sellTab.setBackground(new Color(90, 70, 45)); sellTab.setForeground(Color.WHITE);
                buyTab.setBackground(new Color(50, 40, 30));  buyTab.setForeground(new Color(180, 160, 120));
                sellAllButton.setVisible(true);
                refreshItems();
            }
        });

        buyTab.addActionListener(e -> {
            if (!isBuyTab) {
                isBuyTab = true;
                buyTab.setBackground(new Color(90, 70, 45)); buyTab.setForeground(Color.WHITE);
                sellTab.setBackground(new Color(50, 40, 30)); sellTab.setForeground(new Color(180, 160, 120));
                sellAllButton.setVisible(false);
                refreshItems();
            }
        });

        layeredPane.add(tabPanel, JLayeredPane.DEFAULT_LAYER);

        // GRID
        bg = new JPanel();
        bg.setBackground(new Color(60, 45, 30));
        bg.setBounds(30, 140, 700, 510);
        bg.setLayout(new GridLayout(4, 5, 5, 5));
        bg.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        layeredPane.add(bg, JLayeredPane.DEFAULT_LAYER);

        refreshItems();
        setVisible(true);
    }

    private void refreshItems() {
        bg.removeAll();
        int i = 0;

        if (!isBuyTab) {
            for (java.util.Map.Entry<String, Integer> entry : Inventory.items.entrySet()) {
                if (i >= 20) break;
                String name  = entry.getKey();
                int    count = entry.getValue();
                if (!name.startsWith("Cooked ") && !sellPrices.containsKey(name)) continue;
                int price = sellPrices.getOrDefault(name, 0);

                JPanel slot = new JPanel(new BorderLayout());
                slot.setBackground(new Color(90, 70, 45));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                String imagePath;
                if (name.startsWith("Cooked "))
                    imagePath = "/images/cooked_" + name.replace("Cooked ", "").toLowerCase() + ".png";
                else
                    imagePath = "/images/" + name.toLowerCase().replace(" ", "") + ".png";

                java.net.URL imgUrl = getClass().getResource(imagePath);
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    slot.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);
                } else {
                    JLabel itemLabel = new JLabel(name + "  ₱" + price, SwingConstants.CENTER);
                    itemLabel.setForeground(Color.WHITE);
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                    slot.add(itemLabel, BorderLayout.CENTER);
                }

                JLabel countLabel = new JLabel("x" + count + "  ₱" + price, SwingConstants.RIGHT);
                countLabel.setForeground(Color.YELLOW);
                countLabel.setFont(new Font("Arial", Font.BOLD, 12));
                slot.add(countLabel, BorderLayout.NORTH);

                JButton sellOne = new JButton("Sell");
                sellOne.addActionListener(e -> {
                    Inventory.removeItem(name);
                    PlayerData.addMoney(price);
                    refreshItems();
                });
                slot.add(sellOne, BorderLayout.SOUTH);

                bg.add(slot);
                i++;
            }
        } else {
            for (java.util.Map.Entry<String, Integer> entry : buyPrices.entrySet()) {
                if (i >= 20) break;
                String name  = entry.getKey();
                int    price = entry.getValue();

                JPanel slot = new JPanel(new BorderLayout());
                slot.setBackground(new Color(90, 70, 45));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                String baitPath = "/images/" + Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", "") + ".png";
                java.net.URL baitUrl = getClass().getResource(baitPath);
                if (baitUrl != null) {
                    ImageIcon icon = new ImageIcon(baitUrl);
                    Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    JLabel iconLabel = new JLabel(name + "  ₱" + price, new ImageIcon(scaled), SwingConstants.CENTER);
                    iconLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                    iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    iconLabel.setForeground(Color.WHITE);
                    slot.add(iconLabel, BorderLayout.CENTER);
                } else {
                    JLabel itemLabel = new JLabel(name + "  ₱" + price, SwingConstants.CENTER);
                    itemLabel.setForeground(Color.WHITE);
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                    slot.add(itemLabel, BorderLayout.CENTER);
                }

                JButton buyBtn = new JButton("Buy");
                buyBtn.addActionListener(e -> {
                    if (PlayerData.getMoney() >= price) {
                        PlayerData.addMoney(-price);
                        Inventory.addItem(name);
                        refreshItems();
                    }
                });
                slot.add(buyBtn, BorderLayout.SOUTH);

                bg.add(slot);
                i++;
            }
        }

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

    private void sellAll() {
        for (java.util.Map.Entry<String, Integer> entry :
                new java.util.LinkedHashMap<>(Inventory.items).entrySet()) {
            String name = entry.getKey();
            if (!sellPrices.containsKey(name)) continue;
            int count = entry.getValue();
            int price = sellPrices.getOrDefault(name, 0);
            PlayerData.addMoney(price * count);
            for (int i = 0; i < count; i++) Inventory.removeItem(name);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        instance = null;
        Game.hideOverlayIfNoModals();
    }

    public static void toggleShop(String mode) {
        if (instance == null || !instance.isDisplayable()) {
            instance = new Shop(mode);
            Game.showOverlay();
        } else {
            instance.dispose();
            instance = null;
            Game.hideOverlayIfNoModals();
        }
    }
}