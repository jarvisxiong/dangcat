package org.dangcat.document.excel;

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

public class TestExcelDocument extends TestEntityBase
{
    private static final int TEST_COUNT = 100;

    @Test
    public void testExcelForEntity() throws IOException, EntityException
    {
        List<EntityData> srcEntityDataList = new LinkedList<EntityData>();
        EntityDataUtils.createEntityDataList(srcEntityDataList, TEST_COUNT);
        File file = File.createTempFile("Entity", ".xls");
        ExcelDocumentWriter excelDocumentWriter = new ExcelDocumentWriter();
        excelDocumentWriter.setSheetTitle("EntityData");
        excelDocumentWriter.setDataReader(new EntityDataReader<EntityData>(srcEntityDataList));
        excelDocumentWriter.write(file);

        List<EntityData> dstEntityDataList = new LinkedList<EntityData>();
        ExcelDocumentReader excelDocumentReader = new ExcelDocumentReader();
        excelDocumentReader.read(file);
        excelDocumentReader.setDataWriter(new EntityDataWriter<EntityData>(dstEntityDataList, EntityData.class));
        excelDocumentReader.readSheet(0);
        Assert.assertTrue(SimulateUtils.compareDataCollection(srcEntityDataList, dstEntityDataList));
        file.delete();
    }

    @Test
    public void testExcelForTable() throws IOException, TableException
    {
        Table srcTable = TableDataUtils.getTable();
        TableDataUtils.createTableData(srcTable, TEST_COUNT);
        File file = File.createTempFile("Table", ".xls");
        ExcelDocumentWriter excelDocumentWriter = new ExcelDocumentWriter();
        excelDocumentWriter.setSheetTitle("EntityData");
        excelDocumentWriter.setDataReader(new TableDataReader(srcTable));
        excelDocumentWriter.write(file);

        Table dstTable = TableDataUtils.getTable();
        ExcelDocumentReader excelDocumentReader = new ExcelDocumentReader();
        excelDocumentReader.read(file);
        excelDocumentReader.setDataWriter(new TableDataWriter(dstTable));
        excelDocumentReader.readSheet(0);
        Assert.assertTrue(TableUtils.equalsContent(srcTable, dstTable));
        file.delete();
    }
}
