package org.dangcat.commons.io;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 按照文件前缀、后缀进行过滤。
 */
public class FileNameFilter implements FilenameFilter, FileFilter {
    private List<AbstractFileFilter> abstractFileFilterList = new ArrayList<AbstractFileFilter>();
    private List<AbstractFileFilter[]> abstractFileFiltersList = new ArrayList<AbstractFileFilter[]>();

    public FileNameFilter() {
    }

    /**
     * 以文件后缀构建过滤。
     *
     * @param suffix 后缀名。
     */
    public FileNameFilter(String suffix) {
        this.addSuffixFilter(suffix);
    }

    /**
     * 以文件前缀+后缀构建过滤。
     *
     * @param prefix 前缀名。
     * @param suffix 后缀名。
     */
    public FileNameFilter(String prefix, String suffix) {
        this.addFilter(prefix, suffix);
    }

    @Override
    public boolean accept(File file) {
        if (file.getName().endsWith("~"))
            return false;

        for (AbstractFileFilter abstractFileFilter : this.abstractFileFilterList) {
            if (abstractFileFilter.accept(file))
                return true;
        }
        for (AbstractFileFilter[] abstractFileFilters : this.abstractFileFiltersList) {
            boolean reault = true;
            for (AbstractFileFilter abstractFileFilter : abstractFileFilters) {
                if (!abstractFileFilter.accept(file)) {
                    reault = false;
                    break;
                }
            }
            if (reault)
                return true;
        }
        return false;
    }

    @Override
    public boolean accept(File directory, String fileName) {
        if (fileName.endsWith("~"))
            return false;

        for (AbstractFileFilter abstractFileFilter : this.abstractFileFilterList) {
            if (abstractFileFilter.accept(directory, fileName))
                return true;
        }
        for (AbstractFileFilter[] abstractFileFilters : this.abstractFileFiltersList) {
            boolean reault = true;
            for (AbstractFileFilter abstractFileFilter : abstractFileFilters) {
                if (!abstractFileFilter.accept(directory, fileName)) {
                    reault = false;
                    break;
                }
            }
            if (reault)
                return true;
        }
        return false;
    }

    public boolean accept(String fileName) {
        return this.accept(null, fileName);
    }

    /**
     * 添加前缀+后缀过滤标志。
     *
     * @param prefix 前缀。
     * @param suffix 后缀。
     */
    public void addFilter(String prefix, String suffix) {
        if (!ValueUtils.isEmpty(prefix) && !ValueUtils.isEmpty(suffix))
            this.abstractFileFiltersList.add(new AbstractFileFilter[]{new PrefixFileFilter(prefix, IOCase.INSENSITIVE), new SuffixFileFilter(suffix, IOCase.INSENSITIVE)});
        else {
            this.addPrefixFilter(prefix);
            this.addSuffixFilter(suffix);
        }
    }

    /**
     * 添加前缀过滤。
     *
     * @param prefix 前缀名。
     */
    public void addPrefixFilter(String prefix) {
        if (!ValueUtils.isEmpty(prefix))
            this.abstractFileFilterList.add(new PrefixFileFilter(prefix, IOCase.INSENSITIVE));
    }

    /**
     * 添加后缀过滤。
     *
     * @param suffix 后缀。
     */
    public void addSuffixFilter(String suffix) {
        if (!ValueUtils.isEmpty(suffix))
            this.abstractFileFilterList.add(new SuffixFileFilter(suffix, IOCase.INSENSITIVE));
    }
}
