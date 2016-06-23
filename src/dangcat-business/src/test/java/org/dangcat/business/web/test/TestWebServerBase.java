package org.dangcat.business.web.test;

import org.dangcat.boot.test.SystemTestBase;
import org.dangcat.business.web.WebServer;
import org.junit.BeforeClass;

public class TestWebServerBase extends SystemTestBase
{
    public static final String SERVICE_NAME = "dangcat";

    @BeforeClass
    public static void start()
    {
        launcher(WebServer.class, SERVICE_NAME);
    }
}
