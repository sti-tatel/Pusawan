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
    // fishZoneY: vertical center of the fish zone (0=top, 1=bottom)
    // fishZoneDir: direction the fish zone moves (+1=down, -1=up)
    // fishZoneSpeed: how fast the fish zone moves — increase for harder
    // fishZoneSize: height of the fish zone as a fraction of bar — decrease for harder
    // playerY: vertical center of player bracket
    // playerSize: height of player bracket — smaller = harder
    // catchProgress: 0 to 1, fills when overlapping — reaches 1 to catch
    // holding: true when player is pressing mouse/space — bracket rises when true
    private float fishZoneY     = 0.5f;
    private float fishZoneDir   = 1f;
    private float fishZoneSpeed = 0.004f;
    private float fishZoneSize  = 0.25f;
    private float playerY       = 0.5f;
    private float playerSize    = 0.15f;
    private float catchProgress = 0f;
    private boolean holding     = false;

    // ===== BAR POSITION =====
    // barX/barY: top-left of the vertical catch bar
    // barW/barH: width and height of the bar
    // adjust these to move/resize the minigame bar on screen
    private static final int BAR_X = 1100;  // left edge — adjust to align with pier
    private static final int BAR_Y = 100;   // vertical position — adjust to sit on pier
    private static final int BAR_W = 60;    // width — spans the pier
    private static final int BAR_H = 500;   // height — thin horizontal bar

    // ===== BUTTONS =====
    private JButton menuButton;
    private JButton inventoryButton;
    private JButton cancelButton;
    private BackgroundPanel background;

    public static void setSelectedBait(String bait) {
        selectedBait = bait;
    }

    // ===== CONSTRUCTOR =====
    public Fishing() {

        // game loop — runs every 16ms (~60fps)
        // moves fish zone, applies gravity/lift to player bracket, checks overlap
        new javax.swing.Timer(16, e -> {
            if (!isShowing()) return;
            if (fishingInProgress) {

                // move fish zone up and down, bounce off edges
                fishZoneY += fishZoneDir * fishZoneSpeed;
                if (fishZoneY >= 1f - fishZoneSize / 2) { fishZoneY = 1f - fishZoneSize / 2; fishZoneDir = -1f; }
                if (fishZoneY <= fishZoneSize / 2)       { fishZoneY = fishZoneSize / 2;      fishZoneDir =  1f; }

                // player bracket: rises when holding, falls when not
                // adjust 0.008f (fall speed) and 0.012f (rise speed) for feel
                if (!holding) playerY = Math.min(1f - playerSize / 2, playerY + 0.008f);
                else          playerY = Math.max(playerSize / 2,      playerY - 0.012f);

                // check overlap between fish zone and player bracket
                float fishTop   = fishZoneY - fishZoneSize / 2;
                float fishBot   = fishZoneY + fishZoneSize / 2;
                float playerTop = playerY   - playerSize   / 2;
                float playerBot = playerY   + playerSize   / 2;
                boolean overlap = playerTop < fishBot && playerBot > fishTop;

                // fill progress when overlapping, drain when not
                // adjust +0.005f (fill rate) and -0.003f (drain rate)
                if (overlap) {
                    catchProgress = Math.min(1f, catchProgress + 0.005f);
                    if (catchProgress >= 1f) background.completeCatch();
                } else {
                    catchProgress = Math.max(0f, catchProgress - 0.003f);
                }
            }
            repaint();
        }).start();

        background = new BackgroundPanel();
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

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(20, 460, 100, 40);
        cancelButton.setVisible(false);
        Buttons.addClickSound(cancelButton);
        cancelButton.addActionListener(e -> cancelFishing());
        background.add(cancelButton);

        // disable spacebar on UI buttons to avoid conflict with minigame
        for (JButton b : new JButton[]{cancelButton, menuButton}) {
            b.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "none");
            b.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "none");
        }
    }

    // cancels fishing mid-session and resets all state
    private void cancelFishing() {
        fishingInProgress = false;
        catchProgress     = 0f;
        playerY           = 0.5f;
        fishZoneY         = 0.5f;
        holding           = false;
        menuButton.setVisible(true);
        inventoryButton.setVisible(true);
        cancelButton.setVisible(false);
        repaint();
    }

    // ===== BACKGROUND PANEL =====
    class BackgroundPanel extends JPanel {

        private boolean   pierHovered = false;
        // pierHitbox: clickable area on the pier to start fishing
        // adjust Rectangle(x, y, width, height) to match your pier image
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

            // spacebar: hold to rise, release to fall
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed SPACE"),   "fishDown");
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),  "fishUp");
            getActionMap().put("fishDown", new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (fishingInProgress) holding = true;
                }
            });
            getActionMap().put("fishUp", new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) { holding = false; }
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
                    if (fishingInProgress) return;

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
                    if (Inventory.instance != null) { Inventory.instance.closeInventory(); }
                    if (Shop.instance != null)      { Shop.instance.dispose(); Shop.instance = null; }
                }

                // hold mouse to rise bracket during minigame
                public void mousePressed(MouseEvent e)  { if (fishingInProgress) holding = true; }
                public void mouseReleased(MouseEvent e) { holding = false; }
            });
        }

        // called when catch progress reaches 1
        void completeCatch() {
            fishingInProgress = false;
            showFishResult    = true;
            holding           = false;
            menuButton.setVisible(true);
            inventoryButton.setVisible(true);
            cancelButton.setVisible(false);

            Inventory.addItem(lastCaughtFish);
            if (!isJunk(lastCaughtFish)) PlayerData.incrementCaught(lastCaughtFish);

            // consume bait on successful catch
            if (!selectedBait.equals("No Bait")) {
                Inventory.removeItem(selectedBait);
                if (Inventory.items.getOrDefault(selectedBait, 0) <= 0) {
                    selectedBait = "No Bait";
                }
            }

            // hide result banner after 2 seconds
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
                drawFishingBackground(g);
                drawMinigame(g);
            } else {
                g.drawImage(pondBg.getImage(), 0, 0, getWidth(), getHeight(), this);
                if (pierHovered && pondHover != null)
                    g.drawImage(pondHover.getImage(), 0, 0, getWidth(), getHeight(), this);
            }

            drawFishResult(g);
            drawBaitBar(g);
        }

        // draws the fishing gif background during minigame
        private void drawFishingBackground(Graphics g) {
            g.drawImage(fishingGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        // draws the vertical minigame bar, fish zone, player bracket, and progress bar
        private void drawMinigame(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // ---- BACKGROUND BAR ----
            // dark semi-transparent rounded rectangle
            g2.setColor(new Color(20, 20, 30, 200));
            g2.fillRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 20, 20);
            // subtle white border
            g2.setColor(new Color(255, 255, 255, 40));
            g2.drawRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 20, 20);

            // ---- FISH ZONE ----
            // glowing teal rectangle that the player must overlap
            int fishPx  = (int)(fishZoneY * BAR_H) + BAR_Y;
            int fishHPx = (int)(fishZoneSize * BAR_H);
            int fishTop = fishPx - fishHPx / 2;
            // fill
            g2.setColor(new Color(80, 220, 200, 180));
            g2.fillRoundRect(BAR_X + 4, fishTop, BAR_W - 8, fishHPx, 10, 10);
            // glow border
            g2.setColor(new Color(150, 255, 240, 220));
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawRoundRect(BAR_X + 4, fishTop, BAR_W - 8, fishHPx, 10, 10);

            // ---- PLAYER BRACKET ----
            // white bracket the player controls by holding/releasing
            int playerPx  = (int)(playerY * BAR_H) + BAR_Y;
            int playerHPx = (int)(playerSize * BAR_H);
            int playerTop = playerPx - playerHPx / 2;

            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(3));
            // top line
            g2.drawLine(BAR_X + 5, playerTop, BAR_X + BAR_W - 5, playerTop);
            // bottom line
            g2.drawLine(BAR_X + 5, playerTop + playerHPx, BAR_X + BAR_W - 5, playerTop + playerHPx);
            // left side
            g2.drawLine(BAR_X + 5, playerTop, BAR_X + 5, playerTop + playerHPx);
            // right side
            g2.drawLine(BAR_X + BAR_W - 5, playerTop, BAR_X + BAR_W - 5, playerTop + playerHPx);

            // reset stroke
            g2.setStroke(new java.awt.BasicStroke(1));

            // ---- CATCH PROGRESS BAR ---- 
            // PLAYER HEALTH
            // horizontal bar below the vertical bar showing catch progress
            int progX = 350;  // left edge
            int progY = 650;  // vertical position — on the pier
            int progW = 600;  // width — spans the pier
            int progH = 22;   // height
            g2.setColor(new Color(20, 20, 30, 200));
            g2.fillRoundRect(progX, progY, progW, progH, 10, 10);
            g2.setColor(new Color(80, 220, 150));
            g2.fillRoundRect(progX, progY, (int)(catchProgress * progW), progH, 10, 10);
            g2.setColor(new Color(255, 255, 255, 80));
            g2.drawRoundRect(progX, progY, progW, progH, 10, 10);
            
            // background
            g2.setColor(new Color(20, 20, 30, 200));
            g2.fillRoundRect(progX, progY, progW, progH, 10, 10);
            // fill — green
            g2.setColor(new Color(80, 220, 150));
            g2.fillRoundRect(progX, progY, (int)(catchProgress * progW), progH, 10, 10);
            // border
            g2.setColor(new Color(255, 255, 255, 80));
            g2.drawRoundRect(progX, progY, progW, progH, 10, 10);

            // ---- INSTRUCTION TEXT ----
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("Hold to rise!", BAR_X - 20, BAR_Y - 15);
        }

        // draws the catch result banner after fishing completes
        private void drawFishResult(Graphics g) {
            if (!showFishResult || lastCaughtFish == null) return;

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

        // draws the bait selector UI in bottom-left
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
            lastCaughtFish    = pickCatch();
            fishingInProgress = true;
            catchProgress     = 0f;
            playerY           = 0.5f;
            fishZoneY         = 0.5f;
            fishZoneDir       = 1f;
            holding           = false;

            // difficulty settings based on catch type
            // junk: easier (bigger zone, slower movement)
            // fish: harder (smaller zone, faster movement)
            boolean junk = isJunk(lastCaughtFish);
            fishZoneSize  = junk ? 0.35f : 0.20f; // zone height fraction — bigger = easier
            fishZoneSpeed = junk ? 0.003f : 0.007f; // zone movement speed — slower = easier
            playerSize    = junk ? 0.18f : 0.12f; // bracket height fraction — bigger = easier

            // rod bonuses — equipped rod widens fish zone and player bracket
            if ("bambooRod".equals(PlayerData.equippedRod)) { fishZoneSize += 0.05f; playerSize += 0.03f; }
            if ("hotdogRod".equals(PlayerData.equippedRod)) { fishZoneSize += 0.10f; playerSize += 0.06f; }

            repaint();
        }

        // ===== HELPERS =====
        private boolean isJunk(String name) {
            return name.equals("Sandal") || name.equals("Shoe") || name.equals("Plastic Wrapper");
        }

        // ===== LOOT TABLE =====
        // rolls a random catch based on selected bait
        private String pickCatch() {
            int roll = (int)(Math.random() * 100);
            switch (selectedBait) {
                case "Worm Bait":
                    if (roll < 34) return "Perch";       // 34%
                    if (roll < 56) return "Carp";        // 22%
                    if (roll < 66) return "Catfish";     // 10%
                    if (roll < 71) return "Bass";        // 5%
                    if (roll < 87) return "Plastic Wrapper"; // 16%
                    if (roll < 95) return "Sandal";      // 8%
                    return "Shoe";                       // 5%
                case "Insect Bait":
                    if (roll < 28) return "Perch";       // 28%
                    if (roll < 48) return "Carp";        // 20%
                    if (roll < 70) return "Catfish";     // 22%
                    if (roll < 79) return "Bass";        // 9%
                    if (roll < 91) return "Plastic Wrapper"; // 12%
                    if (roll < 97) return "Sandal";      // 6%
                    return "Shoe";                       // 3%
                case "Fish Bait":
                    if (roll < 22) return "Perch";       // 22%
                    if (roll < 42) return "Carp";        // 20%
                    if (roll < 62) return "Catfish";     // 20%
                    if (roll < 86) return "Bass";        // 24%
                    if (roll < 94) return "Plastic Wrapper"; // 8%
                    if (roll < 98) return "Sandal";      // 4%
                    return "Shoe";                       // 2%
                case "Magic Bait":
                    if (roll < 15) return "Perch";       // 15%
                    if (roll < 35) return "Carp";        // 20%
                    if (roll < 65) return "Catfish";     // 30%
                    return "Bass";                       // 35%
                default: // No Bait
                    if (roll < 12) return "Perch";       // 12%
                    if (roll < 22) return "Carp";        // 10%
                    if (roll < 30) return "Catfish";     // 8%
                    if (roll < 36) return "Bass";        // 6%
                    if (roll < 62) return "Plastic Wrapper"; // 26%
                    if (roll < 81) return "Sandal";      // 19%
                    return "Shoe";                       // 19%
            }
        }

        // ===== CATCH CHANCE DISPLAY =====
        // returns the percentage chance of catching this fish with current bait
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