package org.dangcat.install.swing.panel;

import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.swing.JImagePanel;

import javax.swing.*;
import java.awt.*;

public class InstallHeaderPanel extends JImagePanel {
    private static final long serialVersionUID = 1L;
    private Image backgroundImage = null;
    private String backgroundName = "Background.png";
    private Font headerFont = new Font("宋体", Font.BOLD, 22);
    private String title = null;

    public Image getBackgroundImage() {
        if (this.backgroundImage == null && !ValueUtils.isEmpty(this.getBackgroundName()))
            return ImageUtils.loadImage(this.getClass(), this.getBackgroundName());
        return this.backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBackgroundName() {
        return this.backgroundName;
    }

    public void setBackgroundName(String backgroundName) {
        this.backgroundName = backgroundName;
    }

    private JLabel getFrameTitle() {
        JLabel frameTitle = new JLabel(this.title, JLabel.LEFT);
        frameTitle.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        frameTitle.setForeground(Color.RED);
        frameTitle.setFont(this.getHeaderFont());
        return frameTitle;
    }

    public Font getHeaderFont() {
        return this.headerFont;
    }

    public void setHeaderFont(Font headerFont) {
        this.headerFont = headerFont;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void initialize() {
        this.setPreferredSize(new Dimension(Short.MAX_VALUE, 60));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 20));
        this.setImage(this.getBackgroundImage());
        this.add(this.getFrameTitle());
    }
}
