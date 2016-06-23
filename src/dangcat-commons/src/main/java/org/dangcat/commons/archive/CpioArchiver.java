package org.dangcat.commons.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;

/**
 * Cpio—πÀı°£
 * @author dangcat
 * 
 */
class CpioArchiver extends Archiver
{
    @Override
    protected ArchiveEntry createArchiveEntry(String name, long size)
    {
        return new CpioArchiveEntry(name, size);
    }

    @Override
    protected String getArchiverName()
    {
        return ArchiveStreamFactory.CPIO;
    }
}
