package org.dangcat.commons.os.service;

import org.dangcat.commons.os.service.SystemServiceUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestSystemService
{
    @Test
    public void testServiceExists()
    {
        Assert.assertFalse(SystemServiceUtils.exists("mysql-dangcat1"));
        Assert.assertTrue(SystemServiceUtils.exists("mysql-dangcat"));
        Assert.assertTrue(SystemServiceUtils.isRunning("mysql-dangcat"));
    }
}
