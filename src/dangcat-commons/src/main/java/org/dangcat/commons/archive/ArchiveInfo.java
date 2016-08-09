package org.dangcat.commons.archive;

/**
 * 归档信息。
 *
 * @author dangcat
 */
class ArchiveInfo {
    private String archiverType = null;
    private String compressType = null;
    private String fileExtName = null;

    ArchiveInfo(String extName, String compressType, String archiverType) {
        this.fileExtName = extName;
        this.compressType = compressType;
        this.archiverType = archiverType;
    }

    /**
     * 产生归档对象。
     */
    protected Archiver createArchiver() {
        if (ArchiveType.zip.equalsIgnoreCase(this.archiverType))
            return new ZipArchiver();
        if (ArchiveType.tar.equalsIgnoreCase(this.archiverType))
            return new TarArchiver();
        if (ArchiveType.jar.equalsIgnoreCase(this.archiverType))
            return new JarArchiver();
        if (ArchiveType.cpio.equalsIgnoreCase(this.archiverType))
            return new CpioArchiver();
        return null;
    }

    protected String getArchiverType() {
        return archiverType;
    }

    protected String getCompressType() {
        return compressType;
    }

    protected String getFileExtName() {
        return fileExtName;
    }

    protected void setFileExtName(String fileExtName) {
        this.fileExtName = fileExtName;
    }
}
