package Pusawan;

import java.awt.*;
import javax.swing.*;

public class FishIndex extends JFrame {

    static FishIndex instance;

    // ===== FISH DATA =====
    private static final String[] FISH_NAMES = {"Perch", "Carp", "Catfish", "Bass"};

    private static final java.util.Map<String, int[]> catchRates = new java.util.LinkedHashMap<>();
    // order: No Bait, Worm, Insect, Fish, Magic
    static {
        catchRates.put("Perch",   new int[]{12, 34, 28, 22, 15});
        catchRates.put("Carp",    new int[]{10, 22, 20, 20, 20});
        catchRates.put("Catfish", new int[]{ 8, 10, 22, 20, 30});
        catchRates.put("Bass",    new int[]{ 6,  5,  9, 24, 35});
    }

    private static final java.util.Map<String, int[]> sellPrices = new java.util.LinkedHashMap<>();
    // order: raw, cut, cooked
    static {
        sellPrices.put("Perch",   new int[]{ 7,  4, 30});
        sellPrices.put("Carp",    new int[]{12,  6, 50});
        sellPrices.put("Catfish", new int[]{15,  8, 60});
        sellPrices.put("Bass",    new int[]{20, 10, 80});
    }

    // ===== CONSTRUCTOR =====
    public FishIndex() {
        setTitle("Fish Index");
        setSize(760, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        setCursor(playerCursor.getCustomCursor());
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 760, 620);
        add(layeredPane);

        // close when clicking outside
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!layeredPane.getBounds().contains(e.getPoint())) dispose();
            }
        });

        // ===== HEADER =====
        JPanel header = new JPanel(null);
        header.setBounds(30, 0, 700, 40);
        header.setOpaque(true);
        header.setBackground(new Color(0x006400));

        JLabel title = new JLabel("Fish Index");
        title.setBounds(10, 0, 300, 40);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);

        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(650, 0, 50, 40);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        Buttons.addClickSound(closeBtn);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn);

        layeredPane.add(header, JLayeredPane.PALETTE_LAYER);

        // ===== CONTENT =====
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(30, 50, 30));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBounds(30, 45, 700, 555);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

        // ===== BAIT HEADER ROW =====
        JPanel baitHeader = new JPanel(new GridLayout(1, 6, 5, 0));
        baitHeader.setBackground(new Color(20, 40, 20));
        baitHeader.setMaximumSize(new Dimension(700, 40));
        baitHeader.setPreferredSize(new Dimension(700, 40));

        for (String label : new String[]{"Fish", "No Bait", "Worm", "Insect", "Fish Bait", "Magic"}) {
            JLabel l = new JLabel(label, SwingConstants.CENTER);
            l.setForeground(new Color(180, 220, 180));
            l.setFont(new Font("Arial", Font.BOLD, 12));
            baitHeader.add(l);
        }
        content.add(baitHeader);

        // ===== FISH ENTRIES =====
        for (String fish : FISH_NAMES) {
            boolean caught = PlayerData.getCaughtCount(fish) > 0;

            JPanel entry = new JPanel(null);
            entry.setBackground(caught ? new Color(50, 80, 50) : new Color(40, 40, 40));
            entry.setMaximumSize(new Dimension(700, 120));
            entry.setPreferredSize(new Dimension(700, 120));
            entry.setBorder(BorderFactory.createLineBorder(new Color(80, 120, 80), 1));

            // fish image
            if (caught) {
                java.net.URL imgUrl = getClass().getResource("/images/" + fish.toLowerCase() + ".png");
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    JLabel img = new JLabel(new ImageIcon(scaled));
                    img.setBounds(10, 30, 60, 60);
                    entry.add(img);
                }
            } else {
                JLabel locked = new JLabel("???", SwingConstants.CENTER);
                locked.setBounds(10, 30, 60, 60);
                locked.setFont(new Font("Arial", Font.BOLD, 20));
                locked.setForeground(Color.GRAY);
                entry.add(locked);
            }

            // fish name
            JLabel name = new JLabel(caught ? fish : "???");
            name.setBounds(80, 5, 150, 30);
            name.setFont(new Font("Arial", Font.BOLD, 14));
            name.setForeground(caught ? Color.WHITE : Color.GRAY);
            entry.add(name);

            // total caught
            JLabel caughtLabel = new JLabel("Caught: " + PlayerData.getCaughtCount(fish));
            caughtLabel.setBounds(80, 30, 150, 20);
            caughtLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            caughtLabel.setForeground(caught ? new Color(180, 220, 180) : Color.GRAY);
            entry.add(caughtLabel);

            // sell prices row
            if (caught) {
                int[] prices = sellPrices.get(fish);
                JLabel priceLabel = new JLabel(
                    "Raw: ₱" + prices[0] + "  Cut: ₱" + prices[1] + "  Cooked: ₱" + prices[2]
                );
                priceLabel.setBounds(80, 55, 300, 20);
                priceLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                priceLabel.setForeground(Color.YELLOW);
                entry.add(priceLabel);
            }

            // catch rates per bait
            if (caught) {
                int[] rates = catchRates.get(fish);
                String[] baitNames = {"No Bait", "Worm", "Insect", "Fish", "Magic"};
                JPanel ratesPanel = new JPanel(new GridLayout(1, 5, 5, 0));
                ratesPanel.setOpaque(false);
                ratesPanel.setBounds(80, 80, 580, 25);
                for (int j = 0; j < baitNames.length; j++) {
                    JLabel r = new JLabel(baitNames[j] + ": " + rates[j] + "%", SwingConstants.CENTER);
                    r.setFont(new Font("Arial", Font.PLAIN, 10));
                    r.setForeground(new Color(180, 220, 180));
                    ratesPanel.add(r);
                }
                entry.add(ratesPanel);
            }

            content.add(entry);
            content.add(Box.createVerticalStrut(5));
        }

        Game.showOverlay();
        setVisible(true);
    }

    // ===== DISPOSE =====
    @Override
    public void dispose() {
        super.dispose();
        instance = null;
        Game.hideOverlayIfNoModals();
    }

    // ===== TOGGLE =====
    public static void toggle() {
        Buttons.closeAllDropdowns();
        if (instance != null && instance.isDisplayable()) {
            instance.dispose();
            return;
        }
        instance = new FishIndex();
    }
}