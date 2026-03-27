package Pusawan;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Inventory extends JFrame {
        JPanel screen;
          
    public Inventory(){

        setTitle("Game Panel 1");
        setSize(500,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);
        setResizable(false);
        getContentPane().setBackground(new Color(186, 160, 84));


        screen = new JPanel();

        JLabel title;
        title = new JLabel("INVENTORY");
        add(title);


        JButton button;
        button = new JButton("TO GAME PANEL 1");
        add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();
                new GamePanel1();
            }
        });

    }   
}
