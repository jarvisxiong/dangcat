package org.dangcat.framework.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileNameFilter;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.Environment;

/**
 * 扩展目录载入包。
 * @author dangcat
 * 
 */
public class ExtensionClassLoader
{
    private static ExtensionClassLoader instance = new ExtensionClassLoader();
    private static final Logger logger = Logger.getLogger(ExtensionClassLoader.class);

    public static ExtensionClassLoader getInstance()
    {
        return instance;
    }

    private ClassLoader classLoader = null;
    private List<File> classPathList = new ArrayList<File>();

    private ExtensionClassLoader()
    {

    }

    /**
     * 添加路径目录。
     */
    public void addClassPath(File file)
    {
        if (file != null && file.exists() && !this.classPathList.contains(file))
            this.classPathList.add(file);
    }

    /**
     * 添加路径。
     */
    public void addClassPath(String classPath)
    {
        if (classPath != null && classPath.length() > 0)
        {
            StringTokenizer tokenizer = new StringTokenizer(classPath, ";");
            while (tokenizer.hasMoreTokens())
                addClassPath(new File(tokenizer.nextToken()));
        }
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * 载入扩展包。
     * @param parent 所属父加载器。
     * @return 类加载器。
     */
    public ClassLoader load(ClassLoader parent)
    {
        if (this.classLoader == null)
        {
            ClassLoader classLoader = parent;
            if (!this.classPathList.isEmpty())
            {
                FileNameFilter fileNameFilter = new FileNameFilter(".jar");
                fileNameFilter.addSuffixFilter(".zip");
                List<URL> urlList = new ArrayList<URL>();
                for (File file : this.classPathList)
                {
                    if (file.isDirectory())
                    {
                        File[] files = file.listFiles((FilenameFilter) fileNameFilter);
                        if (files != null)
                        {
                            Arrays.sort(files, new Comparator<File>()
                            {
                                public int compare(File srcFile, File dstFile)
                                {
                                    return srcFile.getName().compareTo(dstFile.getName());
                                }
                            });

                            for (File childFile : files)
                                this.load(urlList, childFile);
                        }
                    }
                    else
                        this.load(urlList, file);
                }
                if (urlList.size() > 0)
                    classLoader = new URLClassLoader(urlList.toArray(new URL[0]), classLoader);
            }
            this.classLoader = classLoader;
            ReflectUtils.addClassLoader(classLoader);
            Thread.currentThread().setContextClassLoader(classLoader);

            if (logger.isDebugEnabled())
                logger.info(this);
        }
        return this.classLoader;
    }

    private void load(List<URL> urlList, File file)
    {
        if (file != null)
        {
            try
            {
                if (!file.exists())
                    logger.error("The file " + file.getAbsolutePath() + " in classpath is not exists!");
                else
                {
                    URL url = file.toURI().toURL();
                    if (!urlList.contains(url))
                        urlList.add(url);
                }
            }
            catch (MalformedURLException e)
            {
                logger.error("The file load error " + file.getAbsolutePath(), e);
            }
        }
    }

    private int printTree(StringBuilder info, ClassLoader classLoader)
    {
        int depth = 0;
        if (classLoader.getParent() != null)
            depth = printTree(info, classLoader.getParent()) + 1;

        StringBuffer indent = new StringBuffer();
        for (int i = 0; i < depth; i++)
            indent.append("  ");

        if (classLoader instanceof URLClassLoader)
        {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            info.append(indent);
            info.append(classLoader.getClass().getName());
            info.append(" {");
            info.append(Environment.LINE_SEPARATOR);
            for (URL url : urlClassLoader.getURLs())
            {
                info.append(indent);
                info.append("  ");
                info.append(url);
                info.append(Environment.LINE_SEPARATOR);
            }
            info.append(indent);
            info.append("}");
            info.append(Environment.LINE_SEPARATOR);
        }
        else
        {
            info.append(indent);
            info.append(classLoader.getClass().getName());
        }
        return depth;
    }

    /**
     * 打印加载结构。
     */
    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        if (this.classLoader != null)
            printTree(info, this.classLoader);
        return info.toString();
    }
}
