package org.dangcat.business.code.entity;

import org.dangcat.commons.database.Database;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class TableManager {
    private Database database = null;
    private Map<String, Table> tableMap = new HashMap<String, Table>();

    public TableManager(Database database) {
        this.database = database;
    }

    private void addColumn(String tableName, Column column) {
        Table table = this.tableMap.get(tableName);
        if (table == null) {
            table = new Table(tableName);
            this.tableMap.put(tableName, table);
        }
        table.getColumns().add(column);
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(this.database.getDriver());
        return DriverManager.getConnection(this.database.getUrl(), this.database.getUser(), this.database.getPassword());
    }

    public Table getTable(String name) {
        return this.tableMap.get(name);
    }

    public Collection<Table> getTables() {
        List<Table> tables = new ArrayList<Table>();
        tables.addAll(this.tableMap.values());
        Collections.sort(tables, new Comparator<Table>() {
            @Override
            public int compare(Table srcTable, Table destTable) {
                return srcTable.getTableName().compareTo(destTable.getTableName());
            }
        });
        return tables;
    }

    private boolean isText(Class<?> classType) {
        if (classType == null)
            return false;
        return String.class.equals(classType) || char[].class.equals(classType) || Character[].class.equals(classType) || byte[].class.equals(classType) || Byte[].class.equals(classType);
    }

    public void load() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        try {
            connection = this.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            Map<String, Collection<String>> primaryKeyMap = this.loadPrimaryKeys(databaseMetaData);
            this.loadTables(databaseMetaData, primaryKeyMap);

            for (Table table : this.tableMap.values())
                TableUtils.sortColumns(table);
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    private Map<String, Collection<String>> loadPrimaryKeys(DatabaseMetaData databaseMetaData) throws SQLException {
        Collection<String> tableNames = new HashSet<String>();
        ResultSet tableResultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
        while (tableResultSet.next())
            tableNames.add(tableResultSet.getString("TABLE_NAME"));
        tableResultSet.close();

        Map<String, Collection<String>> primaryKeyMap = new HashMap<String, Collection<String>>();
        for (String tableName : tableNames) {
            try {
                Collection<String> primaryKeys = new HashSet<String>();
                ResultSet columnsResultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
                while (columnsResultSet.next())
                    primaryKeys.add(columnsResultSet.getString("COLUMN_NAME"));
                columnsResultSet.close();
                primaryKeyMap.put(tableName, primaryKeys);
            } catch (Exception e) {

            }
        }
        return primaryKeyMap;
    }

    private void loadTables(DatabaseMetaData databaseMetaData, Map<String, Collection<String>> primaryKeyMap) throws SQLException {
        for (Entry<String, Collection<String>> entry : primaryKeyMap.entrySet()) {
            String tableName = entry.getKey();
            ResultSet resultSet = databaseMetaData.getColumns(null, null, entry.getKey(), null);
            while (resultSet.next()) {
                Column column = new Column();
                column.setFieldName(resultSet.getString("COLUMN_NAME"));
                column.setFieldClass(TableUtils.getFieldType(resultSet));
                if (this.isText(column.getFieldClass()))
                    column.setDisplaySize(resultSet.getInt("COLUMN_SIZE"));
                column.setNullable(resultSet.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls);
                column.setAutoIncrement("YES".equalsIgnoreCase(resultSet.getString("IS_AUTOINCREMENT")));
                column.setPrimaryKey(entry.getValue().contains(column.getFieldName()));
                if (column.isPrimaryKey() && Number.class.isAssignableFrom(column.getFieldClass()))
                    column.setAutoIncrement(column.isAutoIncrement());
                this.addColumn(tableName, column);
            }
            resultSet.close();
        }
    }
}
