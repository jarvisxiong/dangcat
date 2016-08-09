package org.dangcat.system.upload.impl;

import org.apache.log4j.Logger;
import org.dangcat.framework.exception.ServiceInformation;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.system.upload.UploadDemoService;
import org.dangcat.web.invoke.InvokeProcess;
import org.dangcat.web.invoke.InvokeStep;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传。
 *
 * @author Administrator
 */
public class UploadDemoServiceImpl implements UploadDemoService {
    protected static final Logger logger = Logger.getLogger(UploadDemoService.class);

    private void executeProcess() {
        InvokeProcess invokeProcess = ServiceContext.getInstance().getParam(InvokeProcess.class);
        if (invokeProcess != null) {
            for (InvokeStep invokeStep : invokeProcess.getInvokeSteps()) {
                if (invokeStep.getStatus() != InvokeStep.FINISHED)
                    invokeStep.setTotal(100);
            }

            for (InvokeStep invokeStep : invokeProcess.getInvokeSteps()) {
                if (invokeStep.getStatus() != InvokeStep.FINISHED) {
                    for (int i = 0; i < invokeStep.getTotal() && !invokeStep.isCancel(); i++) {
                        invokeStep.setCurrent(i + 1);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * 上传字节数组。
     */
    public ServiceInformation uploadBytes(byte[] bytes) {
        this.executeProcess();
        String result = "uploadBytes receive bytes " + bytes.length;
        logger.info(result);
        return new ServiceInformation(result);
    }

    /**
     * 上传文件。
     */
    public ServiceInformation uploadFile(File file) {
        this.executeProcess();
        String result = "uploadFile receive file " + file.getAbsolutePath();
        logger.info(result);
        return new ServiceInformation(result);
    }

    /**
     * 上传数据流。
     */
    public ServiceInformation uploadInputStream(InputStream inputStream) {
        this.executeProcess();
        String result = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int length = 0;
        byte[] buffer = new byte[1024];
        try {
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);
            result = "uploadInputStream receive " + outputStream.size();
            logger.info(result);
        } catch (IOException e) {
            logger.error(this, e);
        }
        return new ServiceInformation(result);
    }
}
