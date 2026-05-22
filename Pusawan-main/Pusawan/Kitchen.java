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
        // Ensure kitchen.gif is in the same folder as Kitchen.java
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
                
                // Check LEFT hitbox
                if (new Rectangle(0, 0, LEFT_WIDTH, HITBOX_HEIGHT).contains(p)) {
                    goToCooking(); // Calls the method that uses Game.navigate(Game.COOKING)
                    
                // Check RIGHT hitbox
                } else if (new Rectangle(getWidth() - RIGHT_WIDTH, 0, RIGHT_WIDTH, HITBOX_HEIGHT).contains(p)) {
                    goToCutting(); // Calls the method that uses Game.navigate(Game.CUTTING)
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