package Pusawan;

import java.awt.*;
import javax.swing.*;

public class playerCursor {
    public static java.awt.Cursor getCustomCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Image cursorImage = new ImageIcon("Pusawan-main\\Pusawan\\images\\cursor.png").getImage();
        Point hotspot = new Point(0, 0);

        return toolkit.createCustomCursor(cursorImage, hotspot, "cursor");
    }
}