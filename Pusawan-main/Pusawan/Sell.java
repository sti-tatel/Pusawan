package Pusawan;

import java.awt.*;
import javax.swing.*;

public class Sell extends JPanel {

    private ImageIcon sellBg;
    private Image sellSelected;
    private boolean hovering = false;
    private Rectangle sellZone = new Rectangle(180, 65, 830, 585);

    private JPanel grid;
    private JLayeredPane sellPane;
    private String selectedItem = ""; // Tracks which item slot is selected
    private JLabel descName;          // Displays selected item name
    private JTextArea descText;       // Displays item description text
    private JLabel itemDisplayLabel;  // Displays larger image preview

    public Sell() {
        sellBg       = new ImageIcon(getClass().getResource("/images/sell.gif"));
        sellSelected = new ImageIcon(getClass().getResource("/images/sellSelected.png")).getImage();

        new javax.swing.Timer(24, e -> { if (isShowing()) repaint(); }).start();

        setLayout(new BorderLayout());

        BackgroundPanel bgPanel = new BackgroundPanel();
        bgPanel.setLayout(null);
        add(bgPanel, BorderLayout.CENTER);

        // ===== NAV BUTTONS =====
        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        bgPanel.add(menuButton);

        bgPanel.add(Buttons.createInventoryButton());

        JButton arrowLeft = new JButton(new ImageIcon(getClass().getResource("/images/arrowLeft.png")));
        arrowLeft.setBounds(20, 400, 64, 64);
        arrowLeft.setBorderPainted(false);
        arrowLeft.setContentAreaFilled(false);
        arrowLeft.setFocusPainted(false);
        Buttons.addClickSound(arrowLeft);
        arrowLeft.addActionListener(e -> Game.navigate(Game.STORE));
        bgPanel.add(arrowLeft);

        // ===== HOVER + CLICK =====
        bgPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                boolean h = sellZone.contains(e.getPoint()) && !sellPane.isVisible();
                if (h != hovering) { hovering = h; repaint(); }
            }
        });
        bgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (sellPane.isVisible()) {
                    sellPane.setVisible(false);
                    hovering = false;
                    repaint();
                    return;
                }
                if (sellZone.contains(e.getPoint())) {
                    hovering = false;
                    sellPane.setVisible(true);
                    refreshGrid();
                    repaint();
                } else {
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                }
            }
        });

        // ===== MAIN PANEL =====
        sellPane = new JLayeredPane();
        int lx = (1350 - 845) / 2;
        int ly = (750  - 580) / 2;
        sellPane.setBounds(lx, ly, 845, 580);
        sellPane.setVisible(false);
        bgPanel.add(sellPane);

        // Background Image
        JPanel sellBgPanel = new JPanel() {
            private Image bgImg = new ImageIcon(getClass().getResource("/images/sell.png")).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bgImg, 0, 0, 845, 580, this);
            }
        };
        sellBgPanel.setOpaque(true);
        sellBgPanel.setBounds(0, 0, 845, 580);
        sellPane.add(sellBgPanel, Integer.valueOf(-1));

        // Close Button ("X")
        JButton closeBtn = new JButton("");
        closeBtn.setBounds(726, 47, 67, 70);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(false);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.addActionListener(e -> { 
            selectedItem = ""; 
            if (descName != null) descName.setText("");
            if (descText != null) descText.setText("Select an item");
            if (itemDisplayLabel != null) itemDisplayLabel.setIcon(null);
            sellPane.setVisible(false); 
            repaint(); 
        });
        sellPane.add(closeBtn, JLayeredPane.MODAL_LAYER);

        // Grid Display (With Graphic Bounds Clipping Added)
        grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15)) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                // Forces graphic slots to hide outside the scroll boundaries
                g2d.setClip(0, 0, getWidth(), getHeight());
                super.paint(g2d);
                g2d.dispose();
            }
        };
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        grid.setPreferredSize(new Dimension(475, 1200));

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(grid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBounds(75, 135, 500, 390);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        sellPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        // ===== DESCRIPTION AREA (RIGHT-SIDE SIDEBAR) =====
        JPanel descPanel = new JPanel(null);
        descPanel.setBounds(605, 165, 163, 163); 
        descPanel.setOpaque(true);

        // Fits perfectly within the 163 width (leaving a 5px margin on both sides)
        descName = new JLabel("", SwingConstants.CENTER);
        descName.setBounds(605, 170, 163, 30);
        descName.setFont(new Font("Arial", Font.BOLD, 15)); // Adjusted size for compact UI
        descName.setForeground(Color.WHITE);
        descName.setOpaque(true);
        descPanel.add(descName);

        // Fits perfectly within the 163 width (leaving a 10px margin on both sides)
        descText = new JTextArea("Select an item");
        descText.setBounds(615, 210, 143, 300);
        descText.setForeground(Color.WHITE);
        descText.setFont(new Font("Arial", Font.PLAIN, 12)); // Adjusted size for compact UI
        descText.setOpaque(true);
        descText.setEditable(false);
        descText.setLineWrap(true);
        descText.setWrapStyleWord(true);
        descText.setFocusable(false);
        descPanel.add(descText);
        sellPane.add(descPanel, JLayeredPane.DEFAULT_LAYER);

        // ===== LARGER PREVIEW IMAGE CONTAINER =====
        JLayeredPane itemDisplay = new JLayeredPane();
        itemDisplay.setBounds(612, 100, 120, 120);
        itemDisplay.setOpaque(true);
        
        itemDisplayLabel = new JLabel();
        itemDisplayLabel.setBounds(600, 45, 120, 120);
        itemDisplayLabel.setOpaque(false);
        itemDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        itemDisplayLabel.setVerticalAlignment(SwingConstants.CENTER);
        itemDisplay.add(itemDisplayLabel, JLayeredPane.PALETTE_LAYER);
        sellPane.add(itemDisplay, JLayeredPane.PALETTE_LAYER);

        refreshGrid();
    }

    private void refreshGrid() {
        grid.removeAll();
        int i = 0;

        for (java.util.Map.Entry<String, Integer> entry : Inventory.items.entrySet()) {
            if (i >= 40) break;
            String name  = entry.getKey();
            int    count = entry.getValue();

            JPanel slot = new JPanel(new BorderLayout()) {
                private Image slotImg = new ImageIcon(getClass().getResource("/images/sellSlot.png")).getImage();
                private Image selectedImg = new ImageIcon(getClass().getResource("/images/sellSlotSelected.png")).getImage();

                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                    if (name.equals(selectedItem)) {
                        g.drawImage(selectedImg, 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };
            slot.setOpaque(false);
            slot.setPreferredSize(new Dimension(80, 80));
            slot.setBorder(null);

            java.net.URL imgUrl = getClass().getResource(resolveImagePath(name));
            if (imgUrl != null) {
                Image scaled = new ImageIcon(imgUrl).getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                slot.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);
            } else {
                slot.add(new JLabel(name, SwingConstants.CENTER), BorderLayout.CENTER);
            }

            JLabel countLabel = new JLabel("x" + count);
            countLabel.setFont(new Font("Arial", Font.BOLD, 10));
            countLabel.setForeground(Color.WHITE);
            countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            slot.add(countLabel, BorderLayout.NORTH);

            slot.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    slot.setOpaque(true);
                    slot.setBackground(new Color(255, 255, 255, 40));
                    slot.repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    slot.setOpaque(false);
                    slot.repaint();
                }
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedItem = name;
                    
                    descName.setText(name);
                    
                    String desc = Inventory.descriptions.getOrDefault(name, "No description available.");
                    descText.setText(desc);
                    
                    java.net.URL dispUrl = getClass().getResource(resolveImagePath(name));
                    if (dispUrl != null) {
                        Image dispImg = new ImageIcon(dispUrl).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                        itemDisplayLabel.setIcon(new ImageIcon(dispImg));
                    } else {
                        itemDisplayLabel.setIcon(null);
                    }
                    
                    grid.repaint();
                }
            });
            grid.add(slot);
            i++;
        }

        while (i < 20) {
            JPanel slot = new JPanel() {
                private Image slotImg = new ImageIcon(getClass().getResource("/images/sellSlot.png")).getImage();
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            slot.setOpaque(false);
            slot.setPreferredSize(new Dimension(80, 80));
            slot.setBorder(null);
            grid.add(slot);
            i++;
        }

        grid.revalidate();
        grid.repaint();
    }

    private String resolveImagePath(String name) {
        if (name.startsWith("Cooked "))
            return "/images/cooked_" + name.replace("Cooked ", "").toLowerCase() + ".png";
        if (name.startsWith("Cut "))
            return "/images/cut_" + name.replace("Cut ", "").toLowerCase() + ".png";
        if (name.endsWith("Bait"))
            return "/images/" + Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", "") + ".png";
        return "/images/" + name.toLowerCase().replace(" ", "") + ".png";
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(sellBg.getImage(), 0, 0, getWidth(), getHeight(), this);
            if (hovering) g.drawImage(sellSelected, 0, 0, getWidth(), getHeight(), this);
        }
    }
}