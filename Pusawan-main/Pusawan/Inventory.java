    package Pusawan;

    import java.awt.*;
    import javax.swing.*;

    public class Inventory extends JFrame {

        private JPanel bg;

        static Inventory instance;
        private String mode = "inventory";
        private boolean isItemsTab = false;
        private boolean isBaitTab = false;
        

        private static final java.util.Set<String> FISH_NAMES = new java.util.HashSet<>(
            java.util.Arrays.asList("Carp", "Catfish", "Bass", "Perch")
        );
        
        public static void fillDebug() {
            String[] allItems = {"Perch","Carp","Catfish","Bass","Cut Perch","Cut Carp","Cut Catfish","Cut Bass","Cooked Perch","Cooked Carp","Cooked Catfish","Cooked Bass","Sandal","Shoe","Plastic Wrapper","Worm Bait","Insect Bait","Fish Bait","Magic Bait"};
            for (String item : allItems) items.put(item, 100);
        }

        // ================= SHARED INVENTORY =================
        public static java.util.Map<String, Integer> items = new java.util.LinkedHashMap<>();

        public Inventory(String mode) {

            this.mode = mode;
            if (mode.equals("inventory")) isItemsTab = false;
            if (mode.equals("bait")) isBaitTab = true;

            setTitle("Inventory");
            setSize(780, 553);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setUndecorated(true);
            setResizable(false);
            setLocationRelativeTo(null);
            setBackground(new Color(0, 0, 0, 0));
            setCursor(playerCursor.getCustomCursor());
            setLayout(null);

            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    instance = null;
                    Game.hideOverlayIfNoModals();
                    Buttons.updateInventoryIcon();
                }
            });

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setBounds(0, 0, 780, 553);
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (!layeredPane.getBounds().contains(e.getPoint())) {
                        dispose();
                    }
                }
            });
            add(layeredPane);


            // ================= BACKGROUND =================
            JPanel bgPanel = new JPanel() {
                private Image bgImg = new ImageIcon(getClass().getResource("/images/inventorybg.png")).getImage();
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            bgPanel.setOpaque(true);
            bgPanel.setBounds(0, 0, 780, 553);
            layeredPane.add(bgPanel, Integer.valueOf(-1));

            // ================= HEADER =================
            JPanel header = new JPanel(null);
            header.setBounds(0, 0, 780, 553);
            header.setOpaque(false);

            JButton closeBtn = new JButton("X");
            closeBtn.setBounds(757, 5, 45, 45);
            closeBtn.setBorderPainted(false);
            closeBtn.setContentAreaFilled(false);
            closeBtn.setFocusPainted(false);
            closeBtn.setForeground(Color.RED);
            closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
            closeBtn.addActionListener(e -> dispose());
            header.add(closeBtn);

            layeredPane.add(header, JLayeredPane.PALETTE_LAYER);

            // ================= COINS =================
            // JPanel coinsPanel = new JPanel(null);
            // coinsPanel.setBounds(30, 45, 700, 45);
            // coinsPanel.setBackground(new Color(150, 120, 60));

            // JLabel coinsLabel = PlayerData.createMoneyLabel();
            // coinsLabel.setBounds(10, 8, 300, 30);
            // coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
            // coinsLabel.setForeground(Color.WHITE);
            // coinsPanel.add(coinsLabel);

            // layeredPane.add(coinsPanel, JLayeredPane.DEFAULT_LAYER);

            // ===== TAB LAYOUT =====
            int tx = 42, tabY = 68, tW = 100, tH = 30, tG = 5;

            JButton fishTab = new JButton("Fish");
            fishTab.setBounds(tx, tabY, tW, tH);
            fishTab.setFont(new Font("Arial", Font.BOLD, 14));
            fishTab.setBackground(new Color(90, 70, 45));
            fishTab.setForeground(Color.WHITE);
            fishTab.setFocusPainted(false);
            fishTab.setBorderPainted(false);

            JButton itemsTab = new JButton("Items");
            itemsTab.setBounds(tx + tW + tG, tabY, tW, tH);
            itemsTab.setFont(new Font("Arial", Font.BOLD, 14));
            itemsTab.setBackground(new Color(50, 40, 30));
            itemsTab.setForeground(new Color(180, 160, 120));
            itemsTab.setFocusPainted(false);
            itemsTab.setBorderPainted(false);

            JButton baitTab = new JButton("Bait");
            baitTab.setBounds(tx + (tW + tG) * 2, tabY, tW, tH);
            baitTab.setFont(new Font("Arial", Font.BOLD, 14));
            baitTab.setBackground(new Color(50, 40, 30));
            baitTab.setForeground(new Color(180, 160, 120));
            baitTab.setFocusPainted(false);
            baitTab.setBorderPainted(false);

            baitTab.addActionListener(e -> {
                if (isBaitTab) return;
                isBaitTab = true;
                isItemsTab = false;
                baitTab.setBackground(new Color(90, 70, 45)); baitTab.setForeground(Color.WHITE);
                fishTab.setBackground(new Color(50, 40, 30)); fishTab.setForeground(new Color(180, 160, 120));
                itemsTab.setBackground(new Color(50, 40, 30)); itemsTab.setForeground(new Color(180, 160, 120));
                refreshItems();
            });

            if (mode.equals("bait")) {
                baitTab.setBackground(new Color(90, 70, 45));
                baitTab.setForeground(Color.WHITE);
                fishTab.setBackground(new Color(50, 40, 30));
                fishTab.setForeground(new Color(180, 160, 120));
            }
            
           
            boolean lockedToFish = mode.equals("cut") || mode.equals("cook");
            if (lockedToFish) {
                itemsTab.setEnabled(false);
                itemsTab.setForeground(new Color(80, 70, 60));
                baitTab.setEnabled(false);
                baitTab.setForeground(new Color(80, 70, 60));
            }
            if (mode.equals("bait")) {
                fishTab.setEnabled(false);
                fishTab.setForeground(new Color(80, 70, 60));
                itemsTab.setEnabled(false);
                itemsTab.setForeground(new Color(80, 70, 60));
            }


            fishTab.addActionListener(e -> {
                if (!isItemsTab && !isBaitTab) return;
                isItemsTab = false;
                isBaitTab = false;
                fishTab.setBackground(new Color(90, 70, 45)); fishTab.setForeground(Color.WHITE);
                itemsTab.setBackground(new Color(50, 40, 30)); itemsTab.setForeground(new Color(180, 160, 120));
                baitTab.setBackground(new Color(50, 40, 30)); baitTab.setForeground(new Color(180, 160, 120));
                refreshItems();
            });

            itemsTab.addActionListener(e -> {
                if (isItemsTab) return;
                isItemsTab = true;
                isBaitTab = false;
                itemsTab.setBackground(new Color(90, 70, 45)); itemsTab.setForeground(Color.WHITE);
                fishTab.setBackground(new Color(50, 40, 30)); fishTab.setForeground(new Color(180, 160, 120));
                baitTab.setBackground(new Color(50, 40, 30)); baitTab.setForeground(new Color(180, 160, 120));
                refreshItems();
            });

            JPanel tabPanel = new JPanel(null);
            tabPanel.setBounds(30, 60, 380, 35);
            tabPanel.setOpaque(false);

            tabPanel.add(fishTab);
            tabPanel.add(baitTab);
            tabPanel.add(itemsTab);
            layeredPane.add(tabPanel, JLayeredPane.DEFAULT_LAYER);

            // ================= GRID =================
            bg = new JPanel();
            bg.setBounds(33, 100, 714, 427);
            bg.setOpaque(false);
            bg.setBackground(new Color(0, 0, 0, 0));
            bg.setLayout(new GridLayout(4, 5, 2, 2));
            bg.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            layeredPane.add(bg, JLayeredPane.DEFAULT_LAYER);

            
            setVisible(true);
        }

        // ================= REFRESH =================
        private void refreshItems() {
            bg.removeAll();
            int i = 0;

            for (java.util.Map.Entry<String, Integer> entry : items.entrySet()) {
                if (i >= 20) break;

                final String itemName = entry.getKey();
                int count = entry.getValue();

                boolean isFish = FISH_NAMES.contains(itemName)
                    || itemName.startsWith("Cut ")
                    || itemName.startsWith("Cooked ");

                // tab filter
                if (mode.equals("bait")) {
                    if (!itemName.endsWith("Bait")) continue;
                } else if (mode.equals("cut")) {
                    if (!FISH_NAMES.contains(itemName)) continue;
                } else if (mode.equals("cook")) {
                    if (!itemName.startsWith("Cut ")) continue;
                } else {
                    if (isBaitTab && !itemName.endsWith("Bait")) continue;
                    if (isItemsTab && (isFish || itemName.endsWith("Bait"))) continue;
                    if (!isItemsTab && !isBaitTab && (!isFish || itemName.endsWith("Bait"))) continue;
                }


                JPanel slot = new JPanel(new BorderLayout()) {
                    private Image slotImg = new ImageIcon(getClass().getResource("/images/slotbg.png")).getImage();
                    @Override
                    protected void paintComponent(Graphics g) {
                        g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                    }
                };
                slot.setOpaque(false);
                slot.setBorder(null);

                // ================= MOUSE EVENTS =================
                slot.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        slot.setBackground(Color.WHITE);
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        slot.setBackground(new Color(200, 180, 120));
                    }
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        slot.setBackground(Color.YELLOW);

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
                            if (itemName.equals(PlayerData.equippedRod)) PlayerData.equippedRod = null;
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
                            dispose();
                            instance = null;
                        }
                        if (mode.equals("cook")) {
                            Inventory.removeItem(itemName);
                            String cooked = "Cooked " + itemName.replace("Cut ", "");
                            Cooking.playCookGif(itemName);
                            Inventory.addItem(cooked);
                            dispose();
                            instance = null;
                        }
                        if (mode.equals("bait")) {
                            if (Fishing.selectedBait.equals(itemName)) {
                                Fishing.setSelectedBait("No Bait");
                            } else {
                                Fishing.setSelectedBait(itemName);
                            }
                            dispose();
                            instance = null;
                        }
                    }
                });

                // ================= ICON =================
                String imagePath;
                if (itemName.startsWith("Cut ")) {
                    imagePath = "/images/cut_" + itemName.replace("Cut ", "").toLowerCase() + ".png";
                } else if (itemName.startsWith("Cooked ")) {
                    imagePath = "/images/cooked_" + itemName.replace("Cooked ", "").toLowerCase() + ".png";
                } else if (itemName.endsWith("Bait")) {
                    switch (itemName) {
                        case "Worm Bait":
                            imagePath = "/images/wormBait.png";
                            break;
                        case "Insect Bait":
                            imagePath = "/images/insectBait.png";
                            break;
                        case "Fish Bait":
                            imagePath = "/images/fishBait.png";
                            break;
                        case "Magic Bait":
                            imagePath = "/images/magicBait.png";
                            break;
                        default:
                            imagePath = "/images/" + itemName.replace(" ", "") + ".png";
                            break;
                    }
                } else {
                    imagePath = "/images/" + itemName.toLowerCase().replace(" ", "") + ".png";
                }

                java.net.URL imgUrl = getClass().getResource(imagePath);
                if (imgUrl == null) {
                    // try alternate naming patterns for bait files
                    imgUrl = getClass().getResource("/images/" + itemName.replace(" ", "") + ".png");
                }
                if (imgUrl == null && itemName.endsWith("Bait")) {
                    imgUrl = getClass().getResource("/images/wormBait.png");
                }

                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    JLabel iconLabel = new JLabel(itemName, new ImageIcon(scaled), SwingConstants.CENTER);
                    iconLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                    iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    slot.add(iconLabel, BorderLayout.CENTER);
                } else {
                    slot.add(new JLabel(itemName, SwingConstants.CENTER), BorderLayout.CENTER);
                }

                // ================= COUNT =================
                if (itemName.equals(PlayerData.equippedRod)) {
                    JLabel equippedLabel = new JLabel("Equipped");
                    equippedLabel.setFont(new Font("Arial", Font.BOLD, 10));
                    equippedLabel.setForeground(Color.GREEN);
                    equippedLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    slot.add(equippedLabel, BorderLayout.NORTH);
                }

                if (count > 1) {
                    JLabel countLabel = new JLabel("x" + count);
                    countLabel.setFont(new Font("Arial", Font.BOLD, 12));
                    countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                    slot.add(countLabel, BorderLayout.SOUTH);
                }

                bg.add(slot);
                i++;
            }

            // fill empty slots
            while (i < 20) {
            JPanel slot = new JPanel() {
                private Image slotImg = new ImageIcon(getClass().getResource("/images/slotbg.png")).getImage();
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            slot.setOpaque(false);
                bg.add(slot);
                i++;
            }

            bg.revalidate();
            bg.repaint();
        }

        // ================= ADD ITEM =================
        public static void addItem(String itemName) {
            items.put(itemName, items.getOrDefault(itemName, 0) + 1);
            if (instance != null) instance.refreshItems();
        }

        @Override
        public void dispose() {
            super.dispose();
            instance = null;
            Buttons.updateInventoryIcon();
            if (Shop.instance == null) Game.hideOverlay();
        }

        // ================= REMOVE ITEM =================
        public static void removeItem(String itemName) {
            if (!items.containsKey(itemName)) return;
            int count = items.get(itemName);
            if (count <= 1) items.remove(itemName);
            else items.put(itemName, count - 1);
            if (instance != null) instance.refreshItems();
        }

        // ================= GET FIRST FISH =================
        public static String getFirstFish() {
            for (String item : items.keySet()) {
                if (items.getOrDefault(item, 0) > 0 && FISH_NAMES.contains(item))
                    return item;
            }
            return null;
        }

        // ================= GET FIRST CUT FISH =================
        public static String getFirstCutFish() {
            for (String item : items.keySet()) {
                if (item.startsWith("Cut ") && items.getOrDefault(item, 0) > 0)
                    return item;
            }
            return null;
        }

        // ================= TOGGLE =================
        public static void toggleInventory() {
            Buttons.closeAllDropdowns();
            if (instance != null && instance.isDisplayable()) {
                instance.dispose();
                return;
            }
            instance = new Inventory("inventory");
            instance.refreshItems();
            Game.showOverlay();
            Buttons.updateInventoryIcon();
        }

        // ================= TOGGLE WITH MODE =================
        public static void toggleWithMode(String mode) {
            Buttons.closeAllDropdowns();
            if (instance != null && instance.isDisplayable()) {
                instance.dispose();
                return;
            }
            instance = new Inventory(mode);
            instance.refreshItems();
            Game.showOverlay();
            Buttons.updateInventoryIcon();
        }
    }