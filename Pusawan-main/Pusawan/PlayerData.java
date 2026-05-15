package Pusawan;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

public class PlayerData {
    private static int money = 99999999;

    public static int getMoney() {
        return money;
    }

    private static java.util.List<java.lang.ref.WeakReference<JLabel>> moneyLabels = new java.util.ArrayList<>();

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