package org.dangcat.install.swing.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JLabel;

import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.swing.JImagePanel;

public class InstallHeaderPanel extends JImagePanel
{
    private static final long serialVersionUID = 1L;
    private Image backgroundImage = null;
    private String backgroundName = "Background.png";
    private Font headerFont = new Font("ËÎÌו", Font.BOLD, 22);
    private String title = null;

    public Image getBackgroundImage()
    {
        if (this.backgroundImage == null && !ValueUtils.isEmpty(this.getBackgroundName()))
            return ImageUtils.loadImage(this.getClass(), this.getBackgroundName());
        return this.backgroundImage;
    }

    public String getBackgroundName()
    {
        return this.backgroundName;
    }

    private JLabel getFrameTitle()
    {
        JLabel frameTitle = new JLabel(this.title, JLabel.LEFT);
        frameTitle.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        frameTitle.setForeground(Color.RED);
        frameTitle.setFont(this.getHeaderFont());
        return frameTitle;
    }

    public Font getHeaderFont()
    {
        return this.headerFont;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void initialize()
    {
        this.setPreferredSize(new Dimension(Short.MAX_VALUE, 60));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 20));
        this.setImage(this.getBackgroundImage());
        this.add(this.getFrameTitle());
    }

    public void setBackgroundImage(Image backgroundImage)
    {
        this.backgroundImage = backgroundImage;
    }

    public void setBackgroundName(String backgroundName)
    {
        this.backgroundName = backgroundName;
    }

    public void setHeaderFont(Font headerFont)
    {
        this.headerFont = headerFont;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
