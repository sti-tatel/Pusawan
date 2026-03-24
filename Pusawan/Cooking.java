package Pusawan;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Cooking extends JFrame {
        JPanel screen;
        
    public Cooking(){

        setTitle("Game Cooking");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);
        getContentPane().setBackground(Color.BLUE);

        screen = new JPanel();

        JLabel title;
        title = new JLabel("Game Cooking");
        add(title);

        JButton button;
        button = new JButton("Back Main Menu");
        add(button);
        button.setBounds(100,500,100,100);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Start();
            }
        });

        
        button = new JButton("INVENTORY");
        add(button);

         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();
                new Inventory();
            }
        });

        button = new JButton("Go to Store");
        add(button);

         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Store();
            }
        });

    }
}
