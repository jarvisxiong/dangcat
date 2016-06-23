package org.dangcat.document.csv;

import junit.framework.Assert;
import org.dangcat.document.TestEntityBase;
import org.dangcat.persistence.entity.EntityDataReader;
import org.dangcat.persistence.entity.EntityDataWriter;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TestCsvPerson extends TestEntityBase {
    private static final int TEST_COUNT = 100;

    private List<Sale> createSales() {
        List<Sale> sales = new LinkedList<Sale>();
        for (int i = 0; i < TEST_COUNT; i++) {
            Sale sale = new Sale();
            sale.setId(i);
            sale.setName("Sale" + i);
            sale.setSex(i % 2);
            sales.add(sale);
        }
        return sales;
    }

    @Test
    public void testSales() throws IOException, EntityException {
        List<Sale> srcSales = this.createSales();
        File file = File.createTempFile("Sales", ".csv");
        CsvDocument srcCsvDocument = new CsvDocument();
        srcCsvDocument.write(file, new EntityDataReader<Sale>(srcSales));

        List<Sale> destSales = new LinkedList<Sale>();
        CsvDocument dstCsvDocument = new CsvDocument();
        dstCsvDocument.read(file, new EntityDataWriter<Sale>(destSales, Sale.class));
        Assert.assertTrue(SimulateUtils.compareDataCollection(srcSales, destSales));

        List<Person> persons = new LinkedList<Person>();
        dstCsvDocument.read(file, new EntityDataWriter<Person>(persons, Person.class));
        Assert.assertTrue(SimulateUtils.compareDataCollection(persons, srcSales));

        file.delete();
    }
}
