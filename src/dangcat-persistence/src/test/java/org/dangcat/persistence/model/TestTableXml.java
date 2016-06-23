package org.dangcat.persistence.model;

import junit.framework.Assert;

import org.junit.Test;

public class TestTableXml
{
    @Test
    public void testBuild()
    {
        Table table = TableUtils.build(TestTableXml.class, "TableBuild.xml");
        Assert.assertNotNull(table);
        Assert.assertEquals(30, table.getColumns().size());
        Assert.assertEquals(2, table.getCalculators().size());
    }
}
