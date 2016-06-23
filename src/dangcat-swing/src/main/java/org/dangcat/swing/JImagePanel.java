package org.dangcat.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class JImagePanel extends JPanel
{
    protected static final Logger logger = Logger.getLogger(JImagePanel.class);
    private static final long serialVersionUID = 1L;
    private Image image;
    private File imageFile = null;
    private InputStream imageInputStream = null;
    private Insets imageInsets = null;
    private boolean imageStretch = true;

    private void createImage()
    {
        if (this.image == null && this.getImageInputStream() != null)
        {
            try
            {
                this.image = ImageIO.read(this.getImageInputStream());
            }
            catch (Exception e)
            {
                logger.error("The image read error.", e);
            }
        }
    }

    public Image getImage()
    {
        this.createImage();
        return this.image;
    }

    public File getImageFile()
    {
        return this.imageFile;
    }

    public InputStream getImageInputStream()
    {
        if (this.imageInputStream == null && this.getImageFile() != null)
        {
            try
            {
                this.imageInputStream = new FileInputStream(this.getImageFile());
            }
            catch (Exception e)
            {
                logger.error("The image file " + this.getImageFile().getAbsolutePath() + " read error.", e);
            }
        }
        return this.imageInputStream;
    }

    public Insets getImageInsets()
    {
        return this.imageInsets;
    }

    public boolean isImageStretch()
    {
        return this.imageStretch;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Image image = this.getImage();
        if (image != null)
        {
            int x = 0;
            int y = 0;
            int width = this.getWidth();
            int height = this.getHeight();
            Insets insets = this.getImageInsets();
            if (insets != null)
            {
                x = insets.left;
                y = insets.top;
                width = this.getWidth() - insets.left - insets.right;
                height = this.getHeight() - insets.top - insets.bottom;
            }
            if (!this.isImageStretch())
            {
                width = image.getWidth(this);
                height = image.getHeight(this);
            }
            g.drawImage(image, x, y, width, height, this);
        }
    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public void setImageFile(File imageFile)
    {
        this.imageFile = imageFile;
    }

    public void setImageInputStream(InputStream imageInputStream)
    {
        this.imageInputStream = imageInputStream;
    }

    public void setImageInsets(Insets imageInsets)
    {
        this.imageInsets = imageInsets;
    }

    public void setImageStretch(boolean imageStretch)
    {
        this.imageStretch = imageStretch;
    }
}
