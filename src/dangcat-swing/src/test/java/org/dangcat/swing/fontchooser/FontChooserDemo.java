package org.dangcat.swing.fontchooser;

import java.awt.*;

public class FontChooserDemo {
    public static void main(String[] args) {
        JFontChooser fontChooser = new JFontChooser();
        int result = fontChooser.showDialog(null);
        if (result == JFontChooser.OK_OPTION) {
            Font font = fontChooser.getSelectedFont();
            System.out.println("Selected Font : " + font);
        }
    }
}
