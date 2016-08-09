package org.dangcat.commons.io.process;

import java.io.File;

/**
 * 文件拷贝回调接口。
 */
public interface FileCopyCallback {
    /**
     * 拷贝后回调接口。
     *
     * @param source 来源文件。
     * @param dest   目标文件。
     */
    void afterCopy(File source, File dest);

    /**
     * 拷贝前回调接口。
     *
     * @param source 来源文件。
     * @param dest   目标文件。
     * @return 如果取消返回false，否则返回true。
     */
    boolean beforeCopy(File source, File dest);
}
