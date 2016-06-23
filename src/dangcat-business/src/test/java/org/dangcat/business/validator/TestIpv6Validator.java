package org.dangcat.business.validator;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestIpv6Validator
{
    private void assertIPV6(String ipv6, boolean result)
    {
        Pattern pattern = Pattern.compile(Ipv6Validator.IPV6_Pattern);
        Matcher matcher = pattern.matcher(ipv6);
        if (result)
            Assert.assertTrue(matcher.matches());
        else
            Assert.assertFalse(matcher.matches());
    }

    @Test
    public void validator()
    {
        String ipv6 = "";
        assertIPV6(ipv6, false);

        ipv6 = "2001:0DB8:02de:0000:0000:0000:0000:0e13";// multicast,compressed
        assertIPV6(ipv6, true);

        ipv6 = "::1";// multicast, compressed
        assertIPV6(ipv6, true);

        ipv6 = "::";// unspecified, compressed, non-routable
        assertIPV6(ipv6, true);

        ipv6 = "0:0:0:0:0:0:0:1";// loopback, full
        assertIPV6(ipv6, true);

        ipv6 = "0:0:0:0:0:0:0:0";// unspecified, full
        assertIPV6(ipv6, true);

        ipv6 = "2001:DB8:0:0:8:800:200C:417A";// unicast, full
        assertIPV6(ipv6, true);

        ipv6 = "FF01:0:0:0:0:0:0:101";// multicast, full
        assertIPV6(ipv6, true);

        ipv6 = "2001:DB8::8:800:200C:417A";// unicast, compressed
        assertIPV6(ipv6, true);

        ipv6 = "FF01::101";// multicast, compressed
        assertIPV6(ipv6, true);

        ipv6 = "2001:DB8:0:0:8:800:200C:417A:221";// unicast, full
        assertIPV6(ipv6, false);

        ipv6 = "FF01::101::2";// multicast, compressed
        assertIPV6(ipv6, false);

        ipv6 = "fe80::217:f2ff:fe07:ed62";
        assertIPV6(ipv6, true);

        ipv6 = "2001:0000:1234:0000:0000:C1C0:ABCD:0876";
        assertIPV6(ipv6, true);
        ipv6 = "3ffe:0b00:0000:0000:0001:0000:0000:000a";
        assertIPV6(ipv6, true);
        ipv6 = "FF02:0000:0000:0000:0000:0000:0000:0001";
        assertIPV6(ipv6, true);
        ipv6 = "0000:0000:0000:0000:0000:0000:0000:0001";
        assertIPV6(ipv6, true);
        ipv6 = "0000:0000:0000:0000:0000:0000:0000:0000";
        assertIPV6(ipv6, true);

        // extra 0 not allowed!
        ipv6 = "02001:0000:1234:0000:0000:C1C0:ABCD:0876";
        assertIPV6(ipv6, false);

        // extra 0 not allowed!
        ipv6 = "2001:0000:1234:0000:00001:C1C0:ABCD:0876";
        assertIPV6(ipv6, false);

        // ipv6=" 2001:0000:1234:0000:0000:C1C0:ABCD:0876"; // leading space
        // assertIPV6(ipv6, true);

        // ipv6="2001:0000:1234:0000:0000:C1C0:ABCD:0876"; // trailing space
        // assertIPV6(ipv6, true);

        // ipv6=" 2001:0000:1234:0000:0000:C1C0:ABCD:0876  "; // leading and
        // trailing space
        // assertIPV6(ipv6, true);

        // junk after valid address
        ipv6 = "2001:0000:1234:0000:0000:C1C0:ABCD:0876  0";
        assertIPV6(ipv6, false);

        ipv6 = "2001:0000:1234: 0000:0000:C1C0:ABCD:0876"; // internal space
        assertIPV6(ipv6, false);

        ipv6 = "3ffe:0b00:0000:0001:0000:0000:000a"; // seven segments
        assertIPV6(ipv6, false);
        ipv6 = "FF02:0000:0000:0000:0000:0000:0000:0000:0001"; // nine segments
        assertIPV6(ipv6, false);

        ipv6 = "3ffe:b00::1::a"; // double "::"
        assertIPV6(ipv6, false);

        ipv6 = "::1111:2222:3333:4444:5555:6666::"; // double "::"
        assertIPV6(ipv6, false);

        ipv6 = "2::10";
        assertIPV6(ipv6, true);

        ipv6 = "ff02::1";
        assertIPV6(ipv6, true);
        ipv6 = "fe80::";
        assertIPV6(ipv6, true);
        ipv6 = "2002::";
        assertIPV6(ipv6, true);
        ipv6 = "2001:db8::";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8:1234::";
        assertIPV6(ipv6, true);
        ipv6 = "::ffff:0:0";
        assertIPV6(ipv6, true);
        ipv6 = "::1";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3:4:5:6:7:8";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3:4:5:6::8";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3:4:5::8";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3:4::8";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3::8";
        assertIPV6(ipv6, true);
        ipv6 = "1:2::8";
        assertIPV6(ipv6, true);

        ipv6 = "1::8";
        assertIPV6(ipv6, true);

        ipv6 = "1::2:3:4:5:6:7";
        assertIPV6(ipv6, true);

        ipv6 = "1::2:3:4:5:6";
        assertIPV6(ipv6, true);

        ipv6 = "1::2:3:4:5";
        assertIPV6(ipv6, true);

        ipv6 = "1::2:3:4";
        assertIPV6(ipv6, true);

        ipv6 = "1::2:3";
        assertIPV6(ipv6, true);

        ipv6 = "1::8";
        assertIPV6(ipv6, true);

        ipv6 = "::2:3:4:5:6:7:8";
        assertIPV6(ipv6, true);

        ipv6 = "::2:3:4:5:6:7";
        assertIPV6(ipv6, true);

        ipv6 = "::2:3:4:5:6";
        assertIPV6(ipv6, true);

        ipv6 = "::2:3:4:5";
        assertIPV6(ipv6, true);

        ipv6 = "::2:3:4";
        assertIPV6(ipv6, true);

        ipv6 = "::2:3";
        assertIPV6(ipv6, true);

        ipv6 = "::8";
        assertIPV6(ipv6, true);

        ipv6 = "1:2:3:4:5:6::";
        assertIPV6(ipv6, true);

        ipv6 = "1:2:3:4:5::";
        assertIPV6(ipv6, true);

        ipv6 = "1:2:3:4::";
        assertIPV6(ipv6, true);

        ipv6 = "1:2:3::";
        assertIPV6(ipv6, true);

        ipv6 = "1:2::";
        assertIPV6(ipv6, true);

        ipv6 = "1::";
        assertIPV6(ipv6, true);

        ipv6 = "1:2:3:4:5::7:8";
        assertIPV6(ipv6, true);

        ipv6 = "1:2:3::4:5::7:8"; // Double "::"
        assertIPV6(ipv6, false);

        ipv6 = "12345::6:7:8";
        assertIPV6(ipv6, false);

        ipv6 = "1:2:3:4::7:8";
        assertIPV6(ipv6, true);

        ipv6 = "1:2:3::7:8";
        assertIPV6(ipv6, true);

        ipv6 = "1:2::7:8";
        assertIPV6(ipv6, true);

        ipv6 = "1::7:8";
        assertIPV6(ipv6, true);

        // IPv4 addresses as dotted-quads
        ipv6 = "1:2:3:4:5:6:1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3:4:5::1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3:4::1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3::1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1:2::1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1::1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3:4::5:1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1:2:3::5:1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1:2::5:1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1::5:1.2.3.4";
        assertIPV6(ipv6, true);
        ipv6 = "1::5:11.22.33.44";
        assertIPV6(ipv6, true);
        ipv6 = "1::5:400.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:260.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:256.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.256.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.2.256.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.2.3.256";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:300.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.300.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.2.300.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.2.3.300";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:900.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.900.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.2.900.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:1.2.3.900";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:300.300.300.300";
        assertIPV6(ipv6, false);
        ipv6 = "1::5:3000.30.30.30";
        assertIPV6(ipv6, false);
        ipv6 = "1::400.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::260.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::256.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.256.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.2.256.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.2.3.256";
        assertIPV6(ipv6, false);
        ipv6 = "1::300.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.300.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.2.300.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.2.3.300";
        assertIPV6(ipv6, false);
        ipv6 = "1::900.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.900.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.2.900.4";
        assertIPV6(ipv6, false);
        ipv6 = "1::1.2.3.900";
        assertIPV6(ipv6, false);
        ipv6 = "1::300.300.300.300";
        assertIPV6(ipv6, false);
        ipv6 = "1::3000.30.30.30";
        assertIPV6(ipv6, false);
        ipv6 = "::400.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::260.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::256.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.256.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2.256.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2.3.256";
        assertIPV6(ipv6, false);
        ipv6 = "::300.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.300.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2.300.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2.3.300";
        assertIPV6(ipv6, false);
        ipv6 = "::900.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.900.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2.900.4";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2.3.900";
        assertIPV6(ipv6, false);
        ipv6 = "::300.300.300.300";
        assertIPV6(ipv6, false);
        ipv6 = "::3000.30.30.30";
        assertIPV6(ipv6, false);
        ipv6 = "fe80::217:f2ff:254.7.237.98";
        assertIPV6(ipv6, true);
        ipv6 = "::ffff:192.168.1.26";
        assertIPV6(ipv6, true);
        ipv6 = "2001:1:1:1:1:1:255Z255X255Y255"; // garbage instead of "." in
        // IPv4
        assertIPV6(ipv6, false);

        ipv6 = "::ffff:192x168.1.26"; // ditto
        assertIPV6(ipv6, false);

        ipv6 = "::ffff:192.168.1.1";
        assertIPV6(ipv6, true);

        ipv6 = "0:0:0:0:0:0:13.1.68.3";// IPv4-compatible IPv6 address, full,
        // deprecated
        assertIPV6(ipv6, true);
        ipv6 = "0:0:0:0:0:FFFF:129.144.52.38";// IPv4-mapped IPv6 address, full
        assertIPV6(ipv6, true);

        ipv6 = "::13.1.68.3";// IPv4-compatible IPv6 address, compressed,
        // deprecated
        assertIPV6(ipv6, true);

        ipv6 = "::FFFF:129.144.52.38";// IPv4-mapped IPv6 address, compressed
        assertIPV6(ipv6, true);

        ipv6 = "fe80:0:0:0:204:61ff:254.157.241.86";
        assertIPV6(ipv6, true);
        ipv6 = "fe80::204:61ff:254.157.241.86";
        assertIPV6(ipv6, true);
        ipv6 = "::ffff:12.34.56.78";
        assertIPV6(ipv6, true);
        ipv6 = "::ffff:2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::ffff:257.1.2.3";
        assertIPV6(ipv6, false);
        ipv6 = "1.2.3.4";
        assertIPV6(ipv6, false);

        ipv6 = "1.2.3.4:1111:2222:3333:4444::5555";
        assertIPV6(ipv6, false); // Aeron
        ipv6 = "1.2.3.4:1111:2222:3333::5555";
        assertIPV6(ipv6, false);
        ipv6 = "1.2.3.4:1111:2222::5555";
        assertIPV6(ipv6, false);
        ipv6 = "1.2.3.4:1111::5555";
        assertIPV6(ipv6, false);
        ipv6 = "1.2.3.4::5555";
        assertIPV6(ipv6, false);
        ipv6 = "1.2.3.4::";
        assertIPV6(ipv6, false);

        // Testing IPv4 addresses represented as dotted-quads
        // Leading zero's in IPv4 addresses not allowed: some systems treat the
        // leading "0" in ".086" as the start of an octal number
        // Update: The BNF in RFC-3986 explicitly defines the dec-octet (for
        // IPv4 addresses) not to have a leading zero
        ipv6 = "fe80:0000:0000:0000:0204:61ff:254.157.241.086";
        assertIPV6(ipv6, false);
        ipv6 = "::ffff:192.0.2.128";
        assertIPV6(ipv6, true); // but this is OK, since there's a single
        // digit
        ipv6 = "XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:00.00.00.00";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:000.000.000.000";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:256.256.256.256";
        assertIPV6(ipv6, false);

        // Not testing address with subnet mask
        // ipv6="2001:0DB8:0000:CD30:0000:0000:0000:0000/60";// full, with
        // prefix
        // ipv6="2001:0DB8::CD30:0:0:0:0/60";// compressed, with prefix
        // ipv6="2001:0DB8:0:CD30::/60";// compressed, with prefix //2
        // ipv6="::/128";// compressed, unspecified address type, non-routable
        // ipv6="::1/128";// compressed, loopback address type, non-routable
        // ipv6="FF00::/8";// compressed, multicast address type
        // ipv6="FE80::/10";// compressed, link-local unicast, non-routable
        // ipv6="FEC0::/10";// compressed, site-local unicast, deprecated
        // ipv6="124.15.6.89/60";// standard IPv4, prefix not allowed

        ipv6 = "fe80:0000:0000:0000:0204:61ff:fe9d:f156";
        assertIPV6(ipv6, true);
        ipv6 = "fe80:0:0:0:204:61ff:fe9d:f156";
        assertIPV6(ipv6, true);
        ipv6 = "fe80::204:61ff:fe9d:f156";
        assertIPV6(ipv6, true);
        ipv6 = "::1";
        assertIPV6(ipv6, true);
        ipv6 = "fe80::";
        assertIPV6(ipv6, true);
        ipv6 = "fe80::1";
        assertIPV6(ipv6, true);
        ipv6 = ":";
        assertIPV6(ipv6, false);
        ipv6 = "::ffff:c000:280";
        assertIPV6(ipv6, true);

        // Aeron supplied these test cases
        ipv6 = "1111:2222:3333:4444::5555:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333::5555:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::5555:";
        assertIPV6(ipv6, false);
        ipv6 = "1111::5555:";
        assertIPV6(ipv6, false);
        ipv6 = "::5555:";
        assertIPV6(ipv6, false);
        ipv6 = ":::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:";
        assertIPV6(ipv6, false);
        ipv6 = ":";
        assertIPV6(ipv6, false);

        ipv6 = ":1111:2222:3333:4444::5555";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::5555";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::5555";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::5555";
        assertIPV6(ipv6, false);
        ipv6 = ":::5555";
        assertIPV6(ipv6, false);
        ipv6 = ":::";
        assertIPV6(ipv6, false);

        // Additional test cases
        // from http://rt.cpan.org/Public/Bug/Display.html?id=50693

        ipv6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        assertIPV6(ipv6, true);
        ipv6 = "2001:db8:85a3:0:0:8a2e:370:7334";
        assertIPV6(ipv6, true);
        ipv6 = "2001:db8:85a3::8a2e:370:7334";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8:0000:0000:0000:0000:1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8:0000:0000:0000::1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8:0:0:0:0:1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8:0:0::1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8::1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:db8::1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "0000:0000:0000:0000:0000:0000:0000:0001";
        assertIPV6(ipv6, true);
        ipv6 = "::1";
        assertIPV6(ipv6, true);
        ipv6 = "::ffff:0c22:384e";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8:1234:0000:0000:0000:0000:0000";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0db8:1234:ffff:ffff:ffff:ffff:ffff";
        assertIPV6(ipv6, true);
        ipv6 = "2001:db8:a::123";
        assertIPV6(ipv6, true);
        ipv6 = "fe80::";
        assertIPV6(ipv6, true);

        ipv6 = "123";
        assertIPV6(ipv6, false);
        ipv6 = "ldkfj";
        assertIPV6(ipv6, false);
        ipv6 = "2001::FFD3::57ab";
        assertIPV6(ipv6, false);
        ipv6 = "2001:db8:85a3::8a2e:37023:7334";
        assertIPV6(ipv6, false);
        ipv6 = "2001:db8:85a3::8a2e:370k:7334";
        assertIPV6(ipv6, false);
        ipv6 = "1:2:3:4:5:6:7:8:9";
        assertIPV6(ipv6, false);
        ipv6 = "1::2::3";
        assertIPV6(ipv6, false);
        ipv6 = "1:::3:4:5";
        assertIPV6(ipv6, false);
        ipv6 = "1:2:3::4:5:6:7:8:9";
        assertIPV6(ipv6, false);

        // New from Aeron
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444:5555:6666:7777::";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444:5555:6666::";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444:5555::";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444::";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::";
        assertIPV6(ipv6, true);
        ipv6 = "1111::";
        assertIPV6(ipv6, true);
        // ipv6="::"; assertIPV6(ipv6, true); //duplicate
        ipv6 = "1111:2222:3333:4444:5555:6666::8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444:5555::8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444::8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111::8888";
        assertIPV6(ipv6, true);
        ipv6 = "::8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444:5555::7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444::7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111::7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "::7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444::6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111::6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "::6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111::5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "::5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111::3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "::3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "::2222:3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444:5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444:5555::123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444::123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111::123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "::123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333:4444::6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111::6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "::6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222:3333::5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111::5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "::5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111:2222::4444:5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111::4444:5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "::4444:5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "1111::3333:4444:5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);
        ipv6 = "::2222:3333:4444:5555:6666:123.123.123.123";
        assertIPV6(ipv6, true);

        // Playing with combinations of "0" and "::"
        // NB: these are all sytactically correct, but are bad form
        // because "0" adjacent to "::" should be combined into "::"
        ipv6 = "::0:0:0:0:0:0:0";
        assertIPV6(ipv6, true);
        ipv6 = "::0:0:0:0:0:0";
        assertIPV6(ipv6, true);
        ipv6 = "::0:0:0:0:0";
        assertIPV6(ipv6, true);
        ipv6 = "::0:0:0:0";
        assertIPV6(ipv6, true);
        ipv6 = "::0:0:0";
        assertIPV6(ipv6, true);
        ipv6 = "::0:0";
        assertIPV6(ipv6, true);
        ipv6 = "::0";
        assertIPV6(ipv6, true);
        ipv6 = "0:0:0:0:0:0:0::";
        assertIPV6(ipv6, true);
        ipv6 = "0:0:0:0:0:0::";
        assertIPV6(ipv6, true);
        ipv6 = "0:0:0:0:0::";
        assertIPV6(ipv6, true);
        ipv6 = "0:0:0:0::";
        assertIPV6(ipv6, true);
        ipv6 = "0:0:0::";
        assertIPV6(ipv6, true);
        ipv6 = "0:0::";
        assertIPV6(ipv6, true);
        ipv6 = "0::";
        assertIPV6(ipv6, true);

        // New invalid from Aeron
        // Invalid data
        ipv6 = "XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX";
        assertIPV6(ipv6, false);

        // Too many components
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:8888:9999";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:8888::";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444:5555:6666:7777:8888:9999";
        assertIPV6(ipv6, false);

        // Too few components
        ipv6 = "1111:2222:3333:4444:5555:6666:7777";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222";
        assertIPV6(ipv6, false);
        ipv6 = "1111";
        assertIPV6(ipv6, false);

        // Missing :
        ipv6 = "11112222:3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:22223333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:33334444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:44445555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:55556666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:66667777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:77778888";
        assertIPV6(ipv6, false);

        // Missing : intended for ::
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:";
        assertIPV6(ipv6, false);
        ipv6 = ":";
        assertIPV6(ipv6, false);
        ipv6 = ":8888";
        assertIPV6(ipv6, false);
        ipv6 = ":7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":2222:3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);

        // :::
        ipv6 = ":::2222:3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:::3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:::5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:::8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:::";
        assertIPV6(ipv6, false);

        // Double ::"; assertIPV6(ipv6, false);
        ipv6 = "::2222::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333::5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444:5555::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444:5555:7777::8888";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444:5555:7777:8888::";
        assertIPV6(ipv6, false);

        ipv6 = "1111::3333::5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111::3333:4444::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111::3333:4444:5555::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111::3333:4444:5555:6666::8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111::3333:4444:5555:6666:7777::";
        assertIPV6(ipv6, false);

        ipv6 = "1111:2222::4444::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::4444:5555::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::4444:5555:6666::8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::4444:5555:6666:7777::";
        assertIPV6(ipv6, false);

        ipv6 = "1111:2222:3333::5555::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333::5555:6666::8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333::5555:6666:7777::";
        assertIPV6(ipv6, false);

        ipv6 = "1111:2222:3333:4444::6666::8888";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444::6666:7777::";
        assertIPV6(ipv6, false);

        ipv6 = "1111:2222:3333:4444:5555::7777::";
        assertIPV6(ipv6, false);

        // Too many components"
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:8888:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666::1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444:5555:6666:7777:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:1.2.3.4.5";
        assertIPV6(ipv6, false);

        // Too few components
        ipv6 = "1111:2222:3333:4444:5555:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1.2.3.4";
        assertIPV6(ipv6, false);

        // Missing :
        ipv6 = "11112222:3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:22223333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:33334444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:44445555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:55556666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:66661.2.3.4";
        assertIPV6(ipv6, false);

        // Missing .
        ipv6 = "1111:2222:3333:4444:5555:6666:255255.255.255";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:255.255255.255";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:255.255.255255";
        assertIPV6(ipv6, false);

        // Missing : intended for ::
        ipv6 = ":1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":2222:3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);

        // :::
        ipv6 = ":::2222:3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:::3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:::4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:::5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:::1.2.3.4";
        assertIPV6(ipv6, false);

        // Double ::
        ipv6 = "::2222::4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333::5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444:5555::1.2.3.4";
        assertIPV6(ipv6, false);

        ipv6 = "1111::3333::5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111::3333:4444::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111::3333:4444:5555::1.2.3.4";
        assertIPV6(ipv6, false);

        ipv6 = "1111:2222::4444::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::4444:5555::1.2.3.4";
        assertIPV6(ipv6, false);

        ipv6 = "1111:2222:3333::5555::1.2.3.4";
        assertIPV6(ipv6, false);

        // Missing parts
        ipv6 = "::.";
        assertIPV6(ipv6, false);
        ipv6 = "::..";
        assertIPV6(ipv6, false);
        ipv6 = "::...";
        assertIPV6(ipv6, false);
        ipv6 = "::1...";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2..";
        assertIPV6(ipv6, false);
        ipv6 = "::1.2.3.";
        assertIPV6(ipv6, false);
        ipv6 = "::.2..";
        assertIPV6(ipv6, false);
        ipv6 = "::.2.3.";
        assertIPV6(ipv6, false);
        ipv6 = "::.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::..3.";
        assertIPV6(ipv6, false);
        ipv6 = "::..3.4";
        assertIPV6(ipv6, false);
        ipv6 = "::...4";
        assertIPV6(ipv6, false);

        // Extra : in front
        ipv6 = ":1111:2222:3333:4444:5555:6666:7777::";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555:6666::";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555::";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444::";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::";
        assertIPV6(ipv6, false);
        ipv6 = ":::";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555:6666::8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555::8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444::8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::8888";
        assertIPV6(ipv6, false);
        ipv6 = ":::8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":::7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":::6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":::5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":::4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":::3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":::2222:3333:4444:5555:6666:7777:8888";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444:5555::1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444::1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":::1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333:4444::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":::6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222:3333::5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":::5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111:2222::4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":::4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":1111::3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);
        ipv6 = ":::2222:3333:4444:5555:6666:1.2.3.4";
        assertIPV6(ipv6, false);

        // Extra : at end
        ipv6 = "1111:2222:3333:4444:5555:6666:7777:::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666:::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:::";
        assertIPV6(ipv6, false);
        ipv6 = ":::";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555:6666::8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555::8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444::8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333::8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111::8888:";
        assertIPV6(ipv6, false);
        ipv6 = "::8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444:5555::7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444::7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333::7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111::7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "::7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333:4444::6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333::6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111::6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "::6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222:3333::5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111::5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "::5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111:2222::4444:5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111::4444:5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "::4444:5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "1111::3333:4444:5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "::3333:4444:5555:6666:7777:8888:";
        assertIPV6(ipv6, false);
        ipv6 = "::2222:3333:4444:5555:6666:7777:8888:";
        assertIPV6(ipv6, false);

        // Additional cases:
        // http://crisp.tweakblogs.net/blog/2031/ipv6-validation-%28and-caveats%29.html
        ipv6 = "0:a:b:c:d:e:f::";
        assertIPV6(ipv6, true);
        ipv6 = "::0:a:b:c:d:e:f";
        assertIPV6(ipv6, true); // syntactically correct, but bad form
        // (::0:... could be combined)
        ipv6 = "a:b:c:d:e:f:0::";
        assertIPV6(ipv6, true);
        ipv6 = "':10.0.0.1";
        assertIPV6(ipv6, false);

        // from wiki
        // http://zh.wikipedia.org/zh-cn/IPv6#.E8.BD.AC.E6.8D.A2.E6.9C.BA.E5.88.B6
        ipv6 = "2001::25de::cade";
        assertIPV6(ipv6, false);
        ipv6 = "2001:DB8:2de:0:0:0:0:e13";
        assertIPV6(ipv6, true);
        ipv6 = "2001:DB8:2de::e13";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0DB8:0000:0000:0000:0000:1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0DB8:0000:0000:0000::1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = " 2001:0DB8:0:0:0:0:1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0DB8:0::0:1428:57ab";
        assertIPV6(ipv6, true);
        ipv6 = "2001:0DB8::1428:57ab";
        assertIPV6(ipv6, true);

        ipv6 = "0000:0000:0000:0000:0000:ffff:874B:2B34";
        assertIPV6(ipv6, true);

        ipv6 = "::ffff:874B:2B34";
        assertIPV6(ipv6, true);

        ipv6 = "::ffff:135.75.43.52";
        assertIPV6(ipv6, true);
    }
}
