package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityParams;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Table;
import org.junit.Assert;
import org.junit.Test;

public class TestEntityParams
{
    @Test
    public void testParams()
    {
        Table table = EntityHelper.getEntityMetaData(EntityParams.class).getTable();
        Column idColumn = table.getColumns().find("Id");
        Assert.assertEquals(new Integer(1000), idColumn.getParams().get("idParams1"));
        Column nameColumn = table.getColumns().find("Name");
        Assert.assertEquals(3, nameColumn.getParams().size());
        Assert.assertEquals("nameParams1-value", nameColumn.getParams().get("nameParams1"));
        Assert.assertEquals(DateUtils.parse("2012-12-25", null), nameColumn.getParams().get("nameParams2"));
        Assert.assertEquals(Boolean.TRUE, nameColumn.getParams().get("nameParams3"));
    }
}
