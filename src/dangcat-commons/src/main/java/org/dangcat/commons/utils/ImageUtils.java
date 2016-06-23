package org.dangcat.commons.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class ImageUtils
{
    public static Image loadImage(Class<?> classType, String name)
    {
        if (Object.class == classType)
            return null;

        InputStream inputStream = classType.getResourceAsStream(name);
        if (inputStream == null)
            return loadImage(classType.getSuperclass(), name);

        Image image = null;
        try
        {
            image = ImageIO.read(inputStream);
        }
        catch (Exception e)
        {
        }
        return image;
    }

    public static Image loadImage(File file)
    {
        Image image = null;
        if (file != null && file.exists())
        {
            try
            {
                image = ImageIO.read(new FileInputStream(file));
            }
            catch (Exception e)
            {
            }
        }
        return image;
    }

    public static ImageIcon loadImageIcon(Class<?> classType, String name)
    {
        if (Object.class == classType)
            return null;

        URL url = classType.getResource(name);
        if (url != null)
            return new ImageIcon(classType.getResource(name));
        return loadImageIcon(classType.getSuperclass(), name);
    }
}
