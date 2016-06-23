package org.dangcat.persistence.model;

import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.entity.EntityDataUtils;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.*;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestTableBulder {
    @BeforeClass
    public static void setUpBeforeClass() throws IOException, SessionException {
        SimulateUtils.configure();
        EntityDataUtils.createEntitySimulator();
        TableDataUtils.createTableSimulator();
    }

    private FilterExpress getFilterExpress() {
        FilterGroup filterGroup = new FilterGroup();
        FilterGroup childFilterGroup = new FilterGroup(FilterGroupType.or);
        childFilterGroup.add(EntityData.FieldB, FilterType.between, 0, 100);
        FilterUnit filterUnit = childFilterGroup.add(EntityData.FieldC, FilterType.between, 12.5, 74.5);
        filterUnit.setNot(true);
        filterGroup.add(childFilterGroup);
        filterGroup.add(EntityData.FieldA, FilterType.between, "AAA", "ZZZ");
        return filterGroup;
    }

    private Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ParamA", "AAA");
        params.put("ParamB", 222222);
        params.put("ParamC", 10247874909l);
        params.put("ParamD", (short) 2);
        params.put("ParamE", false);
        return params;
    }

    private Table getTable() {
        Table table = TableDataUtils.getTable();

        table.getSql().clear();
        table.getSql().append("SELECT Id, FieldA, FieldB, FieldC, FieldD, FieldE, FieldG");
        table.getSql().append("\nFROM ModelData");
        table.getSql().append("\nWHERE 1=1");

        table.setOrderBy(OrderBy.parse("Id Desc"));
        return table;
    }

    @Test
    public void testTableBulder() throws TableException, EntityException {
        Table dstTable = TableUtils.build(this.getClass(), "table.modeldata.xml");
        Assert.assertNotNull(dstTable);

        Table srcTable = this.getTable();
        Assert.assertEquals(srcTable.getColumns().size(), dstTable.getColumns().size());

        for (Column srcColumn : srcTable.getColumns()) {
            Column dstColumn = dstTable.getColumns().find(srcColumn.getName());
            Assert.assertNotNull(dstColumn);
            Assert.assertEquals(srcColumn.getName(), dstColumn.getName());
            Assert.assertTrue(srcColumn.getFieldClass().isAssignableFrom(dstColumn.getFieldClass()));
            Assert.assertEquals(srcColumn.getDisplaySize(), dstColumn.getDisplaySize());
            Assert.assertEquals(srcColumn.getTitle(), dstColumn.getTitle());
            Assert.assertEquals(srcColumn.isAutoIncrement(), dstColumn.isAutoIncrement());
            Assert.assertEquals(srcColumn.isPrimaryKey(), dstColumn.isPrimaryKey());
            Assert.assertEquals(srcColumn.isCalculate(), dstColumn.isCalculate());
            Assert.assertEquals(srcColumn.isNullable(), dstColumn.isNullable());
            Assert.assertEquals(srcColumn.getFormat(), dstColumn.getFormat());
        }
        Assert.assertEquals(srcTable.getSql().toString(), dstTable.getSql().toString());
        Assert.assertEquals(srcTable.getOrderBy().toString(), dstTable.getOrderBy().toString());

        FilterExpress srcFilterExpress = this.getFilterExpress();
        FilterExpress dstFilterExpress = dstTable.getFilter();
        Assert.assertNotNull(dstFilterExpress);
        Assert.assertEquals(srcFilterExpress.toString(), dstFilterExpress.toString());

        Map<String, Object> srcParams = this.getParams();
        Map<String, Object> dstParams = dstTable.getParams();
        Assert.assertEquals(srcParams.size(), dstParams.size());

        for (Object key : srcParams.keySet())
            Assert.assertEquals(srcParams.get(key), dstParams.get(key));
    }
}
