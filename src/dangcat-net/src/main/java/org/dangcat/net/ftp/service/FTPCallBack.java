package org.dangcat.net.ftp.service;

import java.io.File;

import org.dangcat.net.ftp.service.impl.FTPContext;

/**
 * 下载回调接口。
 * @author dangcat
 * 
 */
public interface FTPCallBack
{
    /**
     * 下载失败。
     * @param ftpContext FTP会话。
     * @param localPath 本地路径。
     * @param remotePath 远端路径。
     * @param exception 错误异常。
     */
    public void onFailure(FTPContext ftpContext, File localPath, String remotePath, Exception exception);

    /**
     * 下载成功。
     * @param ftpContext FTP会话。
     * @param localPath 本地路径。
     * @param remotePath 远端路径。
     */
    public void onSucess(FTPContext ftpContext, File localPath, String remotePath);
}
