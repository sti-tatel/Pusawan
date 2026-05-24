package Pusawan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Cooking extends JPanel {

    private boolean hovering  = false;
    private boolean cooking   = false;
    private boolean minigame  = false;

    private ImageIcon grillGif;
    private ImageIcon cookingGif;
    private Image     grillSelected;
    

    private Rectangle grillZone = new Rectangle(475, 250, 400, 165);

    private String popupText    = "";
    private long   popupEndTime = 0;

    private static Cooking instance;
    private JButton menuButton;
    private JButton arrowRight;
    private JButton cancelButton;
    public static boolean isCooking = false;

    // ================= MINIGAME STATE =================
    private static final int TOTAL_CIRCLES  = 8;
    private static final int CIRCLE_RADIUS  = 30;
    private static final int SHRINK_START   = 100;  // outer ring starts here
    
    // ADJUSTED FOR BETTER FEEL:
    private static final int SHRINK_SPEED   = 3;   // Slower shrink (was 2) for easier visual tracking
    private static final int PERFECT_WINDOW = 14;  // Doubled perfect window (was 6)
    private static final int GOOD_WINDOW    = 30;  // Widened good window (was 20)

    // health bar
    private static final int BAR_X  = 375;
    private static final int BAR_Y  = 640;
    private static final int BAR_W  = 600;
    private static final int BAR_H  = 40;
    private static final int PROG_Y = 690;
    private static final int PROG_H = 14;

    private float health       = 1.0f;
    private float progress     = 0f;
    private int   circlesDone  = 0;
    private String fishBeingCooked = null;

    // active circle
    private int   circleX      = 0;
    private int   circleY      = 0;
    private int   outerRadius  = SHRINK_START;
    private boolean circleActive = false;
    private String hitResult   = "";  // "Perfect!", "Good", "Bad", "Miss"
    private long   hitEndTime  = 0;
    private Color  hitColor    = Color.WHITE;

    private Timer minigameTimer;

    public Cooking() {
        instance = this;
        loadImages();

        BackgroundPanel panel = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        panel.add(menuButton);

        arrowRight = new JButton(new ImageIcon(getClass().getResource("/images/arrowRight.png")));
        arrowRight.setBounds(1266, 400, 64, 64);
        arrowRight.setBorderPainted(false);
        arrowRight.setContentAreaFilled(false);
        arrowRight.setFocusPainted(false);
        Buttons.addClickSound(arrowRight);
        arrowRight.addActionListener(e -> Game.navigate(Game.KITCHEN));
        panel.add(arrowRight);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(640, 580, 100, 40); // Place it near the progress bar
        cancelButton.setVisible(false);
        cancelButton.addActionListener(e -> endMinigame(false));
        panel.add(cancelButton);


        JButton inventoryButton = Buttons.createInventoryButton();
        panel.add(inventoryButton);

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hovering = grillZone.contains(e.getPoint()) && Inventory.instance == null && !minigame;
                repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (minigame) {
                    handleMinigameClick();
                    return;
                }
                Buttons.closeAllDropdowns();
                if (grillZone.contains(e.getPoint()) && !isCooking) {
                    if (Inventory.getFirstCutFish() == null) return;
                    Inventory.toggleWithMode("cook");
                } else {
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                }
                if (Shop.instance != null) Shop.instance.dispose();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });



        new javax.swing.Timer(16, e -> { if (isShowing()) repaint(); }).start();
    }

    // called from Inventory when fish is selected in cook mode
    public static void startMinigame(String fishName) {
        if (instance == null) return;
        instance.fishBeingCooked = fishName;
        instance.health       = 0.33f; // Starts at 1/3rd full
        instance.circleActive = false;
        instance.minigame     = true;
        isCooking             = true;
        instance.menuButton.setEnabled(false);
        instance.arrowRight.setEnabled(false);
        instance.cancelButton.setVisible(true);
        instance.spawnCircle();
        instance.startMinigameTimer();
    }

    // keep playCookGif for compatibility — now routes to minigame
    public static void playCookGif(String fishName) {
        startMinigame(fishName);
    }

    private void startMinigameTimer() {
        if (minigameTimer != null) minigameTimer.stop();
        minigameTimer = new Timer(16, e -> {
            if (!minigame) { ((Timer) e.getSource()).stop(); return; }
            
            // --- NEW: BAR DEPLETION LOGIC ---
            // Drains roughly 6% per second at 60 frames per second
            health -= 0.001f; 
            if (health <= 0f) {
                endMinigame(false); // Burnt/Failed if the bar hits 0
                return;
            }
            
            if (circleActive) {
                outerRadius -= SHRINK_SPEED;
                if (outerRadius <= CIRCLE_RADIUS) {
                    // missed — outer ring passed center
                    applyHit("Miss");
                }
            }
            repaint();
        });
        minigameTimer.start();
    }

    private void spawnCircle() {
        // spawn within grill zone with padding
        int pad = SHRINK_START + 10;
        circleX     = pad + (int)(Math.random() * (grillZone.width  - pad * 2)) + grillZone.x;
        circleY     = pad + (int)(Math.random() * (grillZone.height - pad * 2)) + grillZone.y;
        outerRadius = SHRINK_START;
        circleActive = true;
    }

    private void handleMinigameClick() {
        if (!circleActive) return;
        int diff = outerRadius - CIRCLE_RADIUS;
        if (diff <= PERFECT_WINDOW) {
            applyHit("Perfect!");
        } else if (diff <= GOOD_WINDOW) {
            applyHit("Good");
        } else {
            applyHit("Bad");
        }
    }

    private void applyHit(String result) {
            circleActive = false;
            hitResult    = result;
            hitEndTime   = System.currentTimeMillis() + 800;

            switch (result) {
                case "Perfect!": health += 0.20f; hitColor = Color.CYAN;   break;
                case "Good":     health += 0.10f; hitColor = Color.GREEN;  break;
                case "Bad":      health -= 0.08f; hitColor = Color.RED;    break;
                case "Miss":     health -= 0.15f; hitColor = Color.ORANGE; break;
            }

            health = Math.max(0f, Math.min(1f, health));

            if (health <= 0f) {
                endMinigame(false); // burnt
                return;
            }

            if (health >= 1.0f) {
                endMinigame(true); // perfectly cooked
                return;
            }

            // spawn next circle after short delay
            new Timer(900, e -> { spawnCircle(); ((Timer) e.getSource()).stop(); }).start();
        }

    private void endMinigame(boolean success) {
        minigame  = false;
        isCooking = false;
        if (minigameTimer != null) minigameTimer.stop();
        menuButton.setEnabled(true);
        arrowRight.setEnabled(true);
        circleActive = false;
        instance.cancelButton.setVisible(false);

        if (success) {
            String cooked = "Cooked " + fishBeingCooked.replace("Cut ", "");
            Inventory.addItem(cooked);
            showPopup("Added: " + cooked);
        } else {
            showPopup("Burnt! Fish wasted.");
        }
        repaint();
    }

    private void showPopup(String text) {
        popupText    = text;
        popupEndTime = System.currentTimeMillis() + 2000;
    }

    private void loadImages() {
        try {
            grillGif     = new ImageIcon(getClass().getResource("/images/grill.gif"));
            grillSelected = new ImageIcon(getClass().getResource("/images/grillSelected.png")).getImage();
            cookingGif   = new ImageIcon(getClass().getResource("/images/cooking.gif"));
        } catch (Exception e) {
            System.out.println("Image load error: " + e.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // background
            g2.drawImage(grillGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            grillGif.setImageObserver(this);

            if (!minigame) {
                if (hovering) g2.drawImage(grillSelected, 0, 0, getWidth(), getHeight(), this);
                if (cooking)  { g2.drawImage(cookingGif.getImage(), 0, 0, getWidth(), getHeight(), this); cookingGif.setImageObserver(this); }
            }

            // ===== MINIGAME DRAWING =====
            if (minigame) {
                // dim background slightly
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // active circle
                if (circleActive) {
                    // outer shrinking ring
                    g2.setColor(new Color(255, 220, 80, 200));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(circleX - outerRadius, circleY - outerRadius, outerRadius * 2, outerRadius * 2);

                    // inner target circle
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.fillOval(circleX - CIRCLE_RADIUS, circleY - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(circleX - CIRCLE_RADIUS, circleY - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
                }

                // hit result flash
                if (System.currentTimeMillis() < hitEndTime && !hitResult.isEmpty()) {
                    g2.setColor(hitColor);
                    g2.setFont(new Font("Arial", Font.BOLD, 22));
                    g2.drawString(hitResult, circleX - 30, circleY - CIRCLE_RADIUS - 10);
                }

                // ===== COOKING PROGRESS BAR =====
                g2.setStroke(new BasicStroke(1));
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);

                // Solid color fill
                g2.setColor(new Color(80, 200, 80)); 
                g2.fillRoundRect(BAR_X + 2, BAR_Y + 2, (int)((BAR_W - 4) * health), BAR_H - 4, 10, 10);
                
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);

                // Bar label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("Cook", BAR_X - 40, BAR_Y + 26);
            }

            // popup
            if (System.currentTimeMillis() < popupEndTime) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(450, 650, 450, 50, 20, 20);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString(popupText, 470, 680);
            }
        }
    }
}