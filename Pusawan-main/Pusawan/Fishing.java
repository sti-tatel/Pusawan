package Pusawan;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;

public class Fishing extends JPanel {

    private boolean fishingInProgress = false;
    private String lastCaughtFish = null;
    private boolean showFishResult = false;

    static String selectedBait = "No Bait";

    // minigame
    private float fishIndicator = 0f;
    private float indicatorDir = 1f;
    private float catchProgress = 0f;
    private static final float GREEN_ZONE_WIDTH = 0.9f;
    private static final float INDICATOR_SPEED = 0.005f;
    private static final int BAR_X = 375;
    private static final int BAR_Y = 600;
    private static final int BAR_W = 600;
    private static final int BAR_H = 40;
    private static final int CATCH_Y = 660;
    private static final int CATCH_H = 30;

    private JButton menuButton;
    private JButton inventoryButton;
    private JButton cancelButton;

    public static void setSelectedBait(String bait) {
        selectedBait = bait;
    }

    public Fishing() {
        new javax.swing.Timer(16, e -> {
            if (!isShowing()) return;
            if (fishingInProgress) {
                fishIndicator += indicatorDir * INDICATOR_SPEED;
                if (fishIndicator >= 1f) { fishIndicator = 1f; indicatorDir = -1f; }
                if (fishIndicator <= 0f) { fishIndicator = 0f; indicatorDir = 1f; }
            }
            repaint();
        }).start();

        BackgroundPanel background2 = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(background2, BorderLayout.CENTER);
        background2.setLayout(null);

        menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        background2.add(menuButton);

        inventoryButton = Buttons.toInventory();
        inventoryButton.setBounds(20, 505, 100, 100);
        background2.add(inventoryButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(20, 460, 100, 40);
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> {
            fishingInProgress = false;
            catchProgress = 0f;
            fishIndicator = 0f;
            indicatorDir = 1f;
            menuButton.setVisible(true);
            inventoryButton.setVisible(true);
            cancelButton.setVisible(false);
            repaint();
        });
        background2.add(cancelButton);

        cancelButton.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "none");
        cancelButton.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "none");
        menuButton.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "none");
        menuButton.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "none");
    }

    class BackgroundPanel extends JPanel {

        private boolean pierHovered = false;
        private Rectangle pierHitbox = new Rectangle(598, 432, 153, 280);
        private ImageIcon pondbackgroundImage;
        private ImageIcon pondHoverImage;
        private ImageIcon fishingGif;

        public BackgroundPanel() {
            pondbackgroundImage = new ImageIcon(getClass().getResource("/images/pond.gif"));
            pondHoverImage = new ImageIcon(getClass().getResource("/images/pondSelected.png"));
            fishingGif = new ImageIcon(getClass().getResource("/images/fishing.gif"));
            pondbackgroundImage.setImageObserver(this);
            fishingGif.setImageObserver(this);

            // space bar for minigame, disabled on menu button
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "fish");
            getActionMap().put("fish", new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (fishingInProgress) handleMinigameInput();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (!fishingInProgress) {
                        boolean over = pierHitbox.contains(e.getPoint());
                        if (over != pierHovered) {
                            pierHovered = over;
                            repaint();
                        }
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (fishingInProgress) {
                        handleMinigameInput();
                        return;
                    }

                    Rectangle baitBar = new Rectangle(10, 460, 180, 35);
                    if (baitBar.contains(e.getPoint())) {
                        Inventory.toggleWithMode("bait");
                        return;
                    }

                    if (pierHitbox.contains(e.getPoint())) {
                        startFishingAnimation();
                        return;
                    }

                    if (Inventory.instance != null) {
                        Inventory.instance.dispose();
                        Inventory.instance = null;
                    }
                    if (Shop.instance != null) {
                        Shop.instance.dispose();
                        Shop.instance = null;
                    }
                }
            });
        }

        private void handleMinigameInput() {
            float greenLeft = fishIndicator * (1f - GREEN_ZONE_WIDTH);
            float greenRight = greenLeft + GREEN_ZONE_WIDTH;
            boolean inGreen = greenLeft <= 0.5f && greenRight >= 0.5f;
            if (inGreen) {
                catchProgress = Math.min(1f, catchProgress + 0.2f);
                if (catchProgress >= 1f) {
                    menuButton.setVisible(true);
                    inventoryButton.setVisible(true);
                    cancelButton.setVisible(false);
                    fishingInProgress = false;
                    showFishResult = true;
                    Inventory.addItem(lastCaughtFish);
                    if (!selectedBait.equals("No Bait")) {
                        Inventory.removeItem(selectedBait);
                        if (Inventory.items.getOrDefault(selectedBait, 0) <= 0)
                            selectedBait = "No Bait";
                    }
                    new Timer(2000, ev -> {
                        showFishResult = false;
                        repaint();
                        ((Timer) ev.getSource()).stop();
                    }).start();
                }
            } else {
                catchProgress = Math.max(0f, catchProgress - 0.15f);
            }
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (fishingInProgress) {
                g.drawImage(fishingGif.getImage(), 0, 0, getWidth(), getHeight(), this);

                // red background bar
                g.setColor(new Color(80, 20, 20));
                g.fillRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 10, 10);

                // moving green zone
                int greenW = (int)(GREEN_ZONE_WIDTH * BAR_W);
                int greenX = BAR_X + (int)(fishIndicator * (BAR_W - greenW));
                g.setColor(new Color(0, 200, 0));
                g.fillRect(greenX, BAR_Y, greenW, BAR_H);

                // static white line at center
                int centerX = BAR_X + BAR_W / 2;
                g.setColor(Color.WHITE);
                g.fillRect(centerX - 3, BAR_Y - 5, 6, BAR_H + 10);

                // bar border
                g.setColor(Color.BLACK);
                g.drawRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 10, 10);

                // catch progress background
                g.setColor(new Color(20, 20, 80));
                g.fillRoundRect(BAR_X, CATCH_Y, BAR_W, CATCH_H, 10, 10);

                // catch progress fill
                int fillW = (int)(catchProgress * BAR_W);
                g.setColor(new Color(50, 150, 255));
                if (fillW > 0) g.fillRoundRect(BAR_X, CATCH_Y, fillW, CATCH_H, 10, 10);

                // catch progress border
                g.setColor(Color.BLACK);
                g.drawRoundRect(BAR_X, CATCH_Y, BAR_W, CATCH_H, 10, 10);

                // instruction text
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString("Click when the green zone is on the line!", BAR_X, BAR_Y - 15);

            } else {
                g.drawImage(pondbackgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                if (pierHovered && pondHoverImage != null) {
                    g.drawImage(pondHoverImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }

            // fish result
            if (showFishResult && lastCaughtFish != null) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, getHeight()/2 - 50, getWidth(), 100);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                boolean isJunk = lastCaughtFish.equals("Sandal") || lastCaughtFish.equals("Shoe") || lastCaughtFish.equals("Plastic Wrapper");
                String text = (isJunk ? "You found: " : "You caught: ") + lastCaughtFish + "! (" + getChance(lastCaughtFish) + "%)";
                int textWidth = g.getFontMetrics().stringWidth(text);
                g.drawString(text, (getWidth() - textWidth)/2, getHeight()/2 + 10);
            }

            // bait bar
            String baitDisplay = selectedBait.equals("No Bait")
                ? "Bait: None"
                : "Bait: " + selectedBait + " x" + Inventory.items.getOrDefault(selectedBait, 0);
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(10, 460, 180, 35, 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.drawString(baitDisplay, 20, 483);
        }

        private void startFishingAnimation() {
            menuButton.setVisible(false);
            inventoryButton.setVisible(false);
            cancelButton.setVisible(true);
            showFishResult = false;
            lastCaughtFish = null;
            fishingInProgress = true;
            catchProgress = 0f;
            fishIndicator = 0f;
            indicatorDir = 1f;
            lastCaughtFish = pickCatch();
            repaint();
        }

        private String pickCatch() {
            int roll = (int)(Math.random() * 100);
            switch (selectedBait) {
                case "Worm Bait":
                    if (roll < 34) return "Perch";                  // 34%
                    else if (roll < 56) return "Carp";              // 22%
                    else if (roll < 66) return "Catfish";           // 10%
                    else if (roll < 71) return "Bass";              // 5%
                    else if (roll < 87) return "Plastic Wrapper";   // 16%
                    else if (roll < 95) return "Sandal";            // 8%
                    else return "Shoe";                             // 5%
                case "Insect Bait":
                    if (roll < 28) return "Perch";                  // 28%
                    else if (roll < 48) return "Carp";              // 20%
                    else if (roll < 70) return "Catfish";           // 22%
                    else if (roll < 79) return "Bass";              // 9%
                    else if (roll < 91) return "Plastic Wrapper";   // 12%
                    else if (roll < 97) return "Sandal";            // 6%
                    else return "Shoe";                             // 3%
                case "Fish Bait":
                    if (roll < 22) return "Perch";                  // 22%
                    else if (roll < 42) return "Carp";              // 20%
                    else if (roll < 62) return "Catfish";           // 20%
                    else if (roll < 86) return "Bass";              // 24%
                    else if (roll < 94) return "Plastic Wrapper";   // 8%
                    else if (roll < 98) return "Sandal";            // 4%
                    else return "Shoe";                             // 2%
                case "Magic Bait":
                    if (roll < 15) return "Perch";                  // 15%
                    else if (roll < 35) return "Carp";              // 20%
                    else if (roll < 65) return "Catfish";           // 30%
                    else return "Bass";                             // 35%
                default: // No Bait
                    if (roll < 12) return "Perch";                  // 12%
                    else if (roll < 22) return "Carp";              // 10%
                    else if (roll < 30) return "Catfish";           // 8%
                    else if (roll < 36) return "Bass";              // 6%
                    else if (roll < 62) return "Plastic Wrapper";   // 26%
                    else if (roll < 81) return "Sandal";            // 19%
                    else return "Shoe";                             // 19%
            }
        }

        private int getChance(String name) {
            switch (selectedBait) {
                case "Worm Bait":
                    switch (name) {
                        case "Perch": return 34;
                        case "Carp": return 22;
                        case "Catfish": return 10;
                        case "Bass": return 5;
                        case "Plastic Wrapper": return 16;
                        case "Sandal": return 8;
                        case "Shoe": return 5;
                        default: return 0;
                    }
                case "Insect Bait":
                    switch (name) {
                        case "Perch": return 28;
                        case "Carp": return 20;
                        case "Catfish": return 22;
                        case "Bass": return 9;
                        case "Plastic Wrapper": return 12;
                        case "Sandal": return 6;
                        case "Shoe": return 3;
                        default: return 0;
                    }
                case "Fish Bait":
                    switch (name) {
                        case "Perch": return 22;
                        case "Carp": return 20;
                        case "Catfish": return 20;
                        case "Bass": return 24;
                        case "Plastic Wrapper": return 8;
                        case "Sandal": return 4;
                        case "Shoe": return 2;
                        default: return 0;
                    }
                case "Magic Bait":
                    switch (name) {
                        case "Perch": return 15;
                        case "Carp": return 20;
                        case "Catfish": return 30;
                        case "Bass": return 35;
                        default: return 0;
                    }
                default: // No Bait
                    switch (name) {
                        case "Perch": return 12;
                        case "Carp": return 10;
                        case "Catfish": return 8;
                        case "Bass": return 6;
                        case "Plastic Wrapper": return 26;
                        case "Sandal": return 19;
                        case "Shoe": return 19;
                        default: return 0;
                    }
            }
        }
    }
}