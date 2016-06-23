package org.dangcat.boot.service;

import org.dangcat.commons.io.FileWriter;

/**
 * 文件写入服务。
 * @author dangcat
 * 
 */
public interface FileWriteService
{
    /**
     * 添加文件写入对象。
     * @param fileWriter 文件写入对象。
     */
    public void addFileWriter(FileWriter fileWriter);

    /**
     * 删除文件写入对象。
     * @param fileWriter 文件写入对象。
     */
    public void removeFileWriter(FileWriter fileWriter);
}
