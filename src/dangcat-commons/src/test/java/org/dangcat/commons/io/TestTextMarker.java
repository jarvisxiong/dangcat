package org.dangcat.commons.io;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestTextMarker
{
    @Test
    public void testProcess() throws IOException
    {
        String expected = "select a, b from testTable where c=12";
        TextMarker textMarker = new TextMarker();
        textMarker.setTemplate("select a, b from ${tableName} where c=${c}");
        textMarker.putData("tableName", "testTable");
        textMarker.putData("c", 12);
        String result = textMarker.process();
        Assert.assertEquals(expected, result);
    }
}
