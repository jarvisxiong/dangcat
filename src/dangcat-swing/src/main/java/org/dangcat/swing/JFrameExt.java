package org.dangcat.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.dangcat.commons.resource.ResourceUtils;
import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.commons.utils.ValueUtils;

public abstract class JFrameExt extends JFrame
{
    private static final long serialVersionUID = 1L;

    public static void show(final JFrameExt frame)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                frame.pack();
                frame.centerFrameOnScreen();
                frame.setVisible(true);
            }
        });
    }

    protected final Logger logger = Logger.getLogger(this.getClass());

    public JFrameExt()
    {
        super();
        String title = this.getText(this.getClass().getSimpleName());
        if (!ValueUtils.isEmpty(title))
            this.setTitle(title);
        Image image = this.loadFrameIcon();
        if (image != null)
            this.setIconImage(image);
    }

    public JFrameExt(String title)
    {
        super(title);
    }

    private void centerFrameOnScreen()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
    }

    protected abstract Container createContentPane();

    protected String getText(Class<?> classType, String key, Object... params)
    {
        return ResourceUtils.getText(classType, key, params);
    }

    protected String getText(String key, Object... params)
    {
        return this.getText(this.getClass(), key, params);
    }

    protected Image loadFrameIcon()
    {
        return ImageUtils.loadImage(this.getClass(), this.getClass().getSimpleName() + ".png");
    }

    @Override
    public void pack()
    {
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                JFrameExt.this.dispose();
                System.exit(0);
            }
        });

        this.setContentPane(this.createContentPane());
        super.pack();
    }

    @Override
    public void setVisible(boolean visible)
    {
        this.centerFrameOnScreen();
        super.setVisible(visible);
    }
}
