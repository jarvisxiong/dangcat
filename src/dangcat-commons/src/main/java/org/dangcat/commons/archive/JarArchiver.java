package org.dangcat.commons.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Jar—πÀı°£
 * @author dangcat
 * 
 */
class JarArchiver extends Archiver
{
    private ZipFile zipFile = null;

    @Override
    protected void close()
    {
        try
        {
            if (this.zipFile != null)
                this.zipFile.close();
        }
        catch (IOException e)
        {
        }
        super.close();
    }

    @Override
    protected ArchiveEntry createArchiveEntry(String name, long size)
    {
        return new JarArchiveEntry(name);
    }

    @Override
    protected InputStream createInputStream(File file) throws IOException
    {
        if (!ValueUtils.isEmpty(this.getEncoding()))
            this.zipFile = new ZipFile(file, this.getEncoding());
        else
            this.zipFile = new ZipFile(file);
        return super.createInputStream(file);
    }

    @Override
    protected String getArchiverName()
    {
        return ArchiveStreamFactory.JAR;
    }

    @Override
    protected InputStream getInputStream(ArchiveInputStream archiveInputStream, ArchiveEntry archiveEntry) throws IOException
    {
        ZipArchiveEntry zipArchiveEntry = this.zipFile.getEntry(archiveEntry.getName());
        return this.zipFile.getInputStream(zipArchiveEntry);
    }
}
