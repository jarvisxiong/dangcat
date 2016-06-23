package org.dangcat.commons.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.dangcat.commons.utils.ValueUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Jar—πÀı°£
 *
 * @author dangcat
 */
class TarArchiver extends Archiver {
    @Override
    protected ArchiveEntry createArchiveEntry(String name, long size) {
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(name);
        tarArchiveEntry.setSize(size);
        return tarArchiveEntry;
    }

    @Override
    protected InputStream decorateInputStream(InputStream inputStream) throws IOException {
        InputStream decorateInputStream = null;
        if (!ValueUtils.isEmpty(this.getEncoding()))
            decorateInputStream = new TarArchiveInputStream(inputStream, this.getEncoding());
        else
            decorateInputStream = super.decorateInputStream(inputStream);
        return decorateInputStream;
    }

    @Override
    protected OutputStream decorateOutputStream(OutputStream outputStream) throws IOException {
        OutputStream decorateOutputStream = null;
        if (!ValueUtils.isEmpty(this.getEncoding()))
            decorateOutputStream = new TarArchiveOutputStream(outputStream, this.getEncoding());
        else
            decorateOutputStream = super.decorateOutputStream(outputStream);
        return decorateOutputStream;
    }

    @Override
    protected String getArchiverName() {
        return ArchiveStreamFactory.TAR;
    }
}
