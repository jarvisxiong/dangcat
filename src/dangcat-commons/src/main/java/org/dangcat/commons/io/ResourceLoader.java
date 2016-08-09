package org.dangcat.commons.io;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 资源加载器。
 *
 * @author dangcat
 */
public class ResourceLoader {
    private static final Logger logger = Logger.getLogger(ResourceLoader.class);
    private static final String META_INF = "META-INF/";
    private static Collection<File> extendDirectories = new LinkedHashSet<File>();
    private Class<?> classType = null;
    private FileNameFilter fileNameFilter = null;
    private Collection<JarFile> jarFiles = null;
    private Locale locale = null;
    private String namePostfix = null;
    private String namePrefix = null;
    private Collection<File> paths = null;
    private List<Resource> resources = new LinkedList<Resource>();

    public ResourceLoader(Class<?> classType) {
        this(classType, null, null, null);
    }

    public ResourceLoader(Class<?> classType, Locale locale, String namePrefix, String namePostfix) {
        this.classType = classType;
        this.namePrefix = namePrefix;
        this.namePostfix = namePostfix;
        this.locale = locale;
    }

    public ResourceLoader(Class<?> classType, String namePrefix) {
        this(classType, null, namePrefix, null);
    }

    public ResourceLoader(Class<?> classType, String namePrefix, String namePostfix) {
        this(classType, null, namePrefix, namePostfix);
    }

    public static void addExtendDirectory(File extendDirectory) {
        if (extendDirectory != null && extendDirectory.exists() && extendDirectory.isDirectory())
            extendDirectories.add(extendDirectory);
    }

    public static InputStream load(Class<?> classType, String resourceName) {
        return load(new ResourceLoader(classType, resourceName));
    }

    public static InputStream load(Class<?> classType, String namePreFix, String namePostFix) {
        return load(new ResourceLoader(classType, namePreFix, namePostFix));
    }

    private static InputStream load(ResourceLoader resourceLoader) {
        Class<?> classType = resourceLoader.getClassType();
        InputStream inputStream = null;
        try {
            resourceLoader.load();
            Resource resource = resourceLoader.getResource();
            if (resource != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                FileUtils.copy(resource.getInputStream(), outputStream);
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                logger.info("Load resource from " + resource + " by " + classType.getSimpleName());
            }
        } catch (Exception e) {
            logger.error(classType, e);
        } finally {
            resourceLoader.close();
        }
        return inputStream;
    }

    /**
     * 添加路径。
     */
    public void addPath(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (logger.isDebugEnabled())
                logger.warn("The path " + directory.getAbsolutePath() + " is not exists.");
        } else if (directory.isDirectory()) {
            if (this.paths == null)
                this.paths = new ArrayList<File>();
            if (!this.paths.contains(directory))
                this.paths.add(directory);
        }
    }

    /**
     * 关闭加载的资源。
     */
    public void close() {
        try {
            for (Resource resource : this.resources)
                resource.getInputStream().close();
        } catch (IOException e) {
            logger.error(this, e);
        }
        try {
            if (this.jarFiles != null) {
                for (JarFile jarFile : this.jarFiles)
                    jarFile.close();
            }
        } catch (IOException e) {
            logger.error(this, e);
        }
    }

    public Class<?> getClassType() {
        return this.classType;
    }

    private FileNameFilter getFileNameFilter() {
        if (this.fileNameFilter == null)
            this.fileNameFilter = new FileNameFilter(this.getNamePrefix(), this.getNamePostfix());
        return this.fileNameFilter;
    }

    protected Locale getLocale() {
        return this.locale;
    }

    protected void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getNamePostfix() {
        return this.namePostfix;
    }

    public void setNamePostfix(String namePostfix) {
        this.namePostfix = namePostfix;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    /**
     * 如果读取一个资源，就读取优先级最高的。
     *
     * @return 资源配置。
     */
    public Resource getResource() {
        return this.resources.size() > 0 ? this.resources.get(this.resources.size() - 1) : null;
    }

    public List<Resource> getResourceList() {
        return this.resources;
    }

    private String getResourceName() {
        StringBuilder resourceName = new StringBuilder();
        if (!ValueUtils.isEmpty(this.getNamePrefix()))
            resourceName.append(this.getNamePrefix());
        if (this.getLocale() != null) {
            resourceName.append("_");
            resourceName.append(this.getLocale());
        }
        if (!ValueUtils.isEmpty(this.getNamePostfix()))
            resourceName.append(this.getNamePostfix());
        return resourceName.toString();
    }

    /**
     * 读取资源文件。
     */
    public void load() {
        List<Resource> resourceList = new LinkedList<Resource>();
        // 读取类当前目录资源。
        this.loadFromResource(resourceList);
        // 读取JAR包目录。
        this.loadFromJarFile(resourceList);
        // 优先读取扩展目录。
        this.loadFromPath(resourceList);

        this.resources.clear();
        for (int index = resourceList.size() - 1; index >= 0; index--) {
            Resource resource = resourceList.get(index);
            if (this.resources.contains(resource)) {
                try {
                    resource.getInputStream().close();
                } catch (IOException e) {
                }
            } else
                this.resources.add(resource);
        }
    }

    /**
     * 读取资源文件。
     */
    private void loadFromJarFile(Collection<Resource> resources) {
        if (this.classType == null)
            return;

        try {
            String filePath = FileUtils.getResourcePath(this.classType, null);
            File file = new File(FileUtils.decodePath(filePath));
            if (file.isDirectory() || !file.exists())
                return;

            String packagePath = this.classType.getPackage().getName().replace(".", "/");
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
            Map<String, JarEntry> metaInfoResourceMap = new HashMap<String, JarEntry>();
            Map<String, JarEntry> classPathResourceMap = new HashMap<String, JarEntry>();
            while (jarEntryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = jarEntryEnumeration.nextElement();
                if (jarEntry.getName().startsWith(META_INF)) {
                    String resourceName = jarEntry.getName().replace(META_INF, "");
                    if (this.getFileNameFilter().accept(resourceName))
                        metaInfoResourceMap.put(resourceName, jarEntry);
                } else if (jarEntry.getName().startsWith(packagePath)) {
                    String resourceName = jarEntry.getName().replace(packagePath, "").replace("/", "");
                    if (this.getFileNameFilter().accept(resourceName))
                        classPathResourceMap.put(resourceName, jarEntry);
                }
            }
            classPathResourceMap.putAll(metaInfoResourceMap);
            for (Entry<String, JarEntry> entry : classPathResourceMap.entrySet()) {
                BufferedInputStream inputStream = new BufferedInputStream(jarFile.getInputStream(entry.getValue()));
                resources.add(new Resource(file.getAbsolutePath(), entry.getKey(), inputStream));
            }
            if (this.jarFiles == null)
                this.jarFiles = new ArrayList<JarFile>();
            this.jarFiles.add(jarFile);
        } catch (Exception e) {
            logger.error(this, e);
        }
    }

    /**
     * 读取属性文件。
     */
    private void loadFromPath(Collection<Resource> resources) {
        String directory = this.classType.getPackage().getName().replace(".", "/");
        this.addPath(FileUtils.getResourcePath(this.classType, directory));
        // 路径考虑目录结构的META_INF
        this.addPath(FileUtils.getResourcePath(this.classType, META_INF));

        if (this.paths != null) {
            for (File directoryFile : this.paths)
                this.loadFromPath(resources, directoryFile);
        }
        // 由扩展目录加载
        if (!extendDirectories.isEmpty()) {
            for (File extendDirectory : extendDirectories)
                this.loadFromPath(resources, extendDirectory);
        }
    }

    private void loadFromPath(Collection<Resource> resources, File directory) {
        File[] files = directory.listFiles((FilenameFilter) this.getFileNameFilter());
        if (files.length > 0) {
            logger.info("Load resource files from " + directory.getAbsolutePath() + ": " + files.length);
            for (File file : files) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
                    resources.add(new Resource(file.getParent(), file.getName(), inputStream));
                } catch (FileNotFoundException e) {
                    logger.warn(this, e);
                }
            }
        }
    }

    /**
     * 读取资源文件。
     */
    private void loadFromResource(Collection<Resource> resources) {
        if (this.classType != null) {
            String resourceName = this.getResourceName();
            if (!ValueUtils.isEmpty(resourceName)) {
                InputStream inputStream = this.classType.getResourceAsStream(resourceName);
                if (inputStream != null) {
                    String path = FileUtils.getResourcePath(this.classType, null);
                    resources.add(new Resource(path, resourceName, inputStream));
                }
            }
        }
    }
}
