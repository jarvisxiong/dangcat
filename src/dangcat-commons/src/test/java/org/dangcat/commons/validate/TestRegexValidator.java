package org.dangcat.commons.validate;

import junit.framework.Assert;
import org.junit.Test;

public class TestRegexValidator {
    @Test
    public void testEmail() {
        EmailValidator validator = new EmailValidator();
        Assert.assertTrue(validator.isValid("a@sian.com"));
        Assert.assertTrue(validator.isValid("a@b.c"));

        Assert.assertFalse(validator.isValid("a.b"));
        Assert.assertFalse(validator.isValid("@a.com"));
        Assert.assertFalse(validator.isValid("a@"));
        Assert.assertFalse(validator.isValid("a@b"));
        Assert.assertFalse(validator.isValid("a@b."));
    }

    @Test
    public void testGBK() {
        GBKValidator validator = new GBKValidator();
        Assert.assertTrue(validator.isValid("测"));
        Assert.assertTrue(validator.isValid("测试"));
        Assert.assertTrue(validator.isValid("A测试B"));
        Assert.assertTrue(validator.isValid("AB测试"));
        Assert.assertFalse(validator.isValid("AA"));
        Assert.assertFalse(validator.isValid("11"));
    }

    @Test
    public void testIdentity() {
        IdentityValidator validator = new IdentityValidator();
        Assert.assertTrue(validator.isValid("320807140525461"));
        Assert.assertTrue(validator.isValid("320807201405254619"));
        Assert.assertTrue(validator.isValid("32080714052546x"));
        Assert.assertTrue(validator.isValid("32080714052546X"));
        Assert.assertTrue(validator.isValid("32080720140525461x"));
        Assert.assertTrue(validator.isValid("32080720140525461X"));

        Assert.assertFalse(validator.isValid("3208071405254611"));
        Assert.assertFalse(validator.isValid("3208072014052546191"));
        Assert.assertFalse(validator.isValid("a20807140525461"));
        Assert.assertFalse(validator.isValid("b20807201405254619"));
        Assert.assertFalse(validator.isValid("32080714052546A"));
        Assert.assertFalse(validator.isValid("32080714052546a"));
        Assert.assertFalse(validator.isValid("32080720140525461b"));
        Assert.assertFalse(validator.isValid("32080720140525461B"));
    }

    @Test
    public void testMobile() {
        MobileValidator validator = new MobileValidator();
        Assert.assertTrue(validator.isValid("13952006520"));
        Assert.assertTrue(validator.isValid("14952006520"));
        Assert.assertTrue(validator.isValid("15952006520"));
        Assert.assertTrue(validator.isValid("18952006520"));

        Assert.assertFalse(validator.isValid("12952006520"));
        Assert.assertFalse(validator.isValid("159520065201"));
        Assert.assertFalse(validator.isValid("1595200652a"));
        Assert.assertFalse(validator.isValid("a5952006520"));
        Assert.assertFalse(validator.isValid("15952_06520"));
        Assert.assertFalse(validator.isValid("25952006520"));
    }

    @Test
    public void testName() {
        NameValidator validator = new NameValidator();
        Assert.assertTrue(validator.isValid("a"));
        Assert.assertTrue(validator.isValid("a_"));
        Assert.assertTrue(validator.isValid("a_b-c"));
        Assert.assertTrue(validator.isValid("1_234"));
        Assert.assertTrue(validator.isValid("a.b.c"));
        Assert.assertTrue(validator.isValid("C._AB0"));

        Assert.assertFalse(validator.isValid("*"));
        Assert.assertFalse(validator.isValid("a+b"));
        Assert.assertFalse(validator.isValid("11/22"));
        Assert.assertFalse(validator.isValid("1A&C"));
    }

    @Test
    public void testNo() {
        NoValidator validator = new NoValidator(5, 10);
        Assert.assertTrue(validator.isValid("a12bc"));
        Assert.assertTrue(validator.isValid("abcda"));
        Assert.assertTrue(validator.isValid("12345"));
        Assert.assertTrue(validator.isValid("abcdefghij"));
        Assert.assertTrue(validator.isValid("1234567890"));
        Assert.assertTrue(validator.isValid("123_567890"));

        Assert.assertFalse(validator.isValid("/*&"));
        Assert.assertFalse(validator.isValid("a+b"));
        Assert.assertFalse(validator.isValid("11/22"));
        Assert.assertFalse(validator.isValid("1A&C"));
        Assert.assertFalse(validator.isValid("a12b"));
        Assert.assertFalse(validator.isValid("abcdefghij1"));
        Assert.assertFalse(validator.isValid("12345678901"));
    }

    @Test
    public void testPort() {
        PortValidator validator = new PortValidator();
        Assert.assertTrue(validator.isValid("1"));
        Assert.assertTrue(validator.isValid("65535"));
        Assert.assertTrue(validator.isValid("65534"));

        Assert.assertFalse(validator.isValid("0"));
        Assert.assertFalse(validator.isValid("65536"));
        Assert.assertFalse(validator.isValid("165535"));
    }

    @Test
    public void testTel() {
        TelValidator validator = new TelValidator();
        Assert.assertTrue(validator.isValid("12345678"));
        Assert.assertTrue(validator.isValid("0511-12345678"));
        Assert.assertTrue(validator.isValid("021-12345678"));

        Assert.assertFalse(validator.isValid("123456781"));
        Assert.assertFalse(validator.isValid("1234567"));
        Assert.assertFalse(validator.isValid("05111-12345678"));
        Assert.assertFalse(validator.isValid("02-12345678"));
        Assert.assertFalse(validator.isValid("05111-1234567"));
        Assert.assertFalse(validator.isValid("021-123456781"));
    }
}
