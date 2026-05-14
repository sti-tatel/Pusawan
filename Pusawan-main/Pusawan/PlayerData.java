package Pusawan;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

public class PlayerData {
    private static int money = 99999999;

    public static int getMoney() {
        return money;
    }

    private static java.util.List<JLabel> moneyLabels = new java.util.ArrayList<>();

    public static JLabel createMoneyLabel() {
        JLabel label = new JLabel("₱" + money);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        moneyLabels.add(label);
        return label;
    }

    public static void addMoney(int amount) {
        money += amount;
        for (JLabel label : moneyLabels) {
            label.setText("₱" + money);
        }
    }


    
}