package org.dangcat.system.upload;

import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceInformation;
import org.dangcat.framework.service.annotation.JndiName;
import org.dangcat.system.upload.impl.UploadProcess;
import org.dangcat.web.annotation.InvokeProcess;

import java.io.File;
import java.io.InputStream;

/**
 * 文件上传。
 *
 * @author Administrator
 */
@JndiName(module = "System", name = "UploadDemo")
public interface UploadDemoService {
    /**
     * 上传字节数组。
     */
    @InvokeProcess(UploadProcess.class)
    ServiceInformation uploadBytes(@Parameter(name = "bytes") byte[] bytes);

    /**
     * 上传文件。
     */
    @InvokeProcess(UploadProcess.class)
    ServiceInformation uploadFile(@Parameter(name = "file") File file);

    /**
     * 上传数据流。
     */
    @InvokeProcess(UploadProcess.class)
    ServiceInformation uploadInputStream(@Parameter(name = "inputStream") InputStream inputStream);
}
