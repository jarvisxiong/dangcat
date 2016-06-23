package org.dangcat.boot.test;

import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Table;

class ServiceTestUtils {
    protected static void dropEntityTable(Class<?>... classTypes) throws Exception {
        if (classTypes == null || classTypes.length == 0)
            return;

        for (Class<?> classType : classTypes) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
            Table table = entityMetaData.getTable();
            if (table.exists())
                table.drop();
        }
    }

    protected static void initEntityTable(Class<?>... classTypes) throws Exception {
        if (classTypes == null || classTypes.length == 0)
            return;

        for (Class<?> classType : classTypes) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
            Table table = entityMetaData.getTable();
            if (table.exists())
                table.drop();
            table.create();
        }
    }
}
