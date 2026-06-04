package Pusawan;
import java.awt.*;
import javax.swing.*;
public class Buy extends JPanel {
    // ================================================================
    // BAIT STOCK  (restocks every 5 minutes)
    // ================================================================
    public static java.util.Map<String, Integer> baitStock = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> BAIT_MAX = new java.util.LinkedHashMap<>();

    static {
        BAIT_MAX.put("Worm Bait",   20);
        BAIT_MAX.put("Insect Bait", 15);
        BAIT_MAX.put("Fish Bait",   10);
        BAIT_MAX.put("Magic Bait",   5);

        java.util.Random rng = new java.util.Random();
        for (java.util.Map.Entry<String, Integer> e : BAIT_MAX.entrySet())
            baitStock.put(e.getKey(), rng.nextInt(e.getValue() + 1));
    }

    // ================================================================
    // ITEM STOCK  (restocks every 5 minutes, different timer offset)
    // ================================================================
    public static java.util.Map<String, Integer> itemStock = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> ITEM_MAX = new java.util.LinkedHashMap<>();

    static {
        ITEM_MAX.put("Default Rod", 1);
        ITEM_MAX.put("Bamboo Rod",  1);
        ITEM_MAX.put("Hotdog Rod",  1);

        java.util.Random rng = new java.util.Random();
        for (java.util.Map.Entry<String, Integer> e : ITEM_MAX.entrySet())
            itemStock.put(e.getKey(), rng.nextInt(e.getValue() + 1));
    }

    // ================================================================
    // RESTOCK TIMERS  (separate countdowns per tab)
    // ================================================================
    private static int baitCountdown = 300;
    private static int itemCountdown = 300;
    static Buy shopPanelRef = null;

    private static javax.swing.Timer restockTimer = new javax.swing.Timer(1000, e -> {
        baitCountdown--;
        itemCountdown--;

        if (baitCountdown <= 0) {
            java.util.Random rng = new java.util.Random();
            for (java.util.Map.Entry<String, Integer> en : BAIT_MAX.entrySet())
                baitStock.put(en.getKey(), rng.nextInt(en.getValue() + 1));
            baitCountdown = 300;
        }

        if (itemCountdown <= 0) {
            java.util.Random rng = new java.util.Random();
            for (java.util.Map.Entry<String, Integer> en : ITEM_MAX.entrySet())
                itemStock.put(en.getKey(), rng.nextInt(en.getValue() + 1));
            itemCountdown = 300;
        }

        if (shopPanelRef != null) shopPanelRef.updateCountdown();
    });

    static { restockTimer.start(); }

    // ================================================================
    // FIELDS
    // ================================================================
    public ImageIcon storeBackgroundGif;
    private Image hover;
    private boolean hovering = false;
    private Rectangle storeZone = new Rectangle(530, 200, 480, 480);

    // Grid state
    private JPanel bg;
    private JLabel countdownLabel;
    private String activeTab = "Bait"; // "Bait" or "Items"
    private static java.util.Set<String> ownedItems = new java.util.HashSet<>();

    // Tab buttons (kept as fields so we can update their style)
    private JButton baitTabBtn;
    private JButton itemTabBtn;

    // Layout constants — tweak these to adjust fit
    private static final int SLOT_SIZE = 130;
    private static final int SLOT_GAP  = 6;
    private static final int COLS      = 5;
    private static final int ROWS      = 3;
    private static final int GRID_W    = COLS * SLOT_SIZE + (COLS - 1) * SLOT_GAP; // 440
    private static final int GRID_H    = ROWS * SLOT_SIZE + (ROWS - 1) * SLOT_GAP; // 260
    private static final int GRID_X    = (800 - GRID_W) / 2;  // 202 — centered
    private static final int GRID_Y    = 155; // below header + tabs + money

    // ================================================================
    // POPUP INSTANCE
    // ================================================================
    static ShopPopup shopPopupInstance = null;

    // ================================================================
    // CONSTRUCTOR  (full-screen store background)
    // ================================================================
    public Buy() {
        loadBackgroundImage();
        new javax.swing.Timer(24, e -> repaint()).start();

        BackgroundPanel storebackground = new BackgroundPanel();
        java.net.URL hoverUrl = getClass().getResource("/images/storeBackgroundSelected.png");
        hover = (hoverUrl != null) ? new ImageIcon(hoverUrl).getImage() : null;

        setLayout(new BorderLayout());
        add(storebackground, BorderLayout.CENTER);
        storebackground.setLayout(null);

        storebackground.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                hovering = storeZone.contains(e.getPoint()) && shopPopupInstance == null;
                repaint();
            }
        });

        storebackground.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Buttons.closeAllDropdowns();
                if (storeZone.contains(e.getPoint())) {
                    toggleShopPopup();
                } else {
                    if (shopPopupInstance != null) closeShopPopup();
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                }
            }
        });

        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        storebackground.add(menuButton);
        storebackground.add(Buttons.createInventoryButton());

        JButton settingsButton = Buttons.toSettings();
        settingsButton.setBounds(1266, 20, 64, 64);
        storebackground.add(settingsButton);

        java.net.URL arrowUrl = getClass().getResource("/images/arrowRight.png");
        if (arrowUrl != null) {
            JButton arrowRight = new JButton(new ImageIcon(arrowUrl));
            arrowRight.setBounds(1266, 400, 64, 64);
            arrowRight.setBorderPainted(false);
            arrowRight.setContentAreaFilled(false);
            arrowRight.setFocusPainted(false);
            Buttons.addClickSound(arrowRight);
            arrowRight.addActionListener(e -> Game.navigate(Game.SELL));
            storebackground.add(arrowRight);
        }
    }

    public void loadBackgroundImage() {
        java.net.URL bgUrl = getClass().getResource("/images/storeBackground.gif");
        storeBackgroundGif = (bgUrl != null) ? new ImageIcon(bgUrl) : new ImageIcon();
    }

    // ================================================================
    // TOGGLE / CLOSE POPUP
    // ================================================================
    public static void toggleShopPopup() {
        Buttons.closeAllDropdowns();
        if (shopPopupInstance != null) { closeShopPopup(); return; }
        shopPopupInstance = new ShopPopup();
        shopPanelRef = shopPopupInstance.getBuyRef();
        JLayeredPane lp = Game.layeredPane();
        int x = (1350 - 845) / 2;
        int y = (750 - 580) / 2;
        shopPopupInstance.setBounds(x, y, 845, 580);
        lp.add(shopPopupInstance, JLayeredPane.POPUP_LAYER);
        lp.revalidate();
        lp.repaint();
        Game.showOverlay();

        Game.overlayPanel().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                closeShopPopup();
            }
        });
    }

    public static void closeShopPopup() {
        if (shopPopupInstance != null) {
            JLayeredPane lp = Game.layeredPane();
            lp.remove(shopPopupInstance);
            shopPopupInstance = null;
            shopPanelRef = null;
            lp.revalidate();
            lp.repaint();
            if (Inventory.instance == null) Game.hideOverlay();
        }
    }

    // ================================================================
    // COUNTDOWN UPDATE
    // ================================================================
    public void updateCountdown() {
        if (countdownLabel == null) return;
        int cd = activeTab.equals("Bait") ? baitCountdown : itemCountdown;
        int m = cd / 60, s = cd % 60;
        countdownLabel.setText(String.format("Restock in: %d:%02d", m, s));
    }

    // ================================================================
    // GRID INIT  — called once, places bg panel at fixed position
    // ================================================================
    public void initGrid(JPanel container) {
        bg = new JPanel(null);
        bg.setOpaque(false);
        bg.setBounds(GRID_X, GRID_Y, GRID_W, GRID_H);
        container.add(bg);
    }

    // ================================================================
    // REFRESH STOCK  — redraws slots for the active tab
    // ================================================================
    public void refreshStock() {
        if (bg == null) return;
        bg.removeAll();

        java.util.Map<String, Integer> currentStock =
            activeTab.equals("Bait") ? baitStock : itemStock;

        int col = 0, row = 0, i = 0;
        for (java.util.Map.Entry<String, Integer> entry : currentStock.entrySet()) {
            if (i >= COLS * ROWS) break;
            int sx = col * (SLOT_SIZE + SLOT_GAP);
            int sy = row * (SLOT_SIZE + SLOT_GAP);
            JPanel slot = makeItemSlot(entry.getKey(), entry.getValue(), currentStock);
            slot.setBounds(sx, sy, SLOT_SIZE, SLOT_SIZE);
            bg.add(slot);
            col++; if (col >= COLS) { col = 0; row++; }
            i++;
        }

        // Fill remaining with empty slots
        while (i < COLS * ROWS) {
            int sx = col * (SLOT_SIZE + SLOT_GAP);
            int sy = row * (SLOT_SIZE + SLOT_GAP);
            JPanel slot = makeEmptySlot();
            slot.setBounds(sx, sy, SLOT_SIZE, SLOT_SIZE);
            bg.add(slot);
            col++; if (col >= COLS) { col = 0; row++; }
            i++;
        }

        bg.revalidate();
        bg.repaint();
        updateCountdown();
    }

    // ================================================================
    // SLOT BUILDERS
    // ================================================================
    private JPanel makeItemSlot(String itemName, int stockCount,
                                java.util.Map<String, Integer> currentStock) {
        JPanel slot = new JPanel(new BorderLayout()) {
            private java.net.URL slotUrl = getClass().getResource("/images/sari sari store slot.png");
            private Image slotImg = (slotUrl != null) ? new ImageIcon(slotUrl).getImage() : null;
            @Override
            protected void paintComponent(Graphics g) {
                if (slotImg != null) g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(new Color(90, 70, 45)); g.fillRect(0, 0, getWidth(), getHeight()); }
                super.paintComponent(g);
            }
        };
        slot.setOpaque(false);
        slot.setBorder(BorderFactory.createLineBorder(new Color(55, 35, 12), 2));

        // Item image
        java.net.URL imgUrl = getClass().getResource(
            "/images/" + Character.toLowerCase(itemName.charAt(0)) + itemName.substring(1).replace(" ", "") + ".png");
        if (imgUrl != null) {
            Image scaled = new ImageIcon(imgUrl).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            JLabel icon = new JLabel(new ImageIcon(scaled));
            icon.setHorizontalAlignment(SwingConstants.CENTER);
            slot.add(icon, BorderLayout.CENTER);
        } else {
            JLabel lbl = new JLabel("<html><center>" + itemName + "</center></html>", SwingConstants.CENTER);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Arial", Font.PLAIN, 9));
            slot.add(lbl, BorderLayout.CENTER);
        }

        boolean isBambooLocked = itemName.equals("Bamboo Rod") && PlayerData.level < 5;
        boolean isHotdogLocked = itemName.equals("Hotdog Rod") && PlayerData.level < 10;
        if (isBambooLocked || isHotdogLocked) {
            String reqText = isBambooLocked ? "Lv 5" : "Lv 10";
            JLabel lockLabel = new JLabel(reqText, SwingConstants.CENTER);
            lockLabel.setFont(new Font("Arial", Font.BOLD, 14));
            lockLabel.setForeground(Color.WHITE);
            slot.add(lockLabel, BorderLayout.SOUTH);
            Graphics2D g2 = (Graphics2D) slot.getGraphics();
            slot.setBackground(new Color(30, 20, 10));
            slot.setOpaque(true);
            return slot;
        }

        boolean isRod = itemName.endsWith("Rod");
        boolean owned = isRod && Inventory.items.getOrDefault(itemName, 0) > 0;
        if (isRod) {
            int price = Shop.buyPrices.getOrDefault(itemName, Shop.buyItems != null ? 0 : 0);
            if (owned) {
                JLabel ownedLabel = new JLabel("Owned", SwingConstants.CENTER);
                ownedLabel.setFont(new Font("Arial", Font.BOLD, 12));
                ownedLabel.setForeground(new Color(100, 220, 100));
                slot.add(ownedLabel, BorderLayout.SOUTH);
            } else {
                int rodPrice = itemName.equals("Default Rod") ? 100 :
                            itemName.equals("Bamboo Rod")  ? 2000 : 5000;
                JLabel priceLbl = new JLabel("₱" + rodPrice, SwingConstants.CENTER);
                priceLbl.setFont(new Font("Arial", Font.BOLD, 10));
                priceLbl.setForeground(new Color(255, 215, 0));
                slot.add(priceLbl, BorderLayout.SOUTH);
                slot.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (PlayerData.getMoney() >= rodPrice) {
                            AudioPlayer.playSound("buySound.wav");
                            PlayerData.addMoney(-rodPrice);
                            Inventory.addItem(itemName);
                            refreshStock();
                        } else {
                            AudioPlayer.playSound("error.wav");
                            PlayerData.flashMoneyLabel();
                        }
                    }
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        slot.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        slot.setBorder(BorderFactory.createLineBorder(new Color(55, 35, 12), 2));
                    }
                });
            }
            return slot;
        }

        if (stockCount > 0) {
            JLabel countLbl = new JLabel("x" + stockCount);
            countLbl.setFont(new Font("Arial", Font.BOLD, 11));
            countLbl.setForeground(Color.WHITE);
            countLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            slot.add(countLbl, BorderLayout.NORTH);

            int price = Shop.buyPrices.getOrDefault(itemName, 0);
            JLabel priceLbl = new JLabel("₱" + price);
            priceLbl.setFont(new Font("Arial", Font.BOLD, 10));
            priceLbl.setForeground(new Color(255, 215, 0));
            priceLbl.setHorizontalAlignment(SwingConstants.CENTER);
            slot.add(priceLbl, BorderLayout.SOUTH);

            slot.addMouseListener(new java.awt.event.MouseAdapter() {
               @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    int p = Shop.buyPrices.getOrDefault(itemName, 0);
                    if (stockCount > 0 && PlayerData.getMoney() >= p) {
                        AudioPlayer.playSound("buySound.wav");
                        PlayerData.addMoney(-p);
                        Inventory.addItem(itemName);
                        currentStock.put(itemName, stockCount - 1);
                        if (activeTab.equals("Items")) {
                            ownedItems.add(itemName);
                        }
                        refreshStock();
                    } else if (PlayerData.getMoney() < p) {
                        AudioPlayer.playSound("error.wav");
                        PlayerData.flashMoneyLabel();
                    }
                }
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    slot.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    slot.setBorder(BorderFactory.createLineBorder(new Color(55, 35, 12), 2));
                }
            });
        } else {
            // Show item image dimmed (low opacity) so user can still see what it is
            java.net.URL dimUrl = getClass().getResource(
                "/images/" + Character.toLowerCase(itemName.charAt(0)) + itemName.substring(1).replace(" ", "") + ".png");
            if (dimUrl != null) {
                Image dimScaled = new ImageIcon(dimUrl).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                JLabel dimIcon = new JLabel(new ImageIcon(dimScaled)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                dimIcon.setHorizontalAlignment(SwingConstants.CENTER);
                // Replace the existing center component with the dimmed one
                slot.remove(slot.getComponentCount() > 0 ? slot.getComponent(0) : null);
                slot.add(dimIcon, BorderLayout.CENTER);
            }
            // "OUT" label on top of the dimmed image
            String outText = (activeTab.equals("Items") && ownedItems.contains(itemName))
                ? "Owned" : "OUT OF STOCK";
            Color outColor = outText.equals("Owned")
                ? new Color(100, 220, 100) : new Color(255, 80, 80);
            JLabel out = new JLabel(outText);
            out.setFont(new Font("Arial", Font.BOLD, 10));
            out.setForeground(outColor);
            out.setHorizontalAlignment(SwingConstants.CENTER);
            slot.add(out, BorderLayout.SOUTH);
            // Grey tint over the whole slot
            slot.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        }
        return slot;
    }

    private JPanel makeEmptySlot() {
        JPanel slot = new JPanel() {
            private java.net.URL slotUrl = getClass().getResource("/images/sari sari store slot.png");
            private Image slotImg = (slotUrl != null) ? new ImageIcon(slotUrl).getImage() : null;
            @Override protected void paintComponent(Graphics g) {
                if (slotImg != null) g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(new Color(50, 35, 20)); g.fillRect(0, 0, getWidth(), getHeight()); }
            }
        };
        slot.setOpaque(false);
        slot.setBorder(BorderFactory.createLineBorder(new Color(40, 25, 8), 2));
        return slot;
    }

    // ================================================================
    // TAB BUTTON STYLE HELPER
    // ================================================================
    private void updateTabStyles() {
        if (baitTabBtn == null || itemTabBtn == null) return;
        // Active tab: bright gold bg, bold text
        baitTabBtn.setBackground(activeTab.equals("Bait")
            ? new Color(180, 130, 40) : new Color(80, 55, 25));
        baitTabBtn.setForeground(activeTab.equals("Bait")
            ? new Color(255, 240, 180) : new Color(180, 150, 90));

        itemTabBtn.setBackground(activeTab.equals("Items")
            ? new Color(180, 130, 40) : new Color(80, 55, 25));
        itemTabBtn.setForeground(activeTab.equals("Items")
            ? new Color(255, 240, 180) : new Color(180, 150, 90));
    }

    // ================================================================
    // BACKGROUND PANEL  (full-screen store scene)
    // ================================================================
    class BackgroundPanel extends JPanel {
        @Override public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (storeBackgroundGif != null)
                g.drawImage(storeBackgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            if (hovering && hover != null)
                g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // ================================================================
    // SHOP POPUP  (the overlay panel, 845 x 580)
    // ================================================================
    static class ShopPopup extends JPanel {
        private Buy buyRef = new Buy();

        public ShopPopup() {
            setLayout(null);
            setOpaque(false);

            // ---- BACKDROP (draws sari sari store bg.png) ----
            JPanel backdrop = new JPanel(null) {
                private java.net.URL bgUrl = getClass().getResource("/images/sari sari store bg.png");
                private Image bgImg = (bgUrl != null) ? new ImageIcon(bgUrl).getImage() : null;
                @Override protected void paintComponent(Graphics g) {
                    if (bgImg != null) {
                        g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                    } else {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setColor(new Color(80, 55, 30));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                        g2.setColor(new Color(110, 80, 45));
                        g2.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 12, 12);
                        g2.setColor(new Color(50, 30, 10));
                        g2.setStroke(new BasicStroke(3));
                        g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 14, 14);
                    }
                }
            };
            backdrop.setBounds(0, 0, 790, 570);
            add(backdrop);

            // ---- TITLE  (y=14, inside header bar of bg image) ----
            JLabel title = new JLabel("Sari-Sari Store");
            title.setFont(new Font("Arial", Font.BOLD, 20));
            title.setForeground(new Color(255, 220, 100));
            title.setBounds(18, 12, 320, 32);
            backdrop.add(title);

            // ---- CLOSE BUTTON ----
            JButton closeBtn = new JButton("X");
            closeBtn.setFont(new Font("Arial", Font.BOLD, 15));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setBorderPainted(false);
            closeBtn.setContentAreaFilled(false);
            closeBtn.setFocusPainted(false);
            Buttons.addClickSound(closeBtn);
            closeBtn.addActionListener(e -> Buy.closeShopPopup());
            closeBtn.setBounds(748, 10, 36, 32);
            backdrop.add(closeBtn);

            // ---- COUNTDOWN ----
            buyRef.countdownLabel = new JLabel("Restock in: 5:00");
            buyRef.countdownLabel.setFont(new Font("Arial", Font.BOLD, 13));
            buyRef.countdownLabel.setForeground(new Color(180, 255, 180));
            buyRef.updateCountdown();
            buyRef.countdownLabel.setBounds(560, 14, 220, 26);
            backdrop.add(buyRef.countdownLabel);

            // ---- MONEY ----
            JLabel moneyLabel = PlayerData.createMoneyLabel();
            moneyLabel.setFont(new Font("Arial", Font.BOLD, 15));
            moneyLabel.setForeground(new Color(255, 215, 0));
            moneyLabel.setBounds(18, 56, 260, 26);
            backdrop.add(moneyLabel);

            // ---- TAB BUTTONS  (Bait | Items) ----
            // Centered below money row, above the grid
            int tabW = 160, tabH = 34, tabY = 108;
            int totalTabsW = tabW * 2 + 10; // 10px gap between tabs
            int tabStartX = (845 - totalTabsW) / 2;

            buyRef.baitTabBtn = makeTabButton("Bait");
            buyRef.baitTabBtn.setBounds(tabStartX, tabY, tabW, tabH);
            buyRef.baitTabBtn.addActionListener(e -> {
                buyRef.activeTab = "Bait";
                buyRef.updateTabStyles();
                buyRef.refreshStock();
            });
            backdrop.add(buyRef.baitTabBtn);

            buyRef.itemTabBtn = makeTabButton("Items");
            buyRef.itemTabBtn.setBounds(tabStartX + tabW + 10, tabY, tabW, tabH);
            buyRef.itemTabBtn.addActionListener(e -> {
                buyRef.activeTab = "Items";
                buyRef.updateTabStyles();
                buyRef.refreshStock();
            });
            backdrop.add(buyRef.itemTabBtn);

            buyRef.updateTabStyles();

            // ---- GRID (fixed position: x=202, y=155, 440x260) ----
            buyRef.initGrid(backdrop);
            buyRef.refreshStock();
        }

        private JButton makeTabButton(String label) {
            JButton btn = new JButton(label) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(50, 30, 8));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            Buttons.addClickSound(btn);
            return btn;
        }

        public Buy getBuyRef() { return buyRef; }
    }
}