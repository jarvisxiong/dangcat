package org.dangcat.commons.archive;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.FileComparer;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestCompressor {
    protected static final Logger logger = Logger.getLogger(TestCompressor.class);

    private File getCompressFile(String fileExtName) {
        File file = new File("log" + File.separator + "compress" + fileExtName);
        if (file.exists())
            FileUtils.delete(file);
        return file;
    }

    private File getLocalDirectory() {
        return new File(FileUtils.getResourcePath(TestCompressor.class, "org/dangcat/"));
    }

    private File getUpcompressDirectory() {
        File dir = new File("log" + File.separator + "compress");
        if (dir.exists())
            FileUtils.delete(dir);
        FileUtils.mkdir(dir.getAbsolutePath());
        return dir;
    }

    @Test
    public void testCompress() throws IOException {
        for (String archiveType : ArchiveType.getAllArchiveTypes()) {
            // 压缩本地目录到指定文件
            File compressFile = this.getCompressFile(archiveType);
            Compressor compressor = new Compressor();
            compressor.addArchiveEntry(this.getLocalDirectory());
            compressor.compress(compressFile);

            logger.info(archiveType + " File Compress TotalSize: " + compressor.getTotalSize() + ", Compress size: " + compressor.getCompressSize() + ", Compress rate = "
                    + String.format("%1$d%%", compressor.getCompressRate()));

            // 测试解压缩
            File upcompressDir = this.getUpcompressDirectory();
            compressor.decompress(compressFile, upcompressDir);
            File destDir = new File(upcompressDir.getAbsolutePath() + File.separator + "dangcat");
            Assert.assertTrue(FileComparer.compare(destDir, this.getLocalDirectory(), null));
        }
    }
}
