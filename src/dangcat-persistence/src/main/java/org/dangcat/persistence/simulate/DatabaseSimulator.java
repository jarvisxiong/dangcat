package org.dangcat.persistence.simulate;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.orm.AutoIncrementManager;
import org.dangcat.persistence.simulate.table.SimulateData;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据库模拟器。
 *
 * @author dangcat
 */
public class DatabaseSimulator {
    private Map<String, SimulateData> simulateDataMap = new LinkedHashMap<String, SimulateData>();

    public void add(SimulateData simulateData, int size) {
        if (simulateData != null) {
            simulateData.setSize(size);
            simulateData.createDataSimulator(this);
            String tableName = simulateData.getDataSimulator().getTableName();
            if (!ValueUtils.isEmpty(tableName))
                this.simulateDataMap.put(tableName, simulateData);
        }
    }

    public void clear() {
        this.simulateDataMap.clear();
    }

    /**
     * 产生模拟数据。
     */
    public void create() {
        for (SimulateData simulateData : this.simulateDataMap.values())
            simulateData.create();
    }

    public SimulateData getSimulateData(String tableName) {
        return this.simulateDataMap.get(tableName);
    }

    /**
     * 初始化数据库。
     *
     * @throws SessionException
     */
    public void initDatabase() {
        AutoIncrementManager.reset();
        for (SimulateData simulateData : this.simulateDataMap.values()) {
            simulateData.initTable();
            simulateData.clear();
            simulateData.create();
            simulateData.save();
        }
    }

    public void remove(String tableName) {
        if (!ValueUtils.isEmpty(tableName))
            this.simulateDataMap.remove(tableName);
    }
}
