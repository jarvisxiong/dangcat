package org.dangcat.business.code.entity;

import org.dangcat.commons.database.Database;
import org.dangcat.commons.database.MySqlDatabase;

import java.sql.SQLException;

public class TestTableManager
{
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {
        Database database = new MySqlDatabase();
        database.setServer("localhost");
        database.setName("dangcat");
        database.setPort(3506);
        database.setUser("root");
        database.setPassword("dangcat2014");
        TableManager tableManager = new TableManager(database);
        tableManager.load();
        for (Table table : tableManager.getTables())
        {
            System.out.println("--------------------------------------------------------");
            System.out.println("Table Name : " + table.getName() + ", TableName : " + table.getTableName());
            for (Column column : table.getColumns())
                System.out.println(column);
        }
    }
}
