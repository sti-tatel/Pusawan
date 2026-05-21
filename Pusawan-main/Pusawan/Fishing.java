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
    // playerX: position of the white player bracket (0=left, 1=right)
    // fishX: position of the thin vertical fish indicator (0=left, 1=right)
    // fishVel: current velocity of fish indicator — changes randomly
    // catchProgress: 0 to 1, shown as bottom bar — gain when overlapping, lose when not
    // PLAYER_SIZE: width of player bracket as fraction of bar — bigger = easier
    // FISH_SIZE: width of fish indicator as fraction of bar
    private float playerX       = 0.5f;
    private float fishX         = 0.5f;
    private float fishVel       = 0f;
    private float catchProgress = 0.25f; // starts at 1/4
    private float playerSize    = 0.15f; // player bracket width fraction
    private float fishSize      = 0.015f; // thin fish indicator width

    // ===== BAR LAYOUT =====
    // main horizontal bar position and size — adjust to sit across your pier
    private static final int BAR_X = 375;  // left edge of bar
    private static final int BAR_Y = 640;  // vertical position
    private static final int BAR_W = 600;  // total width
    private static final int BAR_H = 40;   // height of main bar

    // progress bar below main bar
    private static final int PROG_Y = 690; // vertical position of progress bar
    private static final int PROG_H = 14;  // height of progress bar

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
        new javax.swing.Timer(16, e -> {
            if (!isShowing()) return;
            if (fishingInProgress) {

                // fish indicator moves randomly with occasional direction changes
                // Math.random() < 0.03: 3% chance per frame to add random impulse — increase for more erratic
                if (Math.random() < 0.03) fishVel += (float)(Math.random() * 0.02 - 0.01);
                // clamp velocity so it doesn't get too fast — adjust 0.015f for max speed
                fishVel = Math.max(-0.015f, Math.min(0.015f, fishVel));
                fishX  += fishVel;
                // bounce off edges
                if (fishX <= 0f) { fishX = 0f; fishVel = Math.abs(fishVel); }
                if (fishX >= 1f) { fishX = 1f; fishVel = -Math.abs(fishVel); }

                // check overlap: player bracket contains fish indicator
                float pLeft  = playerX - playerSize / 2;
                float pRight = playerX + playerSize / 2;
                boolean overlap = fishX >= pLeft && fishX <= pRight;

                // progress bar fills when overlapping, drains when not
                // adjust +0.004f (fill rate) and -0.003f (drain rate)
                if (overlap) {
                    catchProgress = Math.min(1f, catchProgress + 0.004f);
                    if (catchProgress >= 1f) background.completeCatch();
                } else {
                    catchProgress = Math.max(0f, catchProgress - 0.003f);
                    if (catchProgress <= 0f) background.failCatch();
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

    // cancels fishing and resets all minigame state
    private void cancelFishing() {
        fishingInProgress = false;
        catchProgress     = 0.25f;
        playerX           = 0.5f;
        fishX             = 0.5f;
        fishVel           = 0f;
        menuButton.setVisible(true);
        inventoryButton.setVisible(true);
        cancelButton.setVisible(false);
        repaint();
    }

    // ===== BACKGROUND PANEL =====
    class BackgroundPanel extends JPanel {

        private boolean   pierHovered = false;
        // pierHitbox: clickable zone on the pier to start fishing
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

            // mouse: move player bracket to follow cursor X during minigame
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (fishingInProgress) {
                        // map cursor X to 0-1 range within the bar
                        playerX = Math.max(playerSize / 2, Math.min(1f - playerSize / 2,
                            (float)(e.getX() - BAR_X) / BAR_W));
                    } else {
                        boolean over = pierHitbox.contains(e.getPoint()) && Inventory.instance == null;
                        if (over != pierHovered) { pierHovered = over; repaint(); }
                    }
                }
                public void mouseDragged(MouseEvent e) {
                    if (fishingInProgress) {
                        playerX = Math.max(playerSize / 2, Math.min(1f - playerSize / 2,
                            (float)(e.getX() - BAR_X) / BAR_W));
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
                    if (Shop.instance != null) { Shop.instance.dispose(); Shop.instance = null; }
                }
            });
        }

        // called when progress bar fills completely — fish caught
        void completeCatch() {
            fishingInProgress = false;
            showFishResult    = true;
            catchProgress     = 0.25f;
            menuButton.setVisible(true);
            inventoryButton.setVisible(true);
            cancelButton.setVisible(false);

            Inventory.addItem(lastCaughtFish);
            if (!isJunk(lastCaughtFish)) PlayerData.incrementCaught(lastCaughtFish);

            // consume bait
            if (!selectedBait.equals("No Bait")) {
                Inventory.removeItem(selectedBait);
                if (Inventory.items.getOrDefault(selectedBait, 0) <= 0)
                    selectedBait = "No Bait";
            }

            // hide result after 2 seconds
            new Timer(2000, ev -> {
                showFishResult = false;
                repaint();
                ((Timer) ev.getSource()).stop();
            }).start();
        }

        // called when progress bar drains completely — fish escaped
        void failCatch() {
            fishingInProgress = false;
            showFishResult    = true;
            lastCaughtFish    = null;
            catchProgress     = 0.25f;
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
                g.drawImage(fishingGif.getImage(), 0, 0, getWidth(), getHeight(), this);
                drawMinigame(g);
            } else {
                g.drawImage(pondBg.getImage(), 0, 0, getWidth(), getHeight(), this);
                if (pierHovered && pondHover != null)
                    g.drawImage(pondHover.getImage(), 0, 0, getWidth(), getHeight(), this);
            }

            drawFishResult(g);
            drawBaitBar(g);
        }

        // draws the horizontal minigame bar, player bracket, fish indicator, and progress bar
        private void drawMinigame(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // ---- MAIN BAR BACKGROUND ----
            // dark semi-transparent rounded bar
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);
            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);

            // ---- PLAYER BRACKET ----
            // white bracket the player moves with the mouse
            int pPx = (int)(playerX * BAR_W) + BAR_X;
            int pHalfW = (int)(playerSize * BAR_W / 2);
            int pLeft  = pPx - pHalfW;
            int pRight = pPx + pHalfW;

            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(3));
            // left arrow wing
            g2.drawLine(pLeft, BAR_Y + BAR_H / 2, pLeft + 12, BAR_Y + 6);
            g2.drawLine(pLeft, BAR_Y + BAR_H / 2, pLeft + 12, BAR_Y + BAR_H - 6);
            // right arrow wing
            g2.drawLine(pRight, BAR_Y + BAR_H / 2, pRight - 12, BAR_Y + 6);
            g2.drawLine(pRight, BAR_Y + BAR_H / 2, pRight - 12, BAR_Y + BAR_H - 6);
            // top and bottom lines connecting bracket
            g2.drawLine(pLeft + 12, BAR_Y + 6, pRight - 12, BAR_Y + 6);
            g2.drawLine(pLeft + 12, BAR_Y + BAR_H - 6, pRight - 12, BAR_Y + BAR_H - 6);

            // ---- FISH INDICATOR ----
            // thin vertical white line that moves randomly
            int fPx = (int)(fishX * BAR_W) + BAR_X;
            g2.setColor(new Color(255, 255, 255, 220));
            g2.setStroke(new java.awt.BasicStroke(3));
            g2.drawLine(fPx, BAR_Y + 4, fPx, BAR_Y + BAR_H - 4);
            // small fish icon below indicator
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("🐟", fPx - 8, BAR_Y + BAR_H + 16);

            g2.setStroke(new java.awt.BasicStroke(1));

            // ---- PROGRESS BAR ----
            // white bar below showing catch progress — starts at 1/4
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(BAR_X, PROG_Y, BAR_W, PROG_H, 8, 8);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(BAR_X, PROG_Y, (int)(catchProgress * BAR_W), PROG_H, 8, 8);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(BAR_X, PROG_Y, BAR_W, PROG_H, 8, 8);

            // instruction text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String hint = "Move your mouse to keep the line on the fish!";
            int tw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, BAR_X + BAR_W / 2 - tw / 2, BAR_Y - 12);
        }

        // draws the catch result banner
        private void drawFishResult(Graphics g) {
            if (!showFishResult) return;

            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, getHeight() / 2 - 50, getWidth(), 100);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));

            String text;
            if (lastCaughtFish == null) {
                text = "The fish got away!";
                g.setColor(new Color(255, 100, 100));
            } else {
                boolean junk = isJunk(lastCaughtFish);
                text = (junk ? "You found: " : "You caught: ")
                    + lastCaughtFish + "! (" + getChance(lastCaughtFish) + "%)";
            }
            int tw = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - tw) / 2, getHeight() / 2 + 10);
        }

        // draws the bait selector UI in bottom-left
        private void drawBaitBar(Graphics g) {
            g.drawImage(baitSelectImage.getImage(), 5, 625, 180, 70, this);

            if (!selectedBait.equals("No Bait")) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRoundRect(10, 634, 48, 48, 8, 8);

                java.net.URL baitUrl = getClass().getResource("/images/"
                    + Character.toLowerCase(selectedBait.charAt(0))
                    + selectedBait.substring(1).replace(" ", "") + ".png");
                if (baitUrl != null)
                    g.drawImage(new ImageIcon(baitUrl).getImage(), 10, 634, 48, 48, this);

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
            catchProgress     = 0.25f; // always starts at 1/4
            playerX           = 0.5f;
            fishX             = 0.5f;
            fishVel           = 0f;

            // difficulty: junk = easier (slower fish, bigger bracket)
            // fish = harder (faster fish, smaller bracket)
            boolean junk = isJunk(lastCaughtFish);
            playerSize = junk ? 0.20f : 0.13f; // bracket width — bigger = easier
            // fishVel random impulse range controlled in game loop via 0.02 multiplier

            // rod bonuses — widens player bracket
            if ("bambooRod".equals(PlayerData.equippedRod)) playerSize += 0.04f;
            if ("hotdogRod".equals(PlayerData.equippedRod)) playerSize += 0.08f;

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
                default:
                    if (roll < 12) return "Perch";
                    if (roll < 22) return "Carp";
                    if (roll < 30) return "Catfish";
                    if (roll < 36) return "Bass";
                    if (roll < 62) return "Plastic Wrapper";
                    if (roll < 81) return "Sandal";
                    return "Shoe";
            }
        }

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