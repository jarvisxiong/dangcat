package org.dangcat.boot.service.impl;

import org.junit.Assert;
import org.junit.Test;

public class TestWatchThreadExecutor {
    @Test
    public void testBlock() {
        WatchBlockRunner watchBlockRunner = new WatchBlockRunner();
        long beginTime = System.currentTimeMillis();
        WatchThreadExecutor.getInstance().submit(watchBlockRunner);
        Assert.assertFalse(watchBlockRunner.isSucess());
        long timeLength = System.currentTimeMillis() - beginTime - watchBlockRunner.getTimeOutLength();
        System.out.println("timeLength = " + timeLength);
        Assert.assertTrue(timeLength > 0 && timeLength < 2000);
    }

    @Test
    public void testNormal() {
        WatchNormalRunner watchNormalRunner = new WatchNormalRunner();
        WatchThreadExecutor.getInstance().submit(watchNormalRunner);
        Assert.assertTrue(watchNormalRunner.isSucess());
    }
}
