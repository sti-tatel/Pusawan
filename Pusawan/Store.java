package Pusawan;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Store extends JFrame {
        JPanel screen;
        
    public Store(){

        setTitle("Game Store");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);
        getContentPane().setBackground(Color.cyan);

        screen = new JPanel();

        JLabel title;
        title = new JLabel("Game Store");
        add(title);

        JButton button;
        button = new JButton("GO to GamePanel1");
        add(button);
        button.setBounds(100,500,100,100);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new GamePanel1();
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

        button = new JButton("Go to Cooking");
        add(button);

         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Cooking();
            }
        });

        button = new JButton("Go to Cutting");
        add(button);

         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                dispose();

                new Cutting();
            }
        });

    }
}
