    package Pusawan;

    import java.awt.*;
    import javax.swing.*;

    public class Inventory extends JFrame {

        private JPanel bg;

        static Inventory instance;
        private String mode = "inventory";
        private boolean isItemsTab = false;

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
            if (mode.equals("bait")) isItemsTab = true;

            setTitle("Inventory");
            setSize(760, 620);
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
            layeredPane.setBounds(0, 0, 760, 620);
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (!layeredPane.getBounds().contains(e.getPoint())) {
                        dispose();
                    }
                }
            });
            add(layeredPane);

            // ================= HEADER =================
            JPanel header = new JPanel(null);
            header.setBounds(30, 0, 700, 40);
            header.setOpaque(true);
            header.setBackground(new Color(0xbe0000));

            JLabel title = new JLabel(new ImageIcon(getClass().getResource("/images/inventoryTitle.png")));
            title.setBounds(0, 0, 700, 40);
            header.add(title);

            JButton closeBtn = new JButton("X");
            closeBtn.setBounds(650, 0, 50, 40);
            closeBtn.setBorderPainted(false);
            closeBtn.setContentAreaFilled(false);
            closeBtn.setFocusPainted(false);
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
            closeBtn.addActionListener(e -> dispose());
            header.add(closeBtn);

            layeredPane.add(header, JLayeredPane.PALETTE_LAYER);

            // ================= COINS =================
            JPanel coinsPanel = new JPanel(null);
            coinsPanel.setBounds(30, 45, 700, 45);
            coinsPanel.setBackground(new Color(150, 120, 60));

            JLabel coinsLabel = PlayerData.createMoneyLabel();
            coinsLabel.setBounds(10, 8, 300, 30);
            coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
            coinsLabel.setForeground(Color.WHITE);
            coinsPanel.add(coinsLabel);

            layeredPane.add(coinsPanel, JLayeredPane.DEFAULT_LAYER);

            // ================= TABS =================
            JPanel tabPanel = new JPanel(null);
            tabPanel.setBounds(30, 95, 700, 40);
            tabPanel.setBackground(new Color(40, 30, 20));

            JButton fishTab = new JButton("Fish");
            fishTab.setBounds(0, 0, 175, 40);
            fishTab.setFont(new Font("Arial", Font.BOLD, 14));
            fishTab.setBackground(new Color(90, 70, 45));
            fishTab.setForeground(Color.WHITE);
            fishTab.setFocusPainted(false);
            fishTab.setBorderPainted(false);

            JButton itemsTab = new JButton("Items");
            itemsTab.setBounds(175, 0, 175, 40);
            itemsTab.setFont(new Font("Arial", Font.BOLD, 14));
            itemsTab.setBackground(new Color(50, 40, 30));
            itemsTab.setForeground(new Color(180, 160, 120));
            itemsTab.setFocusPainted(false);
            itemsTab.setBorderPainted(false);

            boolean lockedToFish = mode.equals("cut") || mode.equals("cook");
            if (lockedToFish) {
                itemsTab.setEnabled(false);
                itemsTab.setForeground(new Color(80, 70, 60));
            }

            fishTab.addActionListener(e -> {
                if (!isItemsTab) return;
                isItemsTab = false;
                fishTab.setBackground(new Color(90, 70, 45));
                fishTab.setForeground(Color.WHITE);
                itemsTab.setBackground(new Color(50, 40, 30));
                itemsTab.setForeground(new Color(180, 160, 120));
                refreshItems();
            });

            itemsTab.addActionListener(e -> {
                if (isItemsTab) return;
                isItemsTab = true;
                itemsTab.setBackground(new Color(90, 70, 45));
                itemsTab.setForeground(Color.WHITE);
                fishTab.setBackground(new Color(50, 40, 30));
                fishTab.setForeground(new Color(180, 160, 120));
                refreshItems();
            });

            tabPanel.add(fishTab);
            tabPanel.add(itemsTab);
            layeredPane.add(tabPanel, JLayeredPane.DEFAULT_LAYER);

            // ================= GRID =================
            bg = new JPanel();
            bg.setBackground(new Color(186, 160, 84));
            bg.setBounds(30, 140, 700, 440);
            bg.setLayout(new GridLayout(4, 5, 5, 5));
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
                if (!mode.equals("bait")) {
                    if (isItemsTab && isFish) continue;
                    if (!isItemsTab && !isFish) continue;
                }

                // mode filter
                if (mode.equals("cut") && !FISH_NAMES.contains(itemName)) continue;
                if (mode.equals("cook") && !itemName.startsWith("Cut ")) continue;
                if (mode.equals("bait") && !itemName.endsWith("Bait")) continue;


                JPanel slot = new JPanel(new BorderLayout());
                slot.setBackground(new Color(200, 180, 120));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

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
                            new javax.swing.Timer(3000, ev -> {
                                Inventory.addItem(cooked);
                                ((javax.swing.Timer) ev.getSource()).stop();
                            }).start();
                            dispose();
                            instance = null;
                        }
                        if (mode.equals("bait")) {
                            Fishing.setSelectedBait(itemName);
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
                JPanel slot = new JPanel();
                slot.setBackground(new Color(200, 180, 120));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
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
            Game.hideOverlayIfNoModals();
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
            if (instance == null || !instance.isDisplayable()) {
                instance = new Inventory("inventory");
                instance.refreshItems();
                Game.showOverlay();
                Buttons.updateInventoryIcon();
            } else {
                instance.dispose();
                instance = null;
                Game.hideOverlayIfNoModals();
                Buttons.updateInventoryIcon();
            }
        }

        // ================= TOGGLE WITH MODE =================
        public static void toggleWithMode(String mode) {
            if (instance == null || !instance.isDisplayable()) {
                instance = new Inventory(mode);
                instance.refreshItems();
                Game.showOverlay();
                Buttons.updateInventoryIcon();
            } else {
                instance.dispose();
                instance = null;
                Game.hideOverlayIfNoModals();
                Buttons.updateInventoryIcon();
            }
        }
    }