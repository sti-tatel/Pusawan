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

    // New Mechanics State
    private int     driftDir          = 1;     // 1 for right, -1 for left
    private boolean isMouseDown       = false; // tracks if player is holding click/space to pull
    private long    fishMoveStartTime = 0;     // tracks the 0.3 - 0.5s freeze delay
    private long    nextFishTime      = 0;     // 1-second cooldown tracker

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

                //speed
                //player speed
                float driftSpeed = 0.020f; // SPEED WHEN NOT CLICKING
                float pullSpeed  = 0.020f; // SPEED WHEN HOLDING CLICK
                if (isMouseDown) {
                    playerX += -driftDir * pullSpeed; 
                } else {
                    playerX += driftDir * driftSpeed; 
                }
                // clamp player to bar bounds
                playerX = Math.max(playerSize / 2, Math.min(1f - playerSize / 2, playerX));

                // fish indicator stays still for 0.3-0.5s before moving
                if (System.currentTimeMillis() > fishMoveStartTime) {
                    // Increased to 0.05 (5% chance) so it changes direction more often
                    //fish speed
                    if (Math.random() < 0.05) { 
                        //bar jerk
                        //fish jerk speed
                        // Increased from 0.02/0.01 to 0.04/0.02 to make the "jerks" much stronger
                        fishVel += (float)(Math.random() * 0.04 - 0.02); 
                    }
                    // Increased max speed from 0.015f to 0.03f so it can travel faster
                    fishVel = Math.max(-0.03f, Math.min(0.03f, fishVel)); 
                    fishX  += fishVel;
                    // bounce off edges
                    if (fishX <= 0f) { fishX = 0f; fishVel = Math.abs(fishVel); }
                    if (fishX >= 1f) { fishX = 1f; fishVel = -Math.abs(fishVel); }
                }

                // check overlap: player bracket contains fish indicator
                float pLeft  = playerX - playerSize / 2;
                float pRight = playerX + playerSize / 2;
                boolean overlap = fishX >= pLeft && fishX <= pRight;

                // progress bar fills when overlapping, drains when not
                // adjust +0.004f (fill rate) and -0.003f (drain rate)
                if (overlap) {
                    //player health
                    //health gain
                    catchProgress = Math.min(1f, catchProgress + 0.013f);
                    if (catchProgress >= 1f) background.completeCatch();
                } else {
                    //health drain
                    //player damage
                    //damage
                    catchProgress = Math.max(0f, catchProgress - 0.004f);
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

        inventoryButton = Buttons.createInventoryButton();
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
        isMouseDown       = false;
        nextFishTime      = System.currentTimeMillis() + 1000; // 1-second cooldown
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
        // adjust Rectangle(x, y, width, height) to match pier image
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

            // spacebar: Press/Release logic for the minigame
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "spaceDown");
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"), "spaceUp");
            
            getActionMap().put("spaceDown", new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (fishingInProgress) isMouseDown = true;
                }
            });

            getActionMap().put("spaceUp", new javax.swing.AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (fishingInProgress) isMouseDown = false;
                }
            });

            // mouse: Hover effects
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (!fishingInProgress) {
                        boolean over = pierHitbox.contains(e.getPoint()) && Inventory.instance == null;
                        if (over != pierHovered) { pierHovered = over; repaint(); }
                    }
                }
            });

            // mouse: Press/Release logic for controlling the new drift minigame
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    isMouseDown = true;
                    if (fishingInProgress) return;

                    // bait selector click
                    if (new Rectangle(5, 625, 180, 70).contains(e.getPoint())) {
                        Inventory.toggleWithMode("bait");
                        return;
                    }

                    // pier click — start fishing
                    if (pierHitbox.contains(e.getPoint())) {
                        if (System.currentTimeMillis() < nextFishTime) return; // Wait 1 second!
                        startFishing();
                        return;
                    }

                    // click elsewhere — close modals
                    Buttons.closeAllDropdowns();
                    if (Inventory.instance != null) { Inventory.instance.closeInventory(); }
                    if (Shop.instance != null) { Shop.instance.dispose(); Shop.instance = null; }
                }

                public void mouseReleased(MouseEvent e) {
                    isMouseDown = false;
                }
            });

            
        }

        // called when progress bar fills completely — fish caught
        void completeCatch() {
            fishingInProgress = false;
            showFishResult    = true;
            nextFishTime      = System.currentTimeMillis() + 1000; // 1-second cooldown
            catchProgress     = 0.25f;
            menuButton.setVisible(true);
            inventoryButton.setVisible(true);
            cancelButton.setVisible(false);

            Inventory.addItem(lastCaughtFish);
            if (!isJunk(lastCaughtFish)) PlayerData.incrementCaught(lastCaughtFish);

            if (!isJunk(lastCaughtFish)) {
                switch (lastCaughtFish) {
                    case "Perch":            PlayerData.addXP(10); break; // +10 XP
                    case "Carp":             PlayerData.addXP(15); break; // +15 XP
                    case "Catfish":          PlayerData.addXP(20); break; // +20 XP
                    case "Bass":             PlayerData.addXP(30); break; // +30 XP
                    case "Sandal":           PlayerData.addXP(2);  break; // +2 XP
                    case "Shoe":             PlayerData.addXP(2);  break; // +2 XP
                    case "Plastic Wrapper":  PlayerData.addXP(1);  break; // +1 XP
                }
            }

            // consume bait
            if (!selectedBait.equals("No Bait")) {
                Inventory.removeItem(selectedBait);
                if (Inventory.items.getOrDefault(selectedBait, 0) <= 0) {
                    selectedBait = "No Bait";
                }
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
            nextFishTime      = System.currentTimeMillis() + 1000; // 1-second cooldown
            lastCaughtFish    = null;
            if (!selectedBait.equals("No Bait")) {
                Inventory.removeItem(selectedBait);
                if (Inventory.items.getOrDefault(selectedBait, 0) <= 0) {
                    selectedBait = "No Bait";
                }
            }
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
                if (pierHovered && pondHover != null) {
                    g.drawImage(pondHover.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }

            drawFishResult(g);
            drawBaitBar(g);
            drawXPBar(g);
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
            // filled grayish-white to remain discernable from the fish bar
            int pPx = (int)(playerX * BAR_W) + BAR_X;
            int pHalfW = (int)(playerSize * BAR_W / 2);
            int pLeft  = pPx - pHalfW;

            g2.setColor(new Color(210, 210, 210)); 
            g2.fillRoundRect(pLeft, BAR_Y + 2, pHalfW * 2, BAR_H - 4, 8, 8);
            g2.setColor(new Color(150, 150, 150)); 
            g2.drawRoundRect(pLeft, BAR_Y + 2, pHalfW * 2, BAR_H - 4, 8, 8);

            // ---- FISH INDICATOR ----
            // thin vertical line that moves randomly
            int fPx = (int)(fishX * BAR_W) + BAR_X;
            
            // thin black outline
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(5));
            g2.drawLine(fPx, BAR_Y + 2, fPx, BAR_Y + BAR_H - 2);

            // white inner line
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawLine(fPx, BAR_Y + 2, fPx, BAR_Y + BAR_H - 2);

            // small fish icon on top of indicator
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Fish", fPx - 8, BAR_Y - 6);

            g2.setStroke(new java.awt.BasicStroke(1));

            // ---- PROGRESS BAR ----
            // white bar below showing catch progress — starts at 1/4
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(BAR_X, PROG_Y, BAR_W, PROG_H, 8, 8);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(BAR_X, PROG_Y, (int)(catchProgress * BAR_W), PROG_H, 8, 8);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(BAR_X, PROG_Y, BAR_W, PROG_H, 8, 8);

            // instruction text updated for new mechanic
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String hint = "Hold Click or Space to pull!";
            int tw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, BAR_X + BAR_W / 2 - tw / 2, BAR_Y - 18);

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
                text = PlayerData.equippedRod == null || PlayerData.equippedRod.equals("None")
                    ? "You need a rod to fish!"
                    : "The fish got away!";
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
                if (baitUrl != null) {
                    g.drawImage(new ImageIcon(baitUrl).getImage(), 10, 634, 48, 48, this);
                }

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
        if (PlayerData.equippedRod == null || PlayerData.equippedRod.equals("None")) {
            showFishResult = true;
            lastCaughtFish = null;
            new Timer(2000, ev -> { showFishResult = false; repaint(); ((Timer)ev.getSource()).stop(); }).start();
            return;
        }
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
            isMouseDown       = false;

            // 50/50 RNG determines which way the bar drifts for this session
            driftDir = Math.random() < 0.5 ? 1 : -1;
            
            // Fish stays frozen for 0.3 to 0.5 seconds (300ms to 500ms)
            fishMoveStartTime = System.currentTimeMillis() + (long)(Math.random() * 200 + 300);

            // difficulty: junk = easier (slower fish, bigger bracket)
            // fish = harder (faster fish, smaller bracket)
            boolean junk = isJunk(lastCaughtFish);
            //bar size
            //player bar size
            //capture size
            playerSize = junk ? 0.20f : 0.20f; // bracket width — bigger = easier
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
                    // Worm Bait chances: Perch 34%, Carp 22%, Catfish 10%, Bass 5%, Plastic Wrapper 16%, Sandal 8%, Shoe 5%
                    if (roll < 34) return "Perch";
                    if (roll < 56) return "Carp";
                    if (roll < 66) return "Catfish";
                    if (roll < 71) return "Bass";
                    if (roll < 87) return "Plastic Wrapper";
                    if (roll < 95) return "Sandal";
                    return "Shoe";
                case "Insect Bait":
                    // Insect Bait chances: Perch 28%, Carp 20%, Catfish 22%, Bass 9%, Plastic Wrapper 12%, Sandal 6%, Shoe 3%
                    if (roll < 28) return "Perch";
                    if (roll < 48) return "Carp";
                    if (roll < 70) return "Catfish";
                    if (roll < 79) return "Bass";
                    if (roll < 91) return "Plastic Wrapper";
                    if (roll < 97) return "Sandal";
                    return "Shoe";
                case "Fish Bait":
                    // Fish Bait chances: Perch 22%, Carp 20%, Catfish 20%, Bass 24%, Plastic Wrapper 8%, Sandal 4%, Shoe 2%
                    if (roll < 22) return "Perch";
                    if (roll < 42) return "Carp";
                    if (roll < 62) return "Catfish";
                    if (roll < 86) return "Bass";
                    if (roll < 94) return "Plastic Wrapper";
                    if (roll < 98) return "Sandal";
                    return "Shoe";
                case "Magic Bait":
                    // Magic Bait chances: Perch 15%, Carp 20%, Catfish 30%, Bass 35%
                    if (roll < 15) return "Perch";
                    if (roll < 35) return "Carp";
                    if (roll < 65) return "Catfish";
                    return "Bass";
                default:
                    // No bait chances: Perch 12%, Carp 10%, Catfish 8%, Bass 6%, Plastic Wrapper 26%, Sandal 19%, Shoe 19%
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

        private void drawXPBar(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int barW = 200;
            int barH = 14;
            int barX = getWidth() - barW - 20;
            int barY = 20;
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(barX, barY, barW, barH, 8, 8);
            int fill = (int)((float) PlayerData.xp / PlayerData.xpToNextLevel * barW);
            g2.setColor(new Color(80, 160, 255));
            g2.fillRoundRect(barX, barY, fill, barH, 8, 8);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(barX, barY, barW, barH, 8, 8);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Lv " + PlayerData.level + "  " + PlayerData.xp + "/" + PlayerData.xpToNextLevel, barX, barY - 4);
        }
    }
}