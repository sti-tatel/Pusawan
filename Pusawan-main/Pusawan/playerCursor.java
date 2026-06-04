package Pusawan;

import java.awt.*;
import javax.swing.*;

public class playerCursor {

    public static Cursor getCustomCursor() {
        System.out.println(playerCursor.class.getResource("/images/cursor.png"));
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image cursorImage = new ImageIcon(
                    playerCursor.class.getResource("/images/cursor.png")).getImage();
            return toolkit.createCustomCursor(cursorImage, new Point(0, 0), "cursor");
        } catch (Exception e) {
            return Cursor.getDefaultCursor();
        }
    }
}
