package Pusawan;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class GamePanel1 extends JFrame {
        JPanel screen;
        JButton button;
        JLabel title;
    
    public GamePanel1(){

        setTitle("Game Panel 1");
        setSize(1350,750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setVisible(true);

        screen = new JPanel();

        title = new JLabel("Game Panel 1");
        add(title);

        button = new JButton("Back to Main Menu");
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
