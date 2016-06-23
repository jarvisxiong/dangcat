package org.dangcat.commons.utils;

import junit.framework.Assert;
import org.junit.Test;

public class TestInetAddressUtils {
    @Test
    public void testInet4Address() {
        Assert.assertTrue(InetAddressUtils.isInet4Address("218.197.4.76"));
        Assert.assertTrue(InetAddressUtils.isInet4Address("255.255.255.255"));
        Assert.assertTrue(InetAddressUtils.isInet4Address("0.0.0.0"));
        Assert.assertFalse(InetAddressUtils.isInet4Address("1.4.5"));
    }

    @Test
    public void testInet6Address() {
        Assert.assertTrue(InetAddressUtils.isInet6Address("202::2012:A:CDCD:2012"));
        Assert.assertTrue(InetAddressUtils.isInet6Address("2009:02:12::9"));
        Assert.assertTrue(InetAddressUtils.isInet6Address("2009::5"));
    }

    @Test
    public void testInetAddress() {
        Assert.assertTrue(InetAddressUtils.isInetAddress("218.197.4.76"));
        Assert.assertTrue(InetAddressUtils.isInetAddress("202::2012:A:CDCD:2012"));
        Assert.assertTrue(InetAddressUtils.isInetAddress("2009:02:12::9"));
        Assert.assertTrue(InetAddressUtils.isInetAddress("2009::5"));

        Assert.assertFalse(InetAddressUtils.isInetAddress("1.4.5"));
        Assert.assertFalse(InetAddressUtils.isInetAddress("10:10::1::1"));
        Assert.assertFalse(InetAddressUtils.isInetAddress("20BC:0df:dfg2"));
    }
}
