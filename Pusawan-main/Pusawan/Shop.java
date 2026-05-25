package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Shop extends JFrame {

    private JPanel bg;
    static Shop instance;

    private static final java.util.Map<String, Integer> buyPrices = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> buyItems  = new java.util.LinkedHashMap<>();

    static {
        buyPrices.put("Worm Bait",   20);
        buyPrices.put("Insect Bait", 25);
        buyPrices.put("Fish Bait",   35);
        buyPrices.put("Magic Bait",  100);

        buyItems.put("bambooRod", 2000);
        buyItems.put("hotdogRod", 5000);
    }

    private boolean isBuyItemsTab = false;

    public Shop(String mode) {
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

        // ===== COINS =====
        JPanel coinsPanel = new JPanel(null);
        coinsPanel.setBounds(30, 45, 700, 45);
        coinsPanel.setBackground(new Color(50, 40, 30));

        JLabel coinsLabel = PlayerData.createMoneyLabel();
        coinsLabel.setBounds(10, 8, 300, 30);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        coinsLabel.setForeground(Color.WHITE);
        coinsPanel.add(coinsLabel);
        layeredPane.add(coinsPanel, JLayeredPane.DEFAULT_LAYER);

        // ===== TABS =====
        JPanel tabPanel = new JPanel(null);
        tabPanel.setBounds(30, 95, 700, 40);
        tabPanel.setBackground(new Color(40, 30, 20));
        layeredPane.add(tabPanel, JLayeredPane.DEFAULT_LAYER);

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



        // ===== GRID =====
        bg = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 8));
        bg.setOpaque(false);
        bg.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bg.setPreferredSize(new Dimension(680, 900));

        JPanel bgWrapper = new JPanel(new BorderLayout());
        bgWrapper.setOpaque(false);
        bgWrapper.add(bg, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(bgWrapper);
        scrollPane.setBounds(30, 140, 700, 510);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        refreshItems();
        setVisible(true);
    }

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

    private void refreshItems() {
        bg.removeAll();
        int i = 0;

        java.util.Map<String, Integer> source = isBuyItemsTab ? buyItems : buyPrices;

        for (java.util.Map.Entry<String, Integer> entry : source.entrySet()) {
            if (i >= 20) break;

            String name  = entry.getKey();
            int    price = entry.getValue();

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

            java.net.URL imgUrl = getClass().getResource(resolveImagePath(name));
            if (imgUrl != null) {
                Image scaled = new ImageIcon(imgUrl).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                JLabel iconLabel = new JLabel(name + " ₱" + price, new ImageIcon(scaled), SwingConstants.CENTER);
                iconLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                iconLabel.setForeground(Color.WHITE);
                iconLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                slot.add(iconLabel, BorderLayout.CENTER);
            } else {
                JLabel itemLabel = new JLabel(name + " ₱" + price, SwingConstants.CENTER);
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

    @Override
    public void dispose() {
        super.dispose();
        instance = null;
        if (Inventory.instance == null) Game.hideOverlay();
    }

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
        return "/images/" + Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", "") + ".png";
    }
}