package org.dangcat.persistence.model;

import org.apache.log4j.Logger;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterGroupType;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.orm.SqlBuilder;
import org.junit.Assert;
import org.junit.Test;

public class TestModelSqlBuilder
{
    protected static final Logger logger = Logger.getLogger(TestModelSqlBuilder.class);

    @Test
    public void testParams() throws TableException
    {
        String sqlExpress1 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA=:FieldA AND FieldB>:FieldA OR FieldB<:FieldC\n";
        Table table = new Table("TestTable");
        // ²âÊÔ¶¯Ì¬²éÑ¯²ÎÊý¡£
        SqlBuilder sqlBuilder = table.getSql();
        sqlBuilder.append(sqlExpress1);
        table.getParams().put("FieldA", 75);
        table.getParams().put("FieldC", "ABSC");
        String sqlExpress2 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA=75 AND FieldB>75 OR FieldB<'ABSC'\n";
        // System.out.println(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress2, sqlBuilder.toString());

        String sqlExpress3 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA='0123''4567''89' AND FieldB>'0123''4567''89' OR FieldB<158.6\n";
        table.getParams().put("FieldA", "0123'4567'89");
        table.getParams().put("FieldC", 158.6);
        // System.out.println(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress3, sqlBuilder.toString());

        // ²âÊÔ¶¯Ì¬Ìæ»»¹ýÂËÄÚÈÝ¡£
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit("FieldA", FilterType.between, "AAAAAA", "ZZZZZ"));
        filterGroup.add(new FilterUnit("FieldB", FilterType.between, 12.56, 1695));
        table.setFilter(filterGroup);
        sqlBuilder.clear();
        sqlBuilder.setFilter(" AND " + filterGroup.toString());
        sqlBuilder.append("SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM ${tableName} \nWHERE FieldA=:FieldA ${filter} AND FieldB>:FieldA OR FieldB<:FieldC\n");

        String sqlExpress4 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA='0123''4567''89'  AND FieldA BETWEEN 'AAAAAA' AND 'ZZZZZ' AND FieldB BETWEEN 12.56 AND 1695 AND FieldB>'0123''4567''89' OR FieldB<158.6\n";
        logger.info(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress4, sqlBuilder.toString());

        String sqlExpress5 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA='0123''4567''89'  AND (FieldA BETWEEN 'AAAAAA' AND 'ZZZZZ' OR FieldB BETWEEN 12.56 AND 1695) AND FieldB>'0123''4567''89' OR FieldB<158.6\n";
        filterGroup.setGroupType(FilterGroupType.or);
        sqlBuilder.setFilter(" AND " + filterGroup.toString());
        logger.info(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress5, sqlBuilder.toString());
    }

    @Test
    public void testTextMarker() throws TableException
    {
        String sqlExpress1 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA=${FieldA} AND FieldB>${FieldA} OR FieldB<${FieldC}\n";
        Table table = new Table("TestTable");
        // ²âÊÔ¶¯Ì¬²éÑ¯²ÎÊý¡£
        SqlBuilder sqlBuilder = table.getSql();
        sqlBuilder.append(sqlExpress1);
        table.getParams().put("FieldA", 75);
        table.getParams().put("FieldC", "ABSC");
        String sqlExpress2 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA=75 AND FieldB>75 OR FieldB<'ABSC'\n";
        // System.out.println(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress2, sqlBuilder.toString());

        String sqlExpress3 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA='0123''4567''89' AND FieldB>'0123''4567''89' OR FieldB<158.6\n";
        table.getParams().put("FieldA", "0123'4567'89");
        table.getParams().put("FieldC", 158.6);
        // System.out.println(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress3, sqlBuilder.toString());

        // ²âÊÔ¶¯Ì¬Ìæ»»¹ýÂËÄÚÈÝ¡£
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit("FieldA", FilterType.between, "AAAAAA", "ZZZZZ"));
        filterGroup.add(new FilterUnit("FieldB", FilterType.between, 12.56, 1695));
        table.setFilter(filterGroup);
        sqlBuilder.clear();
        sqlBuilder.setFilter(" AND " + filterGroup.toString());
        sqlBuilder.append("SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM ${tableName} \nWHERE FieldA=${FieldA} ${filter} AND FieldB>${FieldA} OR FieldB<${FieldC}\n");

        String sqlExpress4 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA='0123''4567''89'  AND FieldA BETWEEN 'AAAAAA' AND 'ZZZZZ' AND FieldB BETWEEN 12.56 AND 1695 AND FieldB>'0123''4567''89' OR FieldB<158.6\n";
        logger.info(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress4, sqlBuilder.toString());

        String sqlExpress5 = "SELECT FieldA, FieldB, FieldC, FieldD, FieldF \nFROM TestTable \nWHERE FieldA='0123''4567''89'  AND (FieldA BETWEEN 'AAAAAA' AND 'ZZZZZ' OR FieldB BETWEEN 12.56 AND 1695) AND FieldB>'0123''4567''89' OR FieldB<158.6\n";
        filterGroup.setGroupType(FilterGroupType.or);
        sqlBuilder.setFilter(" AND " + filterGroup.toString());
        logger.info(sqlBuilder.toString());
        Assert.assertEquals(sqlExpress5, sqlBuilder.toString());
    }
}
