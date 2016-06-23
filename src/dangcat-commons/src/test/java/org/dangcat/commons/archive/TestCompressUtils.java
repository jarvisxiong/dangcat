package org.dangcat.commons.archive;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.ParseUtils;
import org.junit.Test;

import java.io.*;

public class TestCompressUtils {
    protected static final Logger logger = Logger.getLogger(TestCompressUtils.class);
    private static final String packetText = "01 01 00 47 2a ee 86 f0 8d 0d 55 96 9c a5 97 8e " + "0d 33 67 a2 01 08 66 6c 6f 70 73 79 03 13 16 e9 "
            + "75 57 c3 16 18 58 95 f2 93 ff 63 44 07 72 75 04 " + "06 c0 a8 01 10 05 06 00 00 00 14 06 06 00 00 00 " + "02 07 06 00 00 00 01 ";

    private int getCompressRate(int totalSize, int compressSize) {
        return (int) (totalSize == 0 ? 0 : 100 - compressSize * 100.0 / totalSize);
    }

    private byte[] getDataBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < 10; i++)
            byteArrayOutputStream.write(ParseUtils.parseHex(packetText));
        return byteArrayOutputStream.toByteArray();
    }

    private File getFileName(String fileExtName) {
        File file = new File("log" + File.separator + "filecompress" + fileExtName);
        if (file.exists())
            FileUtils.delete(file);
        return file;
    }

    private File getSourceFile() throws IOException {
        File file = this.getFileName(".txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] dataBytes = this.getDataBytes();
        fileOutputStream.write(dataBytes);
        fileOutputStream.close();
        return file;
    }

    @Test
    public void test_bz() throws IOException {
        this.testDataCompress(CompressUtils.bz);
        this.testFileCompress(CompressUtils.bz);
    }

    @Test
    public void test_bz2() throws IOException {
        this.testDataCompress(CompressUtils.bz2);
        this.testFileCompress(CompressUtils.bz2);
    }

    @Test
    public void test_gz() throws IOException {
        this.testDataCompress(CompressUtils.gz);
        this.testFileCompress(CompressUtils.gz);
    }

    @Test
    public void test_xz() throws IOException {
        this.testDataCompress(CompressUtils.xz);
        this.testFileCompress(CompressUtils.xz);
    }

    @Test
    public void test_z() throws IOException {
        this.testDataCompress(CompressUtils.z);
    }

    private void testDataCompress(String name) throws IOException {
        byte[] dataBytes = this.getDataBytes();

        // 压缩数组
        byte[] compressData = CompressUtils.compress(name, dataBytes);
        logger.info(CompressUtils.compressTypeMap.get(name) + " " + name + " Data Compress TotalSize: " + dataBytes.length + ", Compress size: " + compressData.length + ", Compress rate = "
                + String.format("%1$d%%", this.getCompressRate(dataBytes.length, compressData.length)));

        // 解压缩数组
        byte[] upcompressData = CompressUtils.decompress(name, compressData);

        // 测试解压缩后数据是否正确
        Assert.assertEquals(dataBytes.length, upcompressData.length);
        for (int i = 0; i < dataBytes.length; i++)
            Assert.assertEquals(dataBytes[i], upcompressData[i]);
    }

    private void testFileCompress(String name) throws IOException {
        File sourceFile = this.getSourceFile();

        // 压缩文件
        File compressFile = CompressUtils.compress(name, sourceFile);
        Assert.assertTrue(compressFile.exists());

        // 解压缩文件
        File uncompressFile = CompressUtils.decompress(compressFile);
        logger.info(CompressUtils.compressTypeMap.get(name) + " " + name + " File Compress TotalSize: " + sourceFile.length() + ", Compress size: " + compressFile.length() + ", Compress rate = "
                + String.format("%1$d%%", this.getCompressRate((int) sourceFile.length(), (int) compressFile.length())));

        // 测试解压缩数据是否正确
        byte[] sourceData = this.getDataBytes();
        byte[] uncompressData = new byte[(int) uncompressFile.length()];
        FileInputStream fileInputStream = new FileInputStream(uncompressFile);
        fileInputStream.read(uncompressData);
        FileUtils.close(fileInputStream);
        Assert.assertEquals(sourceData.length, uncompressData.length);
        for (int i = 0; i < sourceData.length; i++)
            Assert.assertEquals(sourceData[i], uncompressData[i]);

        sourceFile.delete();
    }
}
