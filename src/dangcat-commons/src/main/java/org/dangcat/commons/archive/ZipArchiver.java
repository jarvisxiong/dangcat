package org.dangcat.commons.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.*;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ZIPÑ¹Ëõ¡£
 * @author dangcat
 * 
 */
class ZipArchiver extends Archiver
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
        return new ZipArchiveEntry(name);
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
    protected InputStream decorateInputStream(InputStream inputStream) throws IOException
    {
        InputStream decorateInputStream = null;
        if (!ValueUtils.isEmpty(this.getEncoding()))
            decorateInputStream = new ZipArchiveInputStream(inputStream, this.getEncoding(), true);
        else
            decorateInputStream = super.decorateInputStream(inputStream);
        return decorateInputStream;
    }

    @Override
    protected OutputStream decorateOutputStream(OutputStream outputStream) throws IOException
    {
        OutputStream decorateOutputStream = super.decorateOutputStream(outputStream);
        if (decorateOutputStream instanceof ZipArchiveOutputStream)
        {
            ZipArchiveOutputStream zipArchiveOutputStream = (ZipArchiveOutputStream) decorateOutputStream;
            if (!ValueUtils.isEmpty(this.getEncoding()))
                zipArchiveOutputStream.setEncoding(this.getEncoding());
            zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);
        }
        return decorateOutputStream;
    }

    @Override
    protected String getArchiverName()
    {
        return ArchiveStreamFactory.ZIP;
    }

    @Override
    protected InputStream getInputStream(ArchiveInputStream archiveInputStream, ArchiveEntry archiveEntry) throws IOException
    {
        ZipArchiveEntry zipArchiveEntry = this.zipFile.getEntry(archiveEntry.getName());
        return this.zipFile.getInputStream(zipArchiveEntry);
    }
}
