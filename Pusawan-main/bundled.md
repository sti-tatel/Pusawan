// === AudioPlayer.java ===
  package Pusawan;
  import javax.sound.sampled.*;
  public class AudioPlayer {
      private static Clip clip;
      private static String currentTrack = "";
        public static void playMusic(String filename) {
            stopMusic();
            currentTrack = filename;
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    AudioPlayer.class.getResource("/audio/" + filename));
              clip = AudioSystem.getClip();
              clip.open(audioStream);
              clip.loop(Clip.LOOP_CONTINUOUSLY);
              clip.start();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
      public static void stopMusic() {
          if (clip != null) {
              clip.stop();
          }
      }
        public static boolean isPlaying(String filename) {
            return clip != null && clip.isRunning() && currentTrack.equals(filename);
        }
    public static void playSound(String filename) {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    AudioPlayer.class.getResource("/audio/" + filename));
                Clip sfxClip = AudioSystem.getClip();
                sfxClip.open(audioStream);
                sfxClip.start(); 
            } catch (Exception e) {
                System.err.println("Could not play sound: " + filename);
                e.printStackTrace();
            }
        }
  }

// === Buttons.java ===
package Pusawan;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.*;
public class Buttons extends JPanel {
    public static final java.util.List<Runnable> dropdownClosers = new java.util.ArrayList<>();
    public static JButton inventoryButtonRef;
    private static final java.util.List<JButton> inventoryButtons = new java.util.ArrayList<>();
    public static void addClickSound(JButton button) {
        button.addActionListener(e -> AudioPlayer.playSound("click.wav"));
    }
    public static JButton toDropdown() {
        ImageIcon menuButtonImage = new ImageIcon(Buttons.class.getResource("/images/menu.png"));
        JButton menuButton = new JButton(menuButtonImage);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        addClickSound(menuButton);
        int itemHeight = 64;
        int totalItems = 4;
        int fullHeight = itemHeight * totalItems;
        JPanel inner = new JPanel(new GridLayout(totalItems, 1, 0, 0));
        inner.setBackground(new Color(0, 0, 0, 180));
        inner.setOpaque(true);
        JPanel clipPanel = new JPanel(null);
        clipPanel.setOpaque(false);
        inner.setBounds(0, 0, 64, fullHeight);
        clipPanel.add(inner);
        JButton mainMenu = new JButton(new ImageIcon(Buttons.class.getResource("/images/mainmenu.png")));
        JButton fishing  = new JButton(new ImageIcon(Buttons.class.getResource("/images/fishing.png")));
        JButton cutting  = new JButton(new ImageIcon(Buttons.class.getResource("/images/cutIconStatic.png")));
        JButton store    = new JButton(new ImageIcon(Buttons.class.getResource("/images/store.png")));
        addClickSound(mainMenu);
        addClickSound(fishing);
        addClickSound(cutting);
        addClickSound(store);
        Insets zero = new Insets(0, 0, 0, 0);
        for (JButton b : new JButton[]{mainMenu, fishing, cutting,  store }) {
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setMargin(zero);
        }
        inner.add(mainMenu);
        inner.add(fishing);
        inner.add(cutting);
        inner.add(store);
        boolean[] open = {false};
        Runnable close = () -> {
            if (!open[0]) return;
            open[0] = false;
            if (Inventory.instance != null) Inventory.instance.closeInventory();
            if (Shop.instance != null) Shop.instance.dispose();
            clipPanel.setVisible(false);
            java.awt.Container parent = clipPanel.getParent();
            if (parent instanceof JLayeredPane) {
                JLayeredPane lp = (JLayeredPane) parent;
                lp.remove(clipPanel);
                lp.repaint(clipPanel.getX(), clipPanel.getY(), 64, fullHeight);
            }
        };
        dropdownClosers.add(close);
        mainMenu.addActionListener(e -> { close.run(); Game.navigate(Game.START); });
        fishing.addActionListener(e ->  { close.run(); Game.navigate(Game.GAME); });
        cutting.addActionListener(e ->  { close.run(); Game.navigate(Game.KITCHEN); });
        store.addActionListener(e ->    { close.run(); Game.navigate(Game.STORE); });
        menuButton.addActionListener(e -> {
            if (open[0]) { close.run(); return; }
            JLayeredPane lp = Game.layeredPane();
            if (lp == null) return;
            Point p = SwingUtilities.convertPoint(menuButton.getParent(), menuButton.getLocation(), lp);
            inner.setBounds(0, 0, 64, fullHeight);
            clipPanel.setBounds(p.x, p.y + menuButton.getHeight(), 64, fullHeight);
            clipPanel.setVisible(true);
            lp.add(clipPanel, JLayeredPane.POPUP_LAYER);
            lp.revalidate();
            lp.repaint(clipPanel.getX(), clipPanel.getY(), 64, fullHeight);
            open[0] = true;
        });
        return menuButton;
    }
    public static JButton toStart(Runnable action) {
        ImageIcon startButtonImage = new ImageIcon(Buttons.class.getResource("/images/startbutton.png"));
        JButton startButton = new JButton(startButtonImage);
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        addClickSound(startButton);
        startButton.setSize(startButtonImage.getIconWidth(), startButtonImage.getIconHeight());
        startButton.addActionListener(e -> action.run());
        return startButton;
    }
    public static JButton toMainMenu() {
        ImageIcon menuButtonImage = new ImageIcon(Buttons.class.getResource("/images/mainmenu.png"));
        JButton menuButton = new JButton(menuButtonImage);
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        addClickSound(menuButton);
        menuButton.addActionListener(e -> Game.navigate(Game.START));
        return menuButton;
    }
    public static JButton toInventory() {
        ImageIcon closedIcon = new ImageIcon(Buttons.class.getResource("/images/inventory.png"));
        ImageIcon openedIcon = new ImageIcon(Buttons.class.getResource("/images/inventoryOpened.png"));
        JButton inventoryButton = new JButton(closedIcon);
        inventoryButton.setBorderPainted(false);
        inventoryButton.setContentAreaFilled(false);
        inventoryButton.setFocusPainted(false);
        addClickSound(inventoryButton);
        inventoryButton.addActionListener(e -> {
            Inventory.toggleInventory();
            inventoryButton.setIcon(Inventory.instance != null ? openedIcon : closedIcon);
        });
        inventoryButtonRef = inventoryButton;
        inventoryButtons.add(inventoryButton);
        return inventoryButton;
    }
    public static void updateInventoryIcon() {
        if (inventoryButtons.isEmpty()) return;
        ImageIcon closedIcon = new ImageIcon(Buttons.class.getResource("/images/inventory.png"));
        ImageIcon openedIcon = new ImageIcon(Buttons.class.getResource("/images/inventoryOpened.png"));
        for (JButton button : inventoryButtons) {
            button.setIcon(Inventory.instance != null ? openedIcon : closedIcon);
        }
    }
    public static void clearInventoryButtons() {
        inventoryButtons.removeIf(b -> !b.isDisplayable());
        inventoryButtonRef = inventoryButtons.isEmpty() ? null : inventoryButtons.get(inventoryButtons.size() - 1);
    }
    public static JButton toCutting() {
        ImageIcon cuttingButtonImage = new ImageIcon(Buttons.class.getResource("/images/cutIconStatic.png"));
        ImageIcon cuttingButtonHoverImage = new ImageIcon(Buttons.class.getResource("/images/cutIconAnimated.gif"));
        JButton cuttingButton = new JButton(cuttingButtonImage);
        cuttingButton.setRolloverEnabled(true);
        cuttingButton.setRolloverIcon(cuttingButtonHoverImage);
        cuttingButton.setBorderPainted(false);
        cuttingButton.setContentAreaFilled(false);
        cuttingButton.setFocusPainted(false);
        addClickSound(cuttingButton);
        cuttingButton.addActionListener(e -> Game.navigate(Game.CUTTING));
        return cuttingButton;
    }
    public static JButton toCooking() {
        ImageIcon cookingButtonImage = new ImageIcon(Buttons.class.getResource("/images/cookIconStatic.png"));
        ImageIcon cookingButtonHoverImage = new ImageIcon(Buttons.class.getResource("/images/cookIconAnimated.gif"));
        JButton cookingButton = new JButton(cookingButtonImage);
        cookingButton.setRolloverEnabled(true);
        cookingButton.setRolloverIcon(cookingButtonHoverImage);
        cookingButton.setBorderPainted(false);
        cookingButton.setContentAreaFilled(false);
        cookingButton.setFocusPainted(false);
        addClickSound(cookingButton);
        cookingButton.addActionListener(e -> Game.navigate(Game.COOKING));
        return cookingButton;
    }
    public static JButton toStore() {
        ImageIcon storeButtonImage = new ImageIcon(Buttons.class.getResource("/images/store.png"));
        JButton storeButton = new JButton(storeButtonImage);
        storeButton.setBorderPainted(false);
        storeButton.setContentAreaFilled(false);
        storeButton.setFocusPainted(false);
        addClickSound(storeButton);
        storeButton.addActionListener(e -> Game.navigate(Game.STORE));
        return storeButton;
    }
    public static void closeAllDropdowns() {
        for (Runnable r : dropdownClosers) r.run();
    }
    public static JButton createInventoryButton() {
        JButton btn = Buttons.toInventory();
        btn.setBounds(84, 20, 64, 64); 
        return btn;
    }
}

// === Buy.java ===
package Pusawan;
import java.awt.*;
import javax.swing.*;
public class Buy extends JPanel {
    public ImageIcon storeBackgroundGif;
    private Image hover;
    private boolean hovering = false;
    private Rectangle storeZone = new Rectangle(530, 200, 480, 480);
    public Buy() {
        loadBackgroundImage();
        new javax.swing.Timer(24, e -> repaint()).start();
        BackgroundPanel storebackground = new BackgroundPanel();
        hover = new ImageIcon(getClass().getResource("/images/storeBackgroundSelected.png")).getImage();
        setLayout(new BorderLayout());
        add(storebackground, BorderLayout.CENTER);
        storebackground.setLayout(null);
        storebackground.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                hovering = storeZone.contains(e.getPoint()) && Shop.instance == null;
                repaint();
            }
        });
        storebackground.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Buttons.closeAllDropdowns();
                if (storeZone.contains(e.getPoint())) {
                    Shop.toggleShop("buy");
                } else {
                    Buttons.closeAllDropdowns();
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                    if (Shop.instance != null) Shop.instance.dispose();
                }
            }
        });
        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        storebackground.add(menuButton);
        storebackground.add(Buttons.createInventoryButton());
        JButton arrowRight = new JButton(new ImageIcon(getClass().getResource("/images/arrowRight.png")));
        arrowRight.setBounds(1266, 400, 64, 64);
        arrowRight.setBorderPainted(false);
        arrowRight.setContentAreaFilled(false);
        arrowRight.setFocusPainted(false);
        Buttons.addClickSound(arrowRight);
        arrowRight.addActionListener(e -> Game.navigate(Game.SELL));
        storebackground.add(arrowRight);
    }
    public void loadBackgroundImage() {
        storeBackgroundGif = new ImageIcon(getClass().getResource("/images/storeBackground.gif"));
    }
    class BackgroundPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(storeBackgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            if (hovering) g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// === Cooking.java ===
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
    private static final int TOTAL_CIRCLES  = 8;
    private static final int CIRCLE_RADIUS  = 30;
    private static final int SHRINK_START   = 100;  
    private static final int SHRINK_SPEED   = 3;   
    private static final int PERFECT_WINDOW = 14;  
    private static final int GOOD_WINDOW    = 30;  
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
    private int   circleX      = 0;
    private int   circleY      = 0;
    private int   outerRadius  = SHRINK_START;
    private boolean circleActive = false;
    private String hitResult   = "";  
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
        cancelButton.setBounds(640, 580, 100, 40); 
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
    public static void startMinigame(String fishName) {
        if (instance == null) return;
        instance.fishBeingCooked = fishName;
        instance.health       = 0.33f; 
        instance.circleActive = false;
        instance.minigame     = true;
        isCooking             = true;
        instance.menuButton.setEnabled(false);
        instance.arrowRight.setEnabled(false);
        instance.cancelButton.setVisible(true);
        instance.spawnCircle();
        instance.startMinigameTimer();
    }
    public static void playCookGif(String fishName) {
        startMinigame(fishName);
    }
    private void startMinigameTimer() {
        if (minigameTimer != null) minigameTimer.stop();
        minigameTimer = new Timer(16, e -> {
            if (!minigame) { ((Timer) e.getSource()).stop(); return; }
            health -= 0.001f; 
            if (health <= 0f) {
                endMinigame(false); 
                return;
            }
            if (circleActive) {
                outerRadius -= SHRINK_SPEED;
                if (outerRadius <= CIRCLE_RADIUS) {
                    applyHit("Miss");
                }
            }
            repaint();
        });
        minigameTimer.start();
    }
    private void spawnCircle() {
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
                endMinigame(false); 
                return;
            }
            if (health >= 1.0f) {
                endMinigame(true); 
                return;
            }
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
            g2.drawImage(grillGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            grillGif.setImageObserver(this);
            if (!minigame) {
                if (hovering) g2.drawImage(grillSelected, 0, 0, getWidth(), getHeight(), this);
                if (cooking)  { g2.drawImage(cookingGif.getImage(), 0, 0, getWidth(), getHeight(), this); cookingGif.setImageObserver(this); }
            }
            if (minigame) {
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (circleActive) {
                    g2.setColor(new Color(255, 220, 80, 200));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(circleX - outerRadius, circleY - outerRadius, outerRadius * 2, outerRadius * 2);
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.fillOval(circleX - CIRCLE_RADIUS, circleY - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(circleX - CIRCLE_RADIUS, circleY - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
                }
                if (System.currentTimeMillis() < hitEndTime && !hitResult.isEmpty()) {
                    g2.setColor(hitColor);
                    g2.setFont(new Font("Arial", Font.BOLD, 22));
                    g2.drawString(hitResult, circleX - 30, circleY - CIRCLE_RADIUS - 10);
                }
                g2.setStroke(new BasicStroke(1));
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);
                g2.setColor(new Color(80, 200, 80)); 
                g2.fillRoundRect(BAR_X + 2, BAR_Y + 2, (int)((BAR_W - 4) * health), BAR_H - 4, 10, 10);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                g2.drawString("Cook", BAR_X - 40, BAR_Y + 26);
            }
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

// === Cutting.java ===
package Pusawan;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
public class Cutting extends JPanel {
    private BufferedImage bg;
    private Image hover;
    private boolean hovering = false;
    private boolean cutting = false;
    private Rectangle knifeZone = new Rectangle(360, 205, 77, 669);
    private Rectangle boardZone = new Rectangle(528, 298, 550, 518);
    private String popupText = "";
    private long popupEndTime = 0;
    private Timer cutTimer;
    private static Cutting instance;
    private JButton menuButton;
    public static boolean isCutting = false;
    public static void playCutGif(String fishName) {
        if (instance == null)
            return;
        isCutting = true;
        instance.cutting = true;
        Buttons.closeAllDropdowns();
        instance.menuButton.setEnabled(false);
        instance.showPopup("Consumed: " + fishName + " → Added: Cut " + fishName);
        instance.repaint();
        new Timer(8000, e -> {
            isCutting = false;
            instance.cutting = false;
            instance.menuButton.setEnabled(true);
            instance.repaint();
            ((Timer) e.getSource()).stop();
        }).start();
    }
    public Cutting() {
        instance = this;
        loadImages();
        BackgroundPanel panel = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        panel.setLayout(null);
        menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        panel.add(menuButton);
        panel.add(Buttons.createInventoryButton());
        JButton arrowLeft = new JButton(new ImageIcon(getClass().getResource("/images/arrowLeft.png")));
        arrowLeft.setBounds(20, 400, 64, 64);
        arrowLeft.setBorderPainted(false);
        arrowLeft.setContentAreaFilled(false);
        arrowLeft.setFocusPainted(false);
        Buttons.addClickSound(arrowLeft);
        arrowLeft.addActionListener(e -> Game.navigate(Game.KITCHEN));
        panel.add(arrowLeft);
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                hovering = (knifeZone.contains(p) || boardZone.contains(p)) && Inventory.instance == null;
                repaint();
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                Buttons.closeAllDropdowns();
                if ((knifeZone.contains(p) || boardZone.contains(p)) && !Cutting.isCutting) {
                if (Inventory.getFirstFish() == null) return;
                Inventory.toggleWithMode("cut");
            } else {
                Buttons.closeAllDropdowns();
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
        new Timer(16, e -> { if (isShowing()) repaint(); }).start();
    }
    private void showPopup(String text) {
        popupText = text;
        popupEndTime = System.currentTimeMillis() + 2000;
    }
    private void loadImages() {
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/images/cuttingBoard.png"));
            hover = new ImageIcon(getClass().getResource("/images/cuttingBoardSelected.png")).getImage();
        } catch (Exception e) {
            System.out.println("Image load error: " + e.getMessage());
        }
    }
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            if (hovering) {
                g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
            }
            if (cutting) {
                Image gifFrame = new ImageIcon(getClass().getResource("/images/cut.gif")).getImage();
                g.drawImage(gifFrame, 0, 0, getWidth(), getHeight(), this);
            }
            if (System.currentTimeMillis() < popupEndTime) {
                g.setColor(new Color(0, 0, 0, 180));
                g.fillRoundRect(450, 650, 450, 50, 20, 20);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString(popupText, 470, 680);
            }
        }
    }
}

// === Fishing.java ===
package Pusawan;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;
public class Fishing extends JPanel {
    private boolean fishingInProgress = false;
    private boolean showFishResult    = false;
    private String  lastCaughtFish    = null;
    static  String  selectedBait      = "No Bait";
    private float playerX       = 0.5f;
    private float fishX         = 0.5f;
    private float fishVel       = 0f;
    private float catchProgress = 0.25f; 
    private float playerSize    = 0.15f; 
    private float fishSize      = 0.015f; 
    private int     driftDir          = 1;     
    private boolean isMouseDown       = false; 
    private long    fishMoveStartTime = 0;     
    private long    nextFishTime      = 0;     
    private static final int BAR_X = 375;  
    private static final int BAR_Y = 640;  
    private static final int BAR_W = 600;  
    private static final int BAR_H = 40;   
    private static final int PROG_Y = 690; 
    private static final int PROG_H = 14;  
    private JButton menuButton;
    private JButton inventoryButton;
    private JButton cancelButton;
    private BackgroundPanel background;
    public static void setSelectedBait(String bait) {
        selectedBait = bait;
    }
    public Fishing() {
        new javax.swing.Timer(16, e -> {
            if (!isShowing()) return;
            if (fishingInProgress) {
                float driftSpeed = 0.020f; 
                float pullSpeed  = 0.020f; 
                if (isMouseDown) {
                    playerX += -driftDir * pullSpeed; 
                } else {
                    playerX += driftDir * driftSpeed; 
                }
                playerX = Math.max(playerSize / 2, Math.min(1f - playerSize / 2, playerX));
                if (System.currentTimeMillis() > fishMoveStartTime) {
                    if (Math.random() < 0.05) { 
                        fishVel += (float)(Math.random() * 0.04 - 0.02); 
                    }
                    fishVel = Math.max(-0.03f, Math.min(0.03f, fishVel)); 
                    fishX  += fishVel;
                    if (fishX <= 0f) { fishX = 0f; fishVel = Math.abs(fishVel); }
                    if (fishX >= 1f) { fishX = 1f; fishVel = -Math.abs(fishVel); }
                }
                float pLeft  = playerX - playerSize / 2;
                float pRight = playerX + playerSize / 2;
                boolean overlap = fishX >= pLeft && fishX <= pRight;
                if (overlap) {
                    catchProgress = Math.min(1f, catchProgress + 0.015f);
                    if (catchProgress >= 1f) background.completeCatch();
                } else {
                    catchProgress = Math.max(0f, catchProgress - 0.005f);
                    if (catchProgress <= 0f) background.failCatch();
                }
            }
            repaint();
        }).start();
        background = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(background, BorderLayout.CENTER);
        background.setLayout(null);
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
        for (JButton b : new JButton[]{cancelButton, menuButton}) {
            b.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "none");
            b.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "none");
        }
    }
    private void cancelFishing() {
        fishingInProgress = false;
        isMouseDown       = false;
        nextFishTime      = System.currentTimeMillis() + 1000; 
        catchProgress     = 0.25f;
        playerX           = 0.5f;
        fishX             = 0.5f;
        fishVel           = 0f;
        menuButton.setVisible(true);
        inventoryButton.setVisible(true);
        cancelButton.setVisible(false);
        repaint();
    }
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
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    if (!fishingInProgress) {
                        boolean over = pierHitbox.contains(e.getPoint()) && Inventory.instance == null;
                        if (over != pierHovered) { pierHovered = over; repaint(); }
                    }
                }
            });
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    isMouseDown = true;
                    if (fishingInProgress) return;
                    if (new Rectangle(5, 625, 180, 70).contains(e.getPoint())) {
                        Inventory.toggleWithMode("bait");
                        return;
                    }
                    if (pierHitbox.contains(e.getPoint())) {
                        if (System.currentTimeMillis() < nextFishTime) return; 
                        startFishing();
                        return;
                    }
                    Buttons.closeAllDropdowns();
                    if (Inventory.instance != null) { Inventory.instance.closeInventory(); }
                    if (Shop.instance != null) { Shop.instance.dispose(); Shop.instance = null; }
                }
                public void mouseReleased(MouseEvent e) {
                    isMouseDown = false;
                }
            });
        }
        void completeCatch() {
            fishingInProgress = false;
            showFishResult    = true;
            nextFishTime      = System.currentTimeMillis() + 1000; 
            catchProgress     = 0.25f;
            menuButton.setVisible(true);
            inventoryButton.setVisible(true);
            cancelButton.setVisible(false);
            Inventory.addItem(lastCaughtFish);
            if (!isJunk(lastCaughtFish)) PlayerData.incrementCaught(lastCaughtFish);
            if (!selectedBait.equals("No Bait")) {
                Inventory.removeItem(selectedBait);
                if (Inventory.items.getOrDefault(selectedBait, 0) <= 0) {
                    selectedBait = "No Bait";
                }
            }
            new Timer(2000, ev -> {
                showFishResult = false;
                repaint();
                ((Timer) ev.getSource()).stop();
            }).start();
        }
        void failCatch() {
            fishingInProgress = false;
            showFishResult    = true;
            nextFishTime      = System.currentTimeMillis() + 1000; 
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
        }
        private void drawMinigame(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);
            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawRoundRect(BAR_X, BAR_Y, BAR_W, BAR_H, 12, 12);
            int pPx = (int)(playerX * BAR_W) + BAR_X;
            int pHalfW = (int)(playerSize * BAR_W / 2);
            int pLeft  = pPx - pHalfW;
            g2.setColor(new Color(210, 210, 210)); 
            g2.fillRoundRect(pLeft, BAR_Y + 2, pHalfW * 2, BAR_H - 4, 8, 8);
            g2.setColor(new Color(150, 150, 150)); 
            g2.drawRoundRect(pLeft, BAR_Y + 2, pHalfW * 2, BAR_H - 4, 8, 8);
            int fPx = (int)(fishX * BAR_W) + BAR_X;
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(5));
            g2.drawLine(fPx, BAR_Y + 2, fPx, BAR_Y + BAR_H - 2);
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawLine(fPx, BAR_Y + 2, fPx, BAR_Y + BAR_H - 2);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Fish", fPx - 8, BAR_Y - 6);
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(BAR_X, PROG_Y, BAR_W, PROG_H, 8, 8);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(BAR_X, PROG_Y, (int)(catchProgress * BAR_W), PROG_H, 8, 8);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawRoundRect(BAR_X, PROG_Y, BAR_W, PROG_H, 8, 8);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String hint = "Hold Click or Space to pull!";
            int tw = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, BAR_X + BAR_W / 2 - tw / 2, BAR_Y - 18);
        }
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
        private void startFishing() {
            menuButton.setVisible(false);
            inventoryButton.setVisible(false);
            cancelButton.setVisible(true);
            showFishResult    = false;
            lastCaughtFish    = pickCatch();
            fishingInProgress = true;
            catchProgress     = 0.25f; 
            playerX           = 0.5f;
            fishX             = 0.5f;
            fishVel           = 0f;
            isMouseDown       = false;
            driftDir = Math.random() < 0.5 ? 1 : -1;
            fishMoveStartTime = System.currentTimeMillis() + (long)(Math.random() * 200 + 300);
            boolean junk = isJunk(lastCaughtFish);
            playerSize = junk ? 0.30f : 0.30f; 
            if ("bambooRod".equals(PlayerData.equippedRod)) playerSize += 0.04f;
            if ("hotdogRod".equals(PlayerData.equippedRod)) playerSize += 0.08f;
            repaint();
        }
        private boolean isJunk(String name) {
            return name.equals("Sandal") || name.equals("Shoe") || name.equals("Plastic Wrapper");
        }
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

// === Game.java ===
package Pusawan;
import java.awt.*;
import javax.swing.*;
public class Game extends JFrame {
    public static final String START = "start";
    public static final String KITCHEN = "kitchen";
    public static final String GAME = "game";
    public static final String CUTTING = "cutting";
    public static final String COOKING = "cooking";
    public static final String STORE = "store";
    public static final String SELL = "sell";
    private static Game instance;
    private CardLayout cardLayout;
    private JPanel container;
    public Game() {
        instance = this;
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setCursor(playerCursor.getCustomCursor());
        setResizable(false);
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        Start start = new Start();
        start.setName(START);
        container.add(start, START);
        Inventory.fillDebug();
        add(container);
        setVisible(true);
        navigate(START);
    }
    public static void navigate(String screen) {
        Buttons.closeAllDropdowns();
        if (Inventory.instance != null) { Inventory.instance.closeInventory(); }
        if (Shop.instance != null) { Shop.instance.dispose(); Shop.instance = null; }
        hideOverlay();
        if (instance.container.getLayout() instanceof CardLayout) {
            boolean found = false;
            for (java.awt.Component c : instance.container.getComponents()) {
                if (c.getName() != null && c.getName().equals(screen)) { found = true; break; }
            }
            if (!found) {
                JPanel panel = null;
                switch (screen) {
                    case GAME:    panel = new Fishing(); break;
                    case CUTTING: panel = new Cutting(); break;
                    case COOKING: panel = new Cooking(); break;
                    case STORE:   panel = new Buy();     break;
                    case SELL:    panel = new Sell();    break;
                    case KITCHEN: panel = new Kitchen(); break;
                }
                if (panel != null) {
                    panel.setName(screen);
                    instance.container.add(panel, screen);
                }
            }
        }
        instance.cardLayout.show(instance.container, screen);
        if (screen.equals(START)) {
            AudioPlayer.playMusic("tropicalBreeze.wav");
        } else if (screen.equals(GAME) && !AudioPlayer.isPlaying("morningMood.wav")) {
            AudioPlayer.playMusic("morningMood.wav");
        }
    }
    public static JLayeredPane layeredPane() {
        return instance.getLayeredPane();
    }
    private static JPanel overlay;
    public static void showOverlay() {
        if (overlay == null) {
            overlay = new JPanel() {
                protected void paintComponent(Graphics g) {
                    g.setColor(new Color(0, 0, 0, 120));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            overlay.setOpaque(false);
            overlay.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (Shop.instance != null) Shop.instance.dispose();
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                }
            });
            instance.getLayeredPane().add(overlay, JLayeredPane.MODAL_LAYER);
        }
        overlay.setBounds(0, 0, instance.getWidth(), instance.getHeight());
        overlay.setVisible(true);
        overlay.repaint();
    }
    public static void hideOverlay() {
        if (overlay != null) overlay.setVisible(false);
    }
    public static void hideOverlayIfNoModals() {
        if (Inventory.instance == null && Shop.instance == null) {
            hideOverlay();
        }
    }
}

// === Inventory.java ===
package Pusawan;
import java.awt.*;
import javax.swing.*;
public class Inventory extends JPanel {
    private JPanel bg;
    static Inventory instance;
    private String mode = "inventory";
    private boolean isItemsTab = false;
    private boolean isBaitTab = false;
    private JLabel descName;
    private JTextArea descText;
    private JLabel itemDisplayLabel;
    private JButton equipBtn;
    private JButton unequipBtn;
    private JButton deleteBtn;
    private static final java.util.Set<String> FISH_NAMES = new java.util.HashSet<>(java.util.Arrays.asList("Carp", "Catfish", "Bass", "Perch"));
    private static final java.util.Map<String, String> descriptions = new java.util.LinkedHashMap<>();
    static {
        descriptions.put("Bass", "A strong freshwater fish.\nSells well raw.");
        descriptions.put("Carp", "A common river fish.\nNot the tastiest.");
        descriptions.put("Catfish", "Bottom-dweller.\nCooks up nicely.");
        descriptions.put("Perch", "Small but plentiful.\nGood for a quick meal.");
        descriptions.put("Cut Bass", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cut Carp", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cut Catfish", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cut Perch", "Cleaned and prepped.\nReady for the grill.");
        descriptions.put("Cooked Bass", "Grilled to perfection.\nFetches a good price.");
        descriptions.put("Cooked Carp", "Tender and flaky.\nSmells amazing.");
        descriptions.put("Cooked Catfish", "Crispy on the outside.\nJuicy inside.");
        descriptions.put("Cooked Perch", "Simple but satisfying.\nA fisherman's staple.");
        descriptions.put("Sandal", "Someone lost this.\nNot worth much.");
        descriptions.put("Shoe", "Waterlogged and worn.\nStill sellable.");
        descriptions.put("Plastic Wrapper", "Trash from the river.\nA few coins maybe.");
        descriptions.put("Worm Bait", "Wriggly and effective.\nAttracts common fish.");
        descriptions.put("Insect Bait", "Buzzing with potential.\nGood for mid-tier fish.");
        descriptions.put("Fish Bait", "Bait made from fish.\nAttracts bigger catches.");
        descriptions.put("Magic Bait", "Mysterious and powerful.\nWhat will you catch?");
    }
    public static java.util.Map<String, Integer> items = new java.util.LinkedHashMap<>();
    public static void fillDebug() {
        String[] allItems = {"Perch", "Carp", "Catfish", "Bass", "Cut Perch", "Cut Carp", "Cut Catfish", "Cut Bass",
            "Cooked Perch", "Cooked Carp", "Cooked Catfish", "Cooked Bass", "Sandal", "Shoe", "Plastic Wrapper",
            "Worm Bait", "Insect Bait", "Fish Bait", "Magic Bait", "Bamboo Rod"};
        for (String item : allItems) {
            items.put(item, 99);
        }
    }
    public Inventory(String mode) {
        this.mode = mode;
        if (mode.equals("bait")) {
            isBaitTab = true;
        }
        setLayout(null);
        setOpaque(false);
        setBounds(0, 0, 1058, 580);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1058, 580);
        add(layeredPane);
        JPanel bgPanel = new JPanel() {
            private Image bgImg = new ImageIcon(getClass().getResource("/images/inventorybg.png")).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setOpaque(true);
        bgPanel.setBounds(60, 0, 998, 580);
        layeredPane.add(bgPanel, Integer.valueOf(-1));
        JButton closeBtn = new JButton();
        closeBtn.setBounds(1005, 24, 53, 53);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setOpaque(false);
        closeBtn.addActionListener(e -> {
            AudioPlayer.playSound("click.wav");
            closeInventory();
        });
        layeredPane.add(closeBtn, JLayeredPane.MODAL_LAYER);
        int tabX = 0;    
        int tabY = 150;
        int tabGap = 5;
        JButton fishTab = makeImageTab("/images/fishTab.png", tabX, tabY);
        JButton baitTab = makeImageTab("/images/baitTab.png", tabX, tabY + 64 + tabGap);
        JButton itemsTab = makeImageTab("/images/itemsTab.png", tabX, tabY + (64 + tabGap) * 2);
        Buttons.addClickSound(fishTab);
        Buttons.addClickSound(baitTab);
        Buttons.addClickSound(itemsTab);
        if (mode.equals("cut") || mode.equals("cook")) {
            itemsTab.setEnabled(false);
            baitTab.setEnabled(false);
        }
        if (mode.equals("bait")) {
            fishTab.setEnabled(false);
            itemsTab.setEnabled(false);
        }
        if (mode.equals("bait")) {
            baitTab.setOpaque(true);
            baitTab.setBackground(new Color(255, 255, 255, 80));
        }
        fishTab.addActionListener(e -> {
            if (!isItemsTab && !isBaitTab) {
                return;
            }
            isItemsTab = false;
            isBaitTab = false;
            refreshItems();
        });
        baitTab.addActionListener(e -> {
            if (isBaitTab) {
                return;
            }
            isBaitTab = true;
            isItemsTab = false;
            refreshItems();
        });
        itemsTab.addActionListener(e -> {
            if (isItemsTab) {
                return;
            }
            isItemsTab = true;
            isBaitTab = false;
            refreshItems();
        });
        layeredPane.add(fishTab, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(baitTab, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(itemsTab, JLayeredPane.PALETTE_LAYER);
        bg = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        bg.setOpaque(false);
        bg.setBorder(BorderFactory.createEmptyBorder(9, 9, 10, 10));
        bg.setPreferredSize(new Dimension(820, 900));
        JPanel bgWrapper = new JPanel(new BorderLayout());
        bgWrapper.setOpaque(false);
        bgWrapper.add(bg, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(bgWrapper);
        scrollPane.setBounds(93, 100, 840, 440);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        JPanel descPanel = new JPanel(null);
        descPanel.setBounds(860, 265, 172, 280);
        descPanel.setOpaque(false);
        descName = new JLabel("", SwingConstants.CENTER);
        descName.setBounds(5, 10, 162, 30);
        descName.setFont(new Font("Arial", Font.BOLD, 20));
        descPanel.add(descName);
        descText = new JTextArea("Select an item");
        descText.setBounds(5, 50, 162, 113);
        descText.setForeground(new Color(0, 0, 0));
        descText.setFont(new Font("Arial", Font.PLAIN, 15));
        descText.setOpaque(false);
        descText.setEditable(false);
        descText.setLineWrap(true);
        descText.setWrapStyleWord(true);
        descText.setFocusable(false);
        descPanel.add(descText);
        Image equipImg = new ImageIcon(getClass().getResource("/images/equip.png")).getImage().getScaledInstance(86, 32, Image.SCALE_SMOOTH);
        equipBtn = new JButton(new ImageIcon(equipImg));
        equipBtn.setBounds(43, 162, 86, 32);
        equipBtn.setBorderPainted(false);
        equipBtn.setContentAreaFilled(false);
        equipBtn.setFocusPainted(false);
        equipBtn.setMargin(new Insets(0, 0, 0, 0));
        equipBtn.setHorizontalAlignment(SwingConstants.CENTER);
        equipBtn.setVerticalAlignment(SwingConstants.CENTER);        
        equipBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        equipBtn.setVerticalTextPosition(SwingConstants.CENTER);        
        equipBtn.setRolloverEnabled(true);
        ImageIcon equipHover = new ImageIcon(new ImageIcon(getClass().getResource("/images/equipHovered.png")).getImage().getScaledInstance(86, 32, Image.SCALE_SMOOTH));
        equipBtn.setRolloverIcon(equipHover);
        equipBtn.setVisible(false);
        descPanel.add(equipBtn);
        Image unequipImg = new ImageIcon(getClass().getResource("/images/unequip.png")).getImage().getScaledInstance(89, 32, Image.SCALE_SMOOTH);
        unequipBtn = new JButton(new ImageIcon(unequipImg));        
        unequipBtn.setBounds(41, 162, 89, 32);
        unequipBtn.setBorderPainted(false);
        unequipBtn.setContentAreaFilled(false);
        unequipBtn.setFocusPainted(false);
        unequipBtn.setMargin(new Insets(0, 0, 0, 0));
        unequipBtn.setHorizontalAlignment(SwingConstants.CENTER);
        unequipBtn.setVerticalAlignment(SwingConstants.CENTER);    
        unequipBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        unequipBtn.setVerticalTextPosition(SwingConstants.CENTER);
        unequipBtn.setRolloverEnabled(true);
        ImageIcon unequipHover = new ImageIcon(new ImageIcon(getClass().getResource("/images/unequipHovered.png")).getImage().getScaledInstance(89, 32, Image.SCALE_SMOOTH));
        unequipBtn.setRolloverIcon(unequipHover);
        unequipBtn.setVisible(false);
        descPanel.add(unequipBtn);
        Image deleteImg = new ImageIcon(getClass().getResource("/images/delete.png")).getImage().getScaledInstance(89, 32, Image.SCALE_SMOOTH);
        deleteBtn = new JButton(new ImageIcon(deleteImg));
        deleteBtn.setBounds(41, 200, 89, 32);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setHorizontalAlignment(SwingConstants.CENTER);
        deleteBtn.setVerticalAlignment(SwingConstants.CENTER);
        deleteBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        deleteBtn.setVerticalTextPosition(SwingConstants.CENTER);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setMargin(new Insets(0, 0, 0, 0));
        deleteBtn.setRolloverEnabled(true);
        ImageIcon deleteHover = new ImageIcon(new ImageIcon(getClass().getResource("/images/deleteHovered.png")).getImage().getScaledInstance(89, 32, Image.SCALE_SMOOTH));
        deleteBtn.setRolloverIcon(deleteHover);
        deleteBtn.setVisible(false);
        descPanel.add(deleteBtn);
        layeredPane.add(descPanel, JLayeredPane.DEFAULT_LAYER);
        JLayeredPane itemDisplay = new JLayeredPane();
        itemDisplay.setBounds(860, 95, 172, 172);
        JLabel bgLabel = new JLabel(new ImageIcon(getClass().getResource("/images/selected.png")));
        bgLabel.setBounds(0, 0, 172, 172);
        itemDisplay.add(bgLabel, JLayeredPane.DEFAULT_LAYER);
        itemDisplayLabel = new JLabel();
        itemDisplayLabel.setBounds(10, 10, 152, 152);
        itemDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        itemDisplayLabel.setVerticalAlignment(SwingConstants.CENTER);
        itemDisplay.add(itemDisplayLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(itemDisplay, JLayeredPane.PALETTE_LAYER);
        refreshItems();
    }
    void closeInventory() {
        JLayeredPane lp = Game.layeredPane();
        if (lp != null) {
            lp.remove(this);
        }
        instance = null;
        Game.hideOverlayIfNoModals();
        Buttons.updateInventoryIcon();
        if (lp != null) {
            lp.revalidate();
            lp.repaint();
        }
    }
    private JButton makeImageTab(String imagePath, int x, int y) {
        JButton btn = new JButton(new ImageIcon(getClass().getResource(imagePath)));
        btn.setBounds(x, y, 64, 64);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        return btn;
    }
    private void refreshItems() {
        bg.removeAll();
        int i = 0;
        for (java.util.Map.Entry<String, Integer> entry : items.entrySet()) {
            if (i >= 40) {
                break;
            }
            final String itemName = entry.getKey();
            int count = entry.getValue();
            boolean isFish = FISH_NAMES.contains(itemName)
                    || itemName.startsWith("Cut ")
                    || itemName.startsWith("Cooked ");
            if (mode.equals("bait")) {
                if (!itemName.endsWith("Bait")) {
                    continue;
                }
            } else if (mode.equals("cut")) {
                if (!FISH_NAMES.contains(itemName)) {
                    continue;
                }
            } else if (mode.equals("cook")) {
                if (!itemName.startsWith("Cut ")) {
                    continue;
                }
            } else {
                if (isBaitTab && !itemName.endsWith("Bait")) {
                    continue;
                }
                if (isItemsTab && (isFish || itemName.endsWith("Bait"))) {
                    continue;
                }
                if (!isItemsTab && !isBaitTab && (!isFish || itemName.endsWith("Bait"))) {
                    continue;
                }
            }
            JPanel slot = new JPanel(new BorderLayout()) {
                private Image slotImg = new ImageIcon(getClass().getResource("/images/slotbg.png")).getImage();
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            slot.setOpaque(false);
            slot.setPreferredSize(new Dimension(80, 80));
            slot.setBorder(null);
            String imagePath;
            if (itemName.startsWith("Cut ")) {
                imagePath = "/images/cut_" + itemName.replace("Cut ", "").toLowerCase() + ".png";
            } else if (itemName.startsWith("Cooked ")) {
                imagePath = "/images/cooked_" + itemName.replace("Cooked ", "").toLowerCase() + ".png";
            } else if (itemName.endsWith("Bait")) {
                switch (itemName) {
                    case "Worm Bait":   imagePath = "/images/wormBait.png";   break;
                    case "Insect Bait": imagePath = "/images/insectBait.png"; break;
                    case "Fish Bait":   imagePath = "/images/fishBait.png";   break;
                    case "Magic Bait":  imagePath = "/images/magicBait.png";  break;
                    default: imagePath = "/images/" + itemName.replace(" ", "") + ".png"; break;
                }
            } else {
                imagePath = "/images/" + itemName.toLowerCase().replace(" ", "") + ".png";
            }
            final String finalImagePath = imagePath;
            slot.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    slot.setBackground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    slot.setBackground(new Color(200, 180, 120));
                }
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    slot.setBackground(Color.YELLOW);
                    descName.setText(itemName);
                    java.net.URL dispUrl = getClass().getResource(finalImagePath);
                    if (dispUrl != null) {
                        Image dispImg = new ImageIcon(dispUrl).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        itemDisplayLabel.setIcon(new ImageIcon(dispImg));
                    } else {
                        itemDisplayLabel.setIcon(null);
                    }
                    String desc = descriptions.getOrDefault(itemName, "No description.");
                    descText.setText(desc);
                    if (mode.equals("inventory")) {
                        boolean isRod = itemName.endsWith("Rod");
                        boolean isEquipped = itemName.equals(PlayerData.equippedRod);
                        equipBtn.setVisible(isRod && !isEquipped);
                        unequipBtn.setVisible(isRod && isEquipped);
                        deleteBtn.setVisible(true);
                        for (java.awt.event.ActionListener al : equipBtn.getActionListeners()) equipBtn.removeActionListener(al);
                        for (java.awt.event.ActionListener al : unequipBtn.getActionListeners()) unequipBtn.removeActionListener(al);
                        for (java.awt.event.ActionListener al : deleteBtn.getActionListeners()) deleteBtn.removeActionListener(al);
                        equipBtn.addActionListener(a -> {
                            AudioPlayer.playSound("click.wav");
                            PlayerData.equippedRod = itemName;
                            equipBtn.setVisible(false);
                            unequipBtn.setVisible(true);
                            refreshItems();
                        });
                        unequipBtn.addActionListener(a -> {
                            AudioPlayer.playSound("click.wav");
                            PlayerData.equippedRod = "None";
                            unequipBtn.setVisible(false);
                            equipBtn.setVisible(true);
                            refreshItems();
                        });
                        deleteBtn.addActionListener(a -> {
                            AudioPlayer.playSound("click.wav");
                            Inventory.removeItem(itemName);
                            refreshItems(); 
                            if (!items.containsKey(itemName) || items.get(itemName) <= 0) {
                                descText.setText("Item deleted.");
                                itemDisplayLabel.setIcon(null);
                                equipBtn.setVisible(false);
                                unequipBtn.setVisible(false);
                                deleteBtn.setVisible(false);
                            }
                        });
                    }
                    if (mode.equals("cut")) {
                        Inventory.removeItem(itemName);
                        Inventory.addItem("Cut " + itemName);
                        Cutting.playCutGif(itemName);
                        closeInventory();
                    }
                    if (mode.equals("cook")) {
                        Inventory.removeItem(itemName);
                        closeInventory();
                        Cooking.playCookGif(itemName);
                    }
                    if (mode.equals("bait")) {
                        if (Fishing.selectedBait.equals(itemName)) {
                            Fishing.setSelectedBait("No Bait");
                        } else {
                            Fishing.setSelectedBait(itemName);
                        }
                        closeInventory();
                    }
                }
            });
            java.net.URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl == null) {
                imgUrl = getClass().getResource("/images/" + itemName.replace(" ", "") + ".png");
            }
            if (imgUrl == null && itemName.endsWith("Bait")) {
                imgUrl = getClass().getResource("/images/wormBait.png");
            }
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image scaled = icon.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                JLabel iconLabel = new JLabel(new ImageIcon(scaled));
                slot.add(iconLabel, BorderLayout.CENTER);
            } else {
                slot.add(new JLabel(itemName, SwingConstants.CENTER), BorderLayout.CENTER);
            }
            if (count > 1) {
                JLabel countLabel = new JLabel("x" + count);
                countLabel.setFont(new Font("Arial", Font.BOLD, 12));
                countLabel.setForeground(Color.WHITE);
                countLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                slot.add(countLabel, BorderLayout.NORTH);
            }
            if (itemName.equals(PlayerData.equippedRod)) {
                JLabel equippedLabel = new JLabel("Equipped");
                equippedLabel.setFont(new Font("Arial", Font.BOLD, 10));
                equippedLabel.setForeground(Color.GREEN);
                equippedLabel.setHorizontalAlignment(SwingConstants.CENTER);
                slot.add(equippedLabel, BorderLayout.NORTH);
            }
            bg.add(slot);
            i++;
        }
        while (i < 40) {
            JPanel slot = new JPanel() {
                private Image slotImg = new ImageIcon(getClass().getResource("/images/slotbg.png")).getImage();
                @Override
                protected void paintComponent(Graphics g) {
                    g.drawImage(slotImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            slot.setOpaque(false);
            slot.setPreferredSize(new Dimension(80, 80));
            slot.setBorder(null);
            bg.add(slot);
            i++;
        }
        bg.revalidate();
        bg.repaint();
    }
    public static void addItem(String itemName) {
        items.put(itemName, items.getOrDefault(itemName, 0) + 1);
        if (instance != null) {
            instance.refreshItems();
        }
    }
    public static void removeItem(String itemName) {
        if (!items.containsKey(itemName)) {
            return;
        }
        int count = items.get(itemName);
        if (count <= 1) {
            items.remove(itemName); 
        }else {
            items.put(itemName, count - 1);
        }
        if (instance != null) {
            instance.refreshItems();
        }
    }
    public static String getFirstFish() {
        for (String item : items.keySet()) {
            if (items.getOrDefault(item, 0) > 0 && FISH_NAMES.contains(item)) {
                return item;
            }
        }
        return null;
    }
    public static String getFirstCutFish() {
        for (String item : items.keySet()) {
            if (item.startsWith("Cut ") && items.getOrDefault(item, 0) > 0) {
                return item;
            }
        }
        return null;
    }
    public static void toggleInventory() {
        Buttons.closeAllDropdowns();
        if (instance != null) {
            instance.closeInventory();
            return;
        }
        instance = new Inventory("inventory");
        JLayeredPane lp = Game.layeredPane();
        int x = (1350 - 1058) / 2 - 60;
        int y = (750 - 580) / 2;
        instance.setBounds(x, y, 1058, 580);
        lp.add(instance, JLayeredPane.POPUP_LAYER);
        lp.revalidate();
        lp.repaint();
        Game.showOverlay();
        Buttons.updateInventoryIcon();
    }
    public static void toggleWithMode(String mode) {
        Buttons.closeAllDropdowns();
        if (instance != null) {
            instance.closeInventory();
            return;
        }
        instance = new Inventory(mode);
        JLayeredPane lp = Game.layeredPane();
        int x = (1350 - 1058) / 2 - 60;
        int y = (750 - 580) / 2;
        instance.setBounds(x, y, 1058, 580);
        lp.add(instance, JLayeredPane.POPUP_LAYER);
        lp.revalidate();
        lp.repaint();
        Game.showOverlay();
        Buttons.updateInventoryIcon();
    }
}

// === Kitchen.java ===
package Pusawan;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Kitchen extends JPanel {
    private ImageIcon animatedBackground;
    private final int LEFT_WIDTH = 365;
    private final int RIGHT_WIDTH = 410;
    private final int HITBOX_HEIGHT = 720;
    private boolean isLeftHovered = false;
    private boolean isRightHovered = false;
    public Kitchen() {
        animatedBackground = new ImageIcon(getClass().getResource("/images/kitchen.gif"));
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                isLeftHovered = new Rectangle(0, 0, LEFT_WIDTH, HITBOX_HEIGHT).contains(p);
                isRightHovered = new Rectangle(getWidth() - RIGHT_WIDTH, 0, RIGHT_WIDTH, HITBOX_HEIGHT).contains(p);
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (new Rectangle(0, 0, LEFT_WIDTH, HITBOX_HEIGHT).contains(p)) {
                    goToCooking(); 
                } else if (new Rectangle(getWidth() - RIGHT_WIDTH, 0, RIGHT_WIDTH, HITBOX_HEIGHT).contains(p)) {
                    goToCutting(); 
                }
            }
        });
        setLayout(null);
        JButton inventoryButton = Buttons.createInventoryButton();
        add(inventoryButton);
        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        add(menuButton);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (animatedBackground != null) {
            g2d.drawImage(animatedBackground.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
        if (isLeftHovered) {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.fillRect(0, 0, LEFT_WIDTH, HITBOX_HEIGHT);
        }
        if (isRightHovered) {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.fillRect(getWidth() - RIGHT_WIDTH, 0, RIGHT_WIDTH, HITBOX_HEIGHT);
        }
    }
    private void goToCooking() {
        Game.navigate(Game.COOKING);
    }
    private void goToCutting() {
        Game.navigate(Game.CUTTING);
    }
}

// === Main.java ===
package Pusawan;
import javax.swing.SwingUtilities;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Game();
        });
    }
}

// === PlayerData.java ===
package Pusawan;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
public class PlayerData {
    private static int money = 99999999;
    public static String equippedRod = null;
    public static int getMoney() {
        return money;
    }
    private static java.util.List<java.lang.ref.WeakReference<JLabel>> moneyLabels = new java.util.ArrayList<>();
    private static java.util.Map<String, Integer> caughtCounts = new java.util.LinkedHashMap<>();
    public static int getCaughtCount(String fish) {
        return caughtCounts.getOrDefault(fish, 0);
    }
    public static void incrementCaught(String fish) {
        caughtCounts.put(fish, caughtCounts.getOrDefault(fish, 0) + 1);
    }
    public static JLabel createMoneyLabel() {
        JLabel label = new JLabel("₱" + money);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        moneyLabels.add(new java.lang.ref.WeakReference<>(label));
        return label;
    }
    public static void addMoney(int amount) {
        money += amount;
        moneyLabels.removeIf(ref -> ref.get() == null);
        for (java.lang.ref.WeakReference<JLabel> ref : moneyLabels) {
            JLabel label = ref.get();
            if (label != null) label.setText("₱" + money);
        }
    }
}

// === Sell.java ===
package Pusawan;
import java.awt.*;
import javax.swing.*;
public class Sell extends JPanel {
    private ImageIcon sellBackgroundGif;
    private Image hover;
    private boolean hovering = false;
    private Rectangle sellZone = new Rectangle(180, 65, 830, 585);
    private JButton inventoryButton;
    public Sell() {
        sellBackgroundGif = new ImageIcon(getClass().getResource("/images/sell.gif"));
        hover = new ImageIcon(getClass().getResource("/images/sellSelected.png")).getImage();
        new javax.swing.Timer(24, e -> { if (isShowing()) repaint(); }).start();
        BackgroundPanel bg = new BackgroundPanel();
        setLayout(new BorderLayout());
        add(bg, BorderLayout.CENTER);
        bg.setLayout(null);
        JButton menuButton = Buttons.toDropdown();
        menuButton.setBounds(20, 20, 64, 64);
        bg.add(menuButton);
        JButton arrowLeft = new JButton(new ImageIcon(getClass().getResource("/images/arrowLeft.png")));
        arrowLeft.setBounds(20, 400, 64, 64);
        arrowLeft.setBorderPainted(false);
        arrowLeft.setContentAreaFilled(false);
        arrowLeft.setFocusPainted(false);
        Buttons.addClickSound(arrowLeft);
        arrowLeft.addActionListener(e -> Game.navigate(Game.STORE));
        bg.add(arrowLeft);
        bg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (Shop.instance != null) {
                    Shop.instance.dispose();
                    e.consume();
                    return;
                }
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (Shop.instance != null) return;
                if (sellZone.contains(e.getPoint())) {
                    hovering = false;
                    repaint();
                    Buttons.closeAllDropdowns();
                    Shop.toggleShop("sell");
                } else {
                    if (Inventory.instance != null) Inventory.instance.closeInventory();
                }
            }
        });
        bg.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                hovering = sellZone.contains(e.getPoint()) && Shop.instance == null;
                repaint();
            }
        });
        inventoryButton = Buttons.createInventoryButton();
        bg.add(inventoryButton);
    }
    class BackgroundPanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(sellBackgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
            if (hovering) g.drawImage(hover, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// === Shop.java ===
package Pusawan;
import java.awt.*;
import javax.swing.*;
public class Shop extends JFrame {
    private JPanel bg;
    static Shop instance;
    private static final java.util.Map<String, Integer> sellPrices = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> buyPrices  = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, Integer> buyItems = new java.util.LinkedHashMap<>();
    private boolean isBuyItemsTab = false;
    static {
        sellPrices.put("Cooked Bass",    80);
        sellPrices.put("Cooked Catfish", 60);
        sellPrices.put("Cooked Carp",    50);
        sellPrices.put("Cooked Perch",   30);
        sellPrices.put("Bass",    20);
        sellPrices.put("Catfish", 15);
        sellPrices.put("Carp",    12);
        sellPrices.put("Perch",    7);
        sellPrices.put("Cut Bass",    10);
        sellPrices.put("Cut Catfish",  8);
        sellPrices.put("Cut Carp",     6);
        sellPrices.put("Cut Perch",    4);
        sellPrices.put("Worm Bait",   10);
        sellPrices.put("Insect Bait", 12);
        sellPrices.put("Fish Bait",   17);
        sellPrices.put("Magic Bait",  50);
        sellPrices.put("Sandal",          5);
        sellPrices.put("Shoe",            5);
        sellPrices.put("Plastic Wrapper", 2);
        buyPrices.put("Worm Bait",   20);
        buyPrices.put("Insect Bait", 25);
        buyPrices.put("Fish Bait",   35);
        buyPrices.put("Magic Bait",  100);
        buyItems.put("bambooRod", 2000);
        buyItems.put("hotdogRod", 5000);
    }
    private boolean isBuyTab      = false; 
    private boolean isSellBaitTab = false; 
    private boolean isSellItemsTab= false; 
    private String shopMode = "both"; 
    public Shop(String mode) {
        this.shopMode = mode;
        if (mode.equals("buy")) isBuyTab = true;
        setTitle("Shop");
        setSize(760, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        setCursor(playerCursor.getCustomCursor());
        setLayout(null);
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 760, 680);
        add(layeredPane);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!layeredPane.getBounds().contains(e.getPoint())) dispose();
            }
        });
        JPanel header = new JPanel(null);
        header.setBounds(30, 0, 700, 40);
        header.setOpaque(true);
        header.setBackground(new Color(0x1800ad));
        JLabel title = new JLabel(new ImageIcon(getClass().getResource("/images/storeHeader.png")));
        title.setBounds(0, 0, 700, 40);
        header.add(title);
        JButton closeButton = new JButton("X");
        closeButton.setBounds(650, 0, 50, 40);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        Buttons.addClickSound(closeButton);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.addActionListener(e -> dispose());
        header.add(closeButton);
        layeredPane.add(header, JLayeredPane.PALETTE_LAYER);
        JPanel coinsPanel = new JPanel(null);
        coinsPanel.setBounds(30, 45, 700, 45);
        coinsPanel.setBackground(new Color(50, 40, 30));
        JLabel coinsLabel = PlayerData.createMoneyLabel();
        coinsLabel.setBounds(10, 8, 300, 30);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        coinsLabel.setForeground(Color.WHITE);
        coinsPanel.add(coinsLabel);
        JButton sellAllButton = new JButton("Sell All");
        sellAllButton.setBounds(560, 8, 120, 30);
        Buttons.addClickSound(sellAllButton);
        sellAllButton.addActionListener(e -> { sellAll(); refreshItems(); });
        sellAllButton.setVisible(!mode.equals("buy"));
        coinsPanel.add(sellAllButton);
        layeredPane.add(coinsPanel, JLayeredPane.DEFAULT_LAYER);
        JPanel tabPanel = new JPanel(null);
        tabPanel.setBounds(30, 95, 700, 40);
        tabPanel.setBackground(new Color(40, 30, 20));
        layeredPane.add(tabPanel, JLayeredPane.DEFAULT_LAYER);
        if (mode.equals("sell")) {
            JButton sellFishTab  = makeTab("Fish",  0,   175);
            JButton sellBaitTab  = makeTab("Bait",  175, 175);
            JButton sellItemsTab = makeTab("Items", 350, 175);
            setActive(sellFishTab);
            setInactive(sellBaitTab);
            setInactive(sellItemsTab);
            sellFishTab.addActionListener(e -> {
                isSellBaitTab = false; isSellItemsTab = false;
                setActive(sellFishTab); setInactive(sellBaitTab); setInactive(sellItemsTab);
                sellAllButton.setVisible(true);
                refreshItems();
            });
            sellBaitTab.addActionListener(e -> {
                isSellBaitTab = true; isSellItemsTab = false;
                setActive(sellBaitTab); setInactive(sellFishTab); setInactive(sellItemsTab);
                sellAllButton.setVisible(true);
                refreshItems();
            });
            sellItemsTab.addActionListener(e -> {
                isSellItemsTab = true; isSellBaitTab = false;
                setActive(sellItemsTab); setInactive(sellFishTab); setInactive(sellBaitTab);
                sellAllButton.setVisible(true);
                refreshItems();
            });
            tabPanel.add(sellFishTab);
            tabPanel.add(sellBaitTab);
            tabPanel.add(sellItemsTab);
        } else if (mode.equals("buy")) {
            JButton buyBaitTab  = makeTab("Bait",  0,   175);
            JButton buyItemsTab = makeTab("Items", 175, 175);
            setActive(buyBaitTab);
            setInactive(buyItemsTab);
            buyBaitTab.addActionListener(e -> {
                isBuyItemsTab = false;
                setActive(buyBaitTab); setInactive(buyItemsTab);
                refreshItems();
            });
            buyItemsTab.addActionListener(e -> {
                isBuyItemsTab = true;
                setActive(buyItemsTab); setInactive(buyBaitTab);
                refreshItems();
            });
            tabPanel.add(buyBaitTab);
            tabPanel.add(buyItemsTab);
        } else {
            JButton sellTab = makeTab("Sell", 0,   175);
            JButton buyTab  = makeTab("Buy",  175, 175);
            setActive(sellTab);
            setInactive(buyTab);
            sellTab.addActionListener(e -> {
                if (isBuyTab) {
                    isBuyTab = false;
                    setActive(sellTab); setInactive(buyTab);
                    sellAllButton.setVisible(true);
                    refreshItems();
                }
            });
            buyTab.addActionListener(e -> {
                if (!isBuyTab) {
                    isBuyTab = true;
                    setActive(buyTab); setInactive(sellTab);
                    sellAllButton.setVisible(false);
                    refreshItems();
                }
            });
            tabPanel.add(sellTab);
            tabPanel.add(buyTab);
        }
        bg = new JPanel();
        bg.setBackground(new Color(60, 45, 30));
        bg.setBounds(30, 140, 700, 510);
        bg.setLayout(new GridLayout(4, 5, 5, 5));
        bg.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        layeredPane.add(bg, JLayeredPane.DEFAULT_LAYER);
        refreshItems();
        setVisible(true);
    }
    private JButton makeTab(String label, int x, int width) {
        JButton tab = new JButton(label);
        Buttons.addClickSound(tab);
        tab.setBounds(x, 0, width, 40);
        tab.setFont(new Font("Arial", Font.BOLD, 14));
        tab.setFocusPainted(false);
        tab.setBorderPainted(false);
        setInactive(tab);
        return tab;
    }
    private void setActive(JButton tab) {
        tab.setBackground(new Color(90, 70, 45));
        tab.setForeground(Color.WHITE);
    }
    private void setInactive(JButton tab) {
        tab.setBackground(new Color(50, 40, 30));
        tab.setForeground(new Color(180, 160, 120));
    }
    private void refreshItems() {
        bg.removeAll();
        int i = 0;
        if (isBuyTab || shopMode.equals("buy")) {
            java.util.Map<String, Integer> source = isBuyItemsTab ? buyItems : buyPrices;
            for (java.util.Map.Entry<String, Integer> entry : source.entrySet()) {
                if (i >= 20) break;
                        String name  = entry.getKey();
                        int    price = entry.getValue();
                        JPanel slot = new JPanel(new BorderLayout());
                        slot.setBackground(new Color(90, 70, 45));
                        slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        java.net.URL imgUrl = getClass().getResource(resolveImagePath(name));
                        if (imgUrl != null) {
                            ImageIcon icon = new ImageIcon(imgUrl);
                            Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                            JLabel iconLabel = new JLabel(name + "  ₱" + price, new ImageIcon(scaled), SwingConstants.CENTER);
                            iconLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                            iconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                            iconLabel.setForeground(Color.WHITE);
                            slot.add(iconLabel, BorderLayout.CENTER);
                        } else {
                            JLabel itemLabel = new JLabel(name + "  ₱" + price, SwingConstants.CENTER);
                            itemLabel.setForeground(Color.WHITE);
                            itemLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                            slot.add(itemLabel, BorderLayout.CENTER);
                        }
                        boolean isRod = name.endsWith("Rod");
                        boolean owned = isRod && Inventory.items.getOrDefault(name, 0) > 0;
                        JButton buyBtn = new JButton(owned ? "Owned" : "Buy");
                        Buttons.addClickSound(buyBtn);
                        buyBtn.setEnabled(!owned);
                        buyBtn.addActionListener(e -> {
                            if (PlayerData.getMoney() >= price) {
                                PlayerData.addMoney(-price);
                                Inventory.addItem(name);
                                refreshItems();
                            }
                        });
                        slot.add(buyBtn, BorderLayout.SOUTH);
                        bg.add(slot);
                        i++;
                    }
        } else {
            for (java.util.Map.Entry<String, Integer> entry : Inventory.items.entrySet()) {
                if (i >= 20) break;
                String name  = entry.getKey();
                int    count = entry.getValue();
                if (!sellPrices.containsKey(name)) continue;
                if (shopMode.equals("sell")) {
                    boolean isFish = name.equals("Bass") || name.equals("Catfish")
                        || name.equals("Carp") || name.equals("Perch")
                        || name.startsWith("Cut ") || name.startsWith("Cooked ");
                    boolean isBait = name.endsWith("Bait");
                    if (isSellBaitTab  && !isBait) continue;
                    if (isSellItemsTab && (isFish || isBait)) continue;
                    if (!isSellBaitTab && !isSellItemsTab && !isFish) continue;
                }
                int price = sellPrices.getOrDefault(name, 0);
                JPanel slot = new JPanel(new BorderLayout());
                slot.setBackground(new Color(90, 70, 45));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                java.net.URL imgUrl = getClass().getResource(resolveImagePath(name));
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    slot.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);
                } else {
                    JLabel itemLabel = new JLabel(name + "  ₱" + price, SwingConstants.CENTER);
                    itemLabel.setForeground(Color.WHITE);
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                    slot.add(itemLabel, BorderLayout.CENTER);
                }
                JLabel countLabel = new JLabel("x" + count + "  ₱" + price, SwingConstants.RIGHT);
                countLabel.setForeground(Color.YELLOW);
                countLabel.setFont(new Font("Arial", Font.BOLD, 12));
                slot.add(countLabel, BorderLayout.NORTH);
                JButton sellOne = new JButton("Sell");
                Buttons.addClickSound(sellOne);
                sellOne.addActionListener(e -> {
                    Inventory.removeItem(name);
                    PlayerData.addMoney(price);
                    refreshItems();
                });
                slot.add(sellOne, BorderLayout.SOUTH);
                bg.add(slot);
                i++;
            }
        }
        while (i < 20) {
            JPanel slot = new JPanel();
            slot.setBackground(new Color(90, 70, 45));
            slot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            bg.add(slot);
            i++;
        }
        bg.revalidate();
        bg.repaint();
    }
    private void sellAll() {
        for (java.util.Map.Entry<String, Integer> entry :
                new java.util.LinkedHashMap<>(Inventory.items).entrySet()) {
            String name = entry.getKey();
            if (!sellPrices.containsKey(name)) continue;
            if (shopMode.equals("sell")) {
                boolean isFish = name.equals("Bass") || name.equals("Catfish")
                    || name.equals("Carp") || name.equals("Perch")
                    || name.startsWith("Cut ") || name.startsWith("Cooked ");
                boolean isBait = name.endsWith("Bait");
                if (isSellBaitTab  && !isBait) continue;
                if (isSellItemsTab && (isFish || isBait)) continue;
                if (!isSellBaitTab && !isSellItemsTab && !isFish) continue;
            }
            int count = entry.getValue();
            int price = sellPrices.getOrDefault(name, 0);
            PlayerData.addMoney(price * count);
            for (int i = 0; i < count; i++) Inventory.removeItem(name);
        }
    }
    @Override
    public void dispose() {
        super.dispose();
        instance = null;
        if (Inventory.instance == null) Game.hideOverlay();
    }
    public static void toggleShop(String mode) {
        Buttons.closeAllDropdowns();
        if (instance != null && instance.isDisplayable()) {
            instance.dispose();
            return;
        }
        instance = new Shop(mode);
        Game.showOverlay();
    }
    private String resolveImagePath(String name) {
        if (name.startsWith("Cooked "))
            return "/images/cooked_" + name.replace("Cooked ", "").toLowerCase() + ".png";
        if (name.startsWith("Cut "))
            return "/images/cut_" + name.replace("Cut ", "").toLowerCase() + ".png";
        if (name.endsWith("Bait"))
            return "/images/" + Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", "") + ".png";
        return "/images/" + Character.toLowerCase(name.charAt(0)) + name.substring(1).replace(" ", "") + ".png";
    }
}

// === Start.java ===
package Pusawan;
import java.awt.*;
import javax.swing.*;
public class Start extends JPanel {
    private ImageIcon titleScreen;
    public Start() {
        loadBackgroundImage();
        new javax.swing.Timer(24, e -> { if (isShowing()) repaint(); }).start();
        BackgroundPanel background1 = new BackgroundPanel();
        background1.setLayout(null); 
        setLayout(new BorderLayout());
        add(background1, BorderLayout.CENTER);
        ImageIcon titleImage = new ImageIcon(
                getClass().getResource("/images/title.png")
        );
        JLabel title = new JLabel(titleImage);
        int titleWidth = 600;
        int titleHeight = 330;
        int titleX = (1350 - titleWidth) / 2;
        title.setBounds(titleX, -50, titleWidth, titleHeight);
        background1.add(title);
        JButton playButton = Buttons.toStart(() -> {
            AudioPlayer.playMusic("morningMood.wav");
            Game.navigate(Game.KITCHEN);
        });
        int buttonWidth = 143;
        int buttonHeight = 80;
        int playX = (1350 - buttonWidth) / 2;
        int playY = (1150 - buttonHeight) / 2;
        playButton.setBounds(playX, playY, buttonWidth, buttonHeight);
        background1.add(playButton);
    }
    public void loadBackgroundImage() {
        try {
            titleScreen = new ImageIcon(getClass().getResource("/images/titlescreen.gif"));
        } catch (Exception e) {
            System.out.println("Background image not found: " + e.getMessage());
        }
    }
    class BackgroundPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (titleScreen != null) {
                g.drawImage(titleScreen.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}

// === WrapLayout.java ===
package Pusawan;
import java.awt.*;
public class WrapLayout extends FlowLayout {
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }
    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
            int hgap = getHgap(), vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
            int width = 0, height = 0, rowHeight = 0, rowWidth = 0;
            for (Component c : target.getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                if (rowWidth + d.width > maxWidth) {
                    width = Math.max(width, rowWidth);
                    height += rowHeight + vgap;
                    rowWidth = 0;
                    rowHeight = 0;
                }
                rowWidth += d.width + hgap;
                rowHeight = Math.max(rowHeight, d.height);
            }
            width = Math.max(width, rowWidth);
            height += rowHeight + insets.top + insets.bottom + vgap * 2;
            return new Dimension(width, height);
        }
    }
}

// === playerCursor.java ===
package Pusawan;
import java.awt.*;
import javax.swing.*;
public class playerCursor {
    public static Cursor getCustomCursor() {
        System.out.println(playerCursor.class.getResource("/images/cursor.png"));
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image cursorImage = new ImageIcon(
                    playerCursor.class.getResource("/images/cursor.png")).getImage();
            return toolkit.createCustomCursor(cursorImage, new Point(0, 0), "cursor");
        } catch (Exception e) {
            return Cursor.getDefaultCursor();
        }
    }
}

