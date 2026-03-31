package Pusawan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Inventory extends JFrame {

    JPanel screen;

    public Inventory() {

        setTitle("Game Panel 1");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setUndecorated(true);
        setVisible(true);
        setResizable(false);
        getContentPane().setBackground(new Color(186, 160, 84));
        setLocationRelativeTo(screen);;
        setCursor(playerCursor.getCustomCursor());

        screen = new JPanel();

        JLabel title;
        title = new JLabel("INVENTORY");
        add(title);

        ImageIcon backButtonImage = new ImageIcon(getClass().getResource("/images/backbutton.png"));
        JButton button = new JButton(backButtonImage);

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                dispose();

            }
        });

    }
}
