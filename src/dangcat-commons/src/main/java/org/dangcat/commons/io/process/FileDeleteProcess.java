package org.dangcat.commons.io.process;

import org.dangcat.commons.io.FileUtils;

import java.io.File;

/**
 * 文件删除进程。
 */
public class FileDeleteProcess extends FileProcess {
    @Override
    protected long getFileSize(File file) {
        return 1l;
    }

    @Override
    protected void process(File file) {
        FileUtils.delete(file);
        if (this.getLogger().isDebugEnabled())
            this.getLogger().debug("delete " + file.getAbsolutePath());
    }
}
