package Pusawan;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Inventory extends JFrame {
        JPanel screen;
        JButton button;
        JLabel title;
    
    public Inventory(){

        setTitle("Game Panel 1");
        setSize(500,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);

        screen = new JPanel();
        screen.setBackground(Color.BLUE);

        title = new JLabel("INVENTORY");
        add(title);

        button = new JButton("TO GAME PANEL 1");
        add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();
                new Inventory();
            }
        });
    }   
}
