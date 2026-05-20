package Pusawan;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;

public class Fishing extends JPanel {

    // ===== STATE =====
    private boolean fishingInProgress = false;
    private boolean showFishResult    = false;
    private String  lastCaughtFish    = null;
    static  String  selectedBait      = "No Bait";

    // ===== MINIGAME =====
    private float fishIndicator  = 0f;
    private float indicatorDir   = 1f;
    private float catchProgress  = 0f;
    private float greenZoneWidth = 0.6f;
    private float indicatorSpeed = 0.005f;

    // ===== BAR LAYOUT =====
    private static final int BAR_X   = 375;
    private static final int BAR_Y   = 600;
    private static final int BAR_W   = 600;
    private static final int BAR_H   = 40;
    private static final int CATCH_Y = 660;
    private static final int CATCH_H = 30;

    // ===== BUTTONS =====
    private JButton menuButton;
    private JButton inventoryButton;
    private JButton cancelButton;

    public static void setSelectedBait(String bait) {
        selectedBait = bait;
    }

    // ===== CONSTRUCTOR =====
    public Fishing() {

        // game loop — updates indicator and repaints
        new javax.swing.Timer(16, e -> {
            if (!isShowing()) return;
            if (fishingInProgress) {
                fishIndicator += indicatorDir * indicatorSpeed;
                if (fishIndicator >= 1f) { fishIndicator = 1f; indicatorDir = -1f; }
                if (fishIndicator <= 0f) { fishIndicator = 0f; indicatorDir =  1f; }
            }
            repaint();
        }).start();

        BackgroundPanel background = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(background, BorderLayout.CENTER);
        background.setLayout(null);

        // ===== BUTTONS SETUP =====
        menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        background.add(menuButton);

        inventoryButton = Buttons.toInventory();
        inventoryButton.setBounds(20, 505, 100, 100);
        background.add(inventoryButton);

        //INDEX BUTTON
        JButton indexButton = new JButton("Index");
        indexButton.setBounds(20, 620, 80, 30);
        indexButton.addActionListener(e -> FishIndex.toggle()); 
        background.add(indexButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(20, 460, 100, 40);
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> cancelFishing());
        background.add(cancelButton);

        // disable spacebar on buttons to avoid conflicts with minigame
        for (JButton b : new JButton[]{cancelButton, menuButton}) {
            b.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "none");
            b.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "none");
        }
    }

    // cancels fishing and resets state
    private void cancelFishing() {
        fishingInProgress = false;
        catchProgress     = 0f;
        fishIndicator     = 0f;
        indicatorDir      = 1f;
        menuButton.setVisible(true);
        inventoryButton.setVisible(true);
        cancelButton.setVisible(false);
        repaint();
    }

    // ===== BACKGROUND PANEL =====
    class BackgroundPanel extends JPanel {

        private boolean   pierHovered = false;
        private Rectangle pierHitbox  = new Rectangle(598, 432, 153, 280);

        private ImageIcon pondBg;
        private ImageIcon pondHover;
        private ImageIcon fishingGif;
        private ImageIcon baitSelectImage;

        public BackgroundPanel() {
            pondBg          = new ImageIcon(getClass().getResource("/images/pond.gif"));
            pondHover       = new ImageIcon(getClass().getResource("/images/pondSelected.png"));
            fishingGif      = new ImageIcon(getClass().getResource("/images/fishing.gif"));
            baitSelectImage = new ImageIcon(getClass().getResource("/images/baitSelect.png"));

            pondBg.setImageObserver(this);
            fishingGif.setImageObserver(this);

            // spacebar triggers minigame input
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "fish");
            getActionMap().put("fish", new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (fishingInProgress) handleMinigameInput();
                }
            });

            // hover highlight on pier
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (!fishingInProgress) {
                        boolean over = pierHitbox.contains(e.getPoint()) && Inventory.instance == null;
                        if (over != pierHovered) { pierHovered = over; repaint(); }
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (fishingInProgress) { handleMinigameInput(); return; }

                    // bait selector click
                    if (new Rectangle(5, 625, 180, 70).contains(e.getPoint())) {
                        Inventory.toggleWithMode("bait");
                        return;
                    }

                    // pier click — start fishing
                    if (pierHitbox.contains(e.getPoint())) {
                        startFishing();
                        return;
                    }

                    // click elsewhere — close modals
                    Buttons.closeAllDropdowns();
                    if (Inventory.instance != null) { Inventory.instance.dispose(); Inventory.instance = null; }
                    if (Shop.instance != null)      { Shop.instance.dispose();      Shop.instance = null; }
                }
            });
        }

        // ===== MINIGAME INPUT =====
        private boolean hadProgress = false;
    private void handleMinigameInput() {
        float greenLeft  = fishIndicator * (1f - greenZoneWidth);
        float greenRight = greenLeft + greenZoneWidth;
        boolean inGreen  = greenLeft <= 0.5f && greenRight >= 0.5f;

        if (inGreen) {
            catchProgress = Math.min(1f, catchProgress + 0.2f);
            hadProgress = true;
            if (catchProgress >= 1f) completeCatch();
        } else {
            catchProgress = Math.max(0f, catchProgress - 0.15f);
            if (catchProgress <= 0f && hadProgress) failCatch();
        }
    }

        // called when catch progress fills up
        private void completeCatch() {
            fishingInProgress = false;
            showFishResult    = true;
            menuButton.setVisible(true);
            inventoryButton.setVisible(true);
            cancelButton.setVisible(false);

            Inventory.addItem(lastCaughtFish);
            if (!isJunk(lastCaughtFish)) PlayerData.incrementCaught(lastCaughtFish);

            // consume bait
            if (!selectedBait.equals("No Bait")) {
                Inventory.removeItem(selectedBait);
                if (Inventory.items.getOrDefault(selectedBait, 0) <= 0) {
                    selectedBait = "No Bait";
                    repaint();
                }
            }

            // hide result after 2 seconds
            new Timer(2000, ev -> {
                showFishResult = false;
                repaint();
                ((Timer) ev.getSource()).stop();
            }).start();
        }

        // FAIL STATE
        private void failCatch() {
            fishingInProgress = false;
            hadProgress       = false;
            catchProgress     = 0f;
            showFishResult    = true;
            lastCaughtFish    = null;
            menuButton.setVisible(true);
            inventoryButton.setVisible(true);
            cancelButton.setVisible(false);

            new Timer(2000, ev -> {
                showFishResult = false;
                repaint();
                ((Timer) ev.getSource()).stop();
            }).start();
        }

        // ===== PAINT =====
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (fishingInProgress) {
                drawMinigame(g);
            } else {
                g.drawImage(pondBg.getImage(), 0, 0, getWidth(), getHeight(), this);
                if (pierHovered && pondHover != null)
                    g.drawImage(pondHover.getImage(), 0, 0, getWidth(), getHeight(), this);
            }

            drawFishResult(g);
            drawBaitBar(g);
        }

        // draws the minigame bars and instructions
        private void drawMinigame(Graphics g) {
            g.drawImage(fishingGif.getImage(), 0, 0, getWidth(), getHeight(), this);

            // green zone bar background
            g.setColor(new Color(80, 20, 20));
            g.fillRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 10, 10);

            // moving green zone
            int greenW = (int)(greenZoneWidth * BAR_W);
            int greenX = BAR_X + (int)(fishIndicator * (BAR_W - greenW));
            g.setColor(new Color(0, 200, 0));
            g.fillRect(greenX, BAR_Y, greenW, BAR_H);

            // center line (target)
            g.setColor(Color.WHITE);
            g.fillRect(BAR_X + BAR_W / 2 - 3, BAR_Y - 5, 6, BAR_H + 10);

            // bar border
            g.setColor(Color.BLACK);
            g.drawRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 10, 10);

            // catch progress bar
            g.setColor(new Color(20, 20, 80));
            g.fillRoundRect(BAR_X, CATCH_Y, BAR_W, CATCH_H, 10, 10);
            int fillW = (int)(catchProgress * BAR_W);
            if (fillW > 0) {
                g.setColor(new Color(50, 150, 255));
                g.fillRoundRect(BAR_X, CATCH_Y, fillW, CATCH_H, 10, 10);
            }
            g.setColor(Color.BLACK);
            g.drawRoundRect(BAR_X, CATCH_Y, BAR_W, CATCH_H, 10, 10);

            // instruction
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Click when the green zone is on the line!", BAR_X, BAR_Y - 15);
        }

        // draws the catch result banner
        private void drawFishResult(Graphics g) {
            if (!showFishResult) return;
            if (lastCaughtFish == null) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, getHeight() / 2 - 50, getWidth(), 100);
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                String fail = "The fish got away!";
                int tw = g.getFontMetrics().stringWidth(fail);
                g.drawString(fail, (getWidth() - tw) / 2, getHeight() / 2 + 10);
                return;
            }

            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, getHeight() / 2 - 50, getWidth(), 100);

            boolean isJunk = isJunk(lastCaughtFish);
            String text = (isJunk ? "You found: " : "You caught: ")
                + lastCaughtFish + "! (" + getChance(lastCaughtFish) + "%)";

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2 + 10);
        }

        // draws the bait selector UI
        private void drawBaitBar(Graphics g) {
            g.drawImage(baitSelectImage.getImage(), 5, 625, 180, 70, this);

            if (!selectedBait.equals("No Bait")) {
                // icon background
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRoundRect(10, 634, 48, 48, 8, 8);

                // bait icon
                java.net.URL baitUrl = getClass().getResource("/images/"
                    + Character.toLowerCase(selectedBait.charAt(0))
                    + selectedBait.substring(1).replace(" ", "") + ".png");
                if (baitUrl != null)
                    g.drawImage(new ImageIcon(baitUrl).getImage(), 10, 634, 48, 48, this);

                // bait name and count
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.drawString(selectedBait, 65, 653);
                g.drawString("x" + Inventory.items.getOrDefault(selectedBait, 0), 65, 673);
            } else {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("No Bait", 65, 663);
            }
        }

        // ===== START FISHING =====
        private void startFishing() {
            menuButton.setVisible(false);
            inventoryButton.setVisible(false);
            cancelButton.setVisible(true);
            showFishResult    = false;
            lastCaughtFish    = null;
            fishingInProgress = true;
            catchProgress     = 0f;
            hadProgress = false;
            fishIndicator     = 0f;
            indicatorDir      = 1f;
            lastCaughtFish    = pickCatch();

            // difficulty: junk = easier (bigger zone, slower bar), fish = harder
            boolean junk = isJunk(lastCaughtFish);
            greenZoneWidth = junk ? 0.45f : 0.25f;
            indicatorSpeed = junk ? 0.06f : 0.07f;

            // rod bonuses
            if ("bambooRod".equals(PlayerData.equippedRod)) greenZoneWidth += 0.1f;
            if ("hotdogRod".equals(PlayerData.equippedRod)) greenZoneWidth += 0.25f;
            greenZoneWidth = Math.min(greenZoneWidth, 0.95f);

            repaint();
        }

        // ===== HELPERS =====
        private boolean isJunk(String name) {
            return name.equals("Sandal") || name.equals("Shoe") || name.equals("Plastic Wrapper");
        }

        // ===== LOOT TABLE =====
        private String pickCatch() {
            int roll = (int)(Math.random() * 100);
            switch (selectedBait) {
                case "Worm Bait":
                    if (roll < 34) return "Perch";
                    if (roll < 56) return "Carp";
                    if (roll < 66) return "Catfish";
                    if (roll < 71) return "Bass";
                    if (roll < 87) return "Plastic Wrapper";
                    if (roll < 95) return "Sandal";
                    return "Shoe";
                case "Insect Bait":
                    if (roll < 28) return "Perch";
                    if (roll < 48) return "Carp";
                    if (roll < 70) return "Catfish";
                    if (roll < 79) return "Bass";
                    if (roll < 91) return "Plastic Wrapper";
                    if (roll < 97) return "Sandal";
                    return "Shoe";
                case "Fish Bait":
                    if (roll < 22) return "Perch";
                    if (roll < 42) return "Carp";
                    if (roll < 62) return "Catfish";
                    if (roll < 86) return "Bass";
                    if (roll < 94) return "Plastic Wrapper";
                    if (roll < 98) return "Sandal";
                    return "Shoe";
                case "Magic Bait":
                    if (roll < 15) return "Perch";
                    if (roll < 35) return "Carp";
                    if (roll < 65) return "Catfish";
                    return "Bass";
                default: // No Bait
                    if (roll < 12) return "Perch";
                    if (roll < 22) return "Carp";
                    if (roll < 30) return "Catfish";
                    if (roll < 36) return "Bass";
                    if (roll < 62) return "Plastic Wrapper";
                    if (roll < 81) return "Sandal";
                    return "Shoe";
            }
        }

        // ===== CATCH CHANCE DISPLAY =====
        private int getChance(String name) {
            switch (selectedBait) {
                case "Worm Bait":
                    switch (name) {
                        case "Perch": return 34; case "Carp": return 22;
                        case "Catfish": return 10; case "Bass": return 5;
                        case "Plastic Wrapper": return 16; case "Sandal": return 8;
                        case "Shoe": return 5; default: return 0;
                    }
                case "Insect Bait":
                    switch (name) {
                        case "Perch": return 28; case "Carp": return 20;
                        case "Catfish": return 22; case "Bass": return 9;
                        case "Plastic Wrapper": return 12; case "Sandal": return 6;
                        case "Shoe": return 3; default: return 0;
                    }
                case "Fish Bait":
                    switch (name) {
                        case "Perch": return 22; case "Carp": return 20;
                        case "Catfish": return 20; case "Bass": return 24;
                        case "Plastic Wrapper": return 8; case "Sandal": return 4;
                        case "Shoe": return 2; default: return 0;
                    }
                case "Magic Bait":
                    switch (name) {
                        case "Perch": return 15; case "Carp": return 20;
                        case "Catfish": return 30; case "Bass": return 35;
                        default: return 0;
                    }
                default:
                    switch (name) {
                        case "Perch": return 12; case "Carp": return 10;
                        case "Catfish": return 8; case "Bass": return 6;
                        case "Plastic Wrapper": return 26; case "Sandal": return 19;
                        case "Shoe": return 19; default: return 0;
                    }
            }
        }
    }
}