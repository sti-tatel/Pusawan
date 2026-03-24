package Pusawan;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Cutting extends JFrame {
        JPanel screen;
        JButton button;
        JLabel title;
    
    public Cutting(){

        setTitle("Cutting");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);

        screen = new JPanel();

        title = new JLabel("Game Panel 2");
        add(title);

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

        button = new JButton("Go Cooking");
        add(button);

         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Store();
            }
        });

    }
}
