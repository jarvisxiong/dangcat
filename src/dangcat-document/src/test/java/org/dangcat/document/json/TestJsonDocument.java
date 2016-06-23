package org.dangcat.document.json;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.dangcat.document.EntityData;
import org.dangcat.document.EntityDataUtils;
import org.dangcat.document.TableDataUtils;
import org.dangcat.document.TestEntityBase;
import org.dangcat.persistence.entity.EntityDataReader;
import org.dangcat.persistence.entity.EntityDataWriter;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataReader;
import org.dangcat.persistence.model.TableDataWriter;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Test;

public class TestJsonDocument extends TestEntityBase
{
    private static final int TEST_COUNT = 100;

    @Test
    public void testTextForEntity() throws IOException, EntityException
    {
        List<EntityData> srcEntityDataList = new LinkedList<EntityData>();
        EntityDataUtils.createEntityDataList(srcEntityDataList, TEST_COUNT);
        File file = File.createTempFile("Entity", ".json");
        JsonDocument srcJsonDocument = new JsonDocument();
        srcJsonDocument.write(file, new EntityDataReader<EntityData>(srcEntityDataList));

        List<EntityData> dstEntityDataList = new LinkedList<EntityData>();
        JsonDocument dstJsonDocument = new JsonDocument();
        dstJsonDocument.read(file, new EntityDataWriter<EntityData>(dstEntityDataList, EntityData.class));
        Assert.assertTrue(SimulateUtils.compareDataCollection(srcEntityDataList, dstEntityDataList));
        file.delete();
    }

    @Test
    public void testTextForTable() throws IOException, TableException
    {
        Table srcTable = TableDataUtils.getTable();
        TableDataUtils.createTableData(srcTable, TEST_COUNT);
        File file = File.createTempFile("Table", ".json");
        JsonDocument srcJsonDocument = new JsonDocument();
        srcJsonDocument.write(file, new TableDataReader(srcTable));

        Table dstTable = TableDataUtils.getTable();
        JsonDocument dstJsonDocument = new JsonDocument();
        dstJsonDocument.read(file, new TableDataWriter(dstTable));
        Assert.assertTrue(TableUtils.equalsContent(srcTable, dstTable));
        file.delete();
    }
}
