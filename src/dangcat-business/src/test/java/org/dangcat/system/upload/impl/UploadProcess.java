package org.dangcat.system.upload.impl;

import org.dangcat.web.invoke.InvokeProcess;

public class UploadProcess extends InvokeProcess
{
    private static final String ANALYZE = "analyze";
    private static final String PROCESS = "process";
    private static final String STORE = "store";

    public UploadProcess()
    {
        super(UPLOAD, ANALYZE, PROCESS, STORE);
    }
}
