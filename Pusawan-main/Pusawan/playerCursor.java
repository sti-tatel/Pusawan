package Pusawan;

import java.awt.*;
import javax.swing.*;

public class playerCursor {
    public static java.awt.Cursor getCustomCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Image cursorImage = new ImageIcon(
        playerCursor.class.getResource("/images/cursor.png")).getImage();
        Point hotspot = new Point(0, 0);

        return toolkit.createCustomCursor(cursorImage, hotspot, "cursor");
    }
}