package org.dangcat.commons.io.process;

import java.io.File;

import junit.framework.Assert;

import org.dangcat.commons.io.FileCompare;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.io.process.FileCopyProcess;
import org.junit.Test;

public class TestFileCopyProcess
{
    @Test
    public void testCopyDirectory()
    {
        File srcFile = new File("./src");
        File destFile = new File("./log/src");
        FileUtils.delete(destFile);
        FileCopyProcess fileCopyProcess = new FileCopyProcess();
        fileCopyProcess.addTask(srcFile, destFile);
        fileCopyProcess.run();
        Assert.assertEquals(100, fileCopyProcess.getFinishedPercent());
        Assert.assertTrue(FileCompare.compare(srcFile, destFile, true));

        this.testDeleteFile(destFile);
    }

    @Test
    public void testCopyFile()
    {
        File srcFile = new File("./src/main/resources/META-INF/MANIFEST.MF");
        File destFile = new File("./log/META-INF/MANIFEST.MF");
        FileUtils.delete(destFile);
        FileCopyProcess fileCopyProcess = new FileCopyProcess();
        fileCopyProcess.addTask(srcFile, destFile);
        fileCopyProcess.run();
        Assert.assertEquals(100, fileCopyProcess.getFinishedPercent());
        Assert.assertTrue(FileCompare.compare(srcFile, destFile, true));

        this.testDeleteFile(destFile);
    }

    private void testDeleteFile(File... files)
    {
        FileDeleteProcess fileDeleteProcess = new FileDeleteProcess()
        {
            @Override
            protected void afterProcess(File file)
            {
                if (this.getFinishedPercent() == 100)
                    System.out.println(this.getFinishedPercent() + "%");
                super.afterProcess(file);
            }
        };
        fileDeleteProcess.addTasks(files);
        fileDeleteProcess.run();
        Assert.assertEquals(100, fileDeleteProcess.getFinishedPercent());
        for (File file : files)
            Assert.assertFalse(file.exists());
    }
}
