package org.dangcat.boot.menus;

import org.apache.log4j.Logger;
import org.dangcat.boot.menus.xml.MenusXmlResolver;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.io.ResourceLoader;
import org.dangcat.commons.utils.ValueUtils;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 系统菜单管理。
 * @author dangcat
 * 
 */
public class MenusManager
{
    protected static Logger logger = Logger.getLogger(MenusManager.class);
    private static MenusManager instance = new MenusManager();
    private Menus menus = null;

    private MenusManager()
    {
    }

    public static MenusManager getInstance()
    {
        return instance;
    }

    public Menus getMenus()
    {
        return this.menus;
    }

    public void load(Class<?> classType, String name)
    {
        if (!ValueUtils.isEmpty(name))
        {
            InputStream inputStream = ResourceLoader.load(classType, name);
            if (inputStream != null)
            {
                try
                {
                    this.load(inputStream);
                }
                finally
                {
                    FileUtils.close(inputStream);
                }
            }
        }
    }

    public void load(File file)
    {
        if (file == null || !file.exists())
        {
            logger.warn("The system munus file " + file + " is not exists.");
            return;
        }

        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
            this.load(inputStream);
        }
        catch (FileNotFoundException e)
        {
            logger.error(file, e);
        }
        finally
        {
            inputStream = FileUtils.close(inputStream);
        }
    }

    private void load(InputStream inputStream)
    {
        try
        {
            MenusXmlResolver menusXmlResolver = new MenusXmlResolver();
            menusXmlResolver.open(inputStream);
            this.menus = (Menus) menusXmlResolver.resolve();
        }
        catch (DocumentException e)
        {
            logger.error(this, e);
        }
    }
}
