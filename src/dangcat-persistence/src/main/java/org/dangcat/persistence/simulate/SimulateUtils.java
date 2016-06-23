package org.dangcat.persistence.simulate;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.conf.ConfigureManager;
import org.dangcat.persistence.entity.EntityField;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Field;
import org.dangcat.persistence.model.Row;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimulateUtils {
    private static final String RESOURCE_DEFAULT_PATH = "/test-classes/META-INF/resource.properties";

    /**
     * 比较两个实体对象。
     *
     * @param srcData  来源实体。
     * @param destData 目标实体。
     * @return 是否相同。
     */
    public static boolean compareData(Object srcData, Object destData) {
        return compareData(srcData, destData, true);
    }

    public static boolean compareData(Object srcData, Object destData, boolean showError) {
        if (srcData instanceof Row && destData instanceof Row)
            return compareRow((Row) srcData, (Row) destData, showError);
        return compareEntity(srcData, destData, showError);
    }

    /**
     * 比较实体集合。
     *
     * @param srcCollection  来源实体集合。
     * @param destCollection 目标实体集合。
     * @return 是否相同。
     */
    public static boolean compareDataCollection(Collection<?> srcCollection, Collection<?> destCollection) {
        Object[] srcDataArray = srcCollection.toArray();
        List<Object> destDataList = new ArrayList<Object>();
        destDataList.addAll(destCollection);
        for (int i = 0; i < srcDataArray.length; i++) {
            Object found = null;
            for (int j = 0; j < destDataList.size(); j++) {
                if (compareData(srcDataArray[i], destDataList.get(j), false)) {
                    found = destDataList.get(j);
                    destDataList.remove(j);
                    break;
                }
            }
            if (found == null)
                return false;
        }
        return srcDataArray.length == destCollection.size();
    }

    /**
     * 比较两个实体对象。
     *
     * @param srcEntity  来源实体。
     * @param destEntity 目标实体。
     * @return 是否相同。
     */
    private static boolean compareEntity(Object srcEntity, Object destEntity, boolean showError) {
        EntityMetaData srcEntityMetaData = EntityHelper.getEntityMetaData(srcEntity.getClass());
        EntityMetaData destEntityMetaData = EntityHelper.getEntityMetaData(destEntity.getClass());
        for (EntityField srcEntityField : srcEntityMetaData.getEntityFieldCollection()) {
            if (srcEntityField.isRowNum())
                continue;

            EntityField destEntityField = destEntityMetaData.getEntityField(srcEntityField.getName());
            if (destEntityField == null) {
                System.err.println(srcEntityField.getName() + " is not found in dest entity.");
                return false;
            }

            Object srcValue = srcEntityField.getValue(srcEntity);
            Object destValue = destEntityField.getValue(destEntity);
            int result = ValueUtils.compare(srcValue, destValue);
            if (result != 0) {
                if (showError)
                    System.err.println(srcEntityField.getName() + " : srcField = " + srcValue + ", destField = " + destValue);
                return false;
            }
        }
        return true;
    }

    /**
     * 比较两个行对象。
     *
     * @param srcEntity  来源实体。
     * @param destEntity 目标实体。
     * @return 是否相同。
     */
    private static boolean compareRow(Row srcRow, Row destRow, boolean showError) {
        for (Column column : srcRow.getParent().getColumns()) {
            if (column.isRowNum())
                continue;

            Field destField = destRow.getField(column.getName());
            if (destField == null) {
                System.err.println(column.getName() + " is not found in dest entity.");
                return false;
            }

            Object srcValue = srcRow.getField(column.getName()).getObject();
            Object destValue = destField.getObject();
            int result = ValueUtils.compare(srcValue, destValue);
            if (result != 0) {
                if (showError)
                    System.err.println(column.getName() + " : srcField = " + srcValue + ", destField = " + destValue);
                return false;
            }
        }
        return true;
    }

    /**
     * 配置测试资源。
     */
    public static void configure() {
        configure(Environment.getHomePath() + RESOURCE_DEFAULT_PATH);
    }

    /**
     * 配置测试资源。
     */
    public static void configure(String path) {
        ConfigureManager.getInstance().configure(new File(FileUtils.decodePath(path)));
    }

    /**
     * 产生测试的临时目录。
     */
    public static File createTmpDirectory(String path) {
        File dir = new File("log" + File.separator + path);
        if (dir.exists())
            FileUtils.delete(dir);
        FileUtils.mkdir(dir.getAbsolutePath());
        return dir;
    }

    public static void resetData(Object data, String... fieldNames) {
        if (data instanceof Row)
            resetRow((Row) data, fieldNames);
        else
            resetEntity(data, fieldNames);
    }

    public static void resetDataCollection(Collection<?> dataCollection, String... fieldNames) {
        if (dataCollection != null) {
            for (Object data : dataCollection)
                resetData(data, fieldNames);
        }
    }

    private static void resetEntity(Object data, String... fieldNames) {
        if (fieldNames != null) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(data);
            for (String fieldName : fieldNames)
                entityMetaData.getEntityField(fieldName).setValue(data, null);
        }
    }

    private static void resetRow(Row row, String... fieldNames) {
        if (fieldNames != null) {
            for (String fieldName : fieldNames)
                row.getField(fieldName).setObject(null);
        }
    }
}