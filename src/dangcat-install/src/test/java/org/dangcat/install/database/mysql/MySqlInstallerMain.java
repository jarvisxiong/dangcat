package org.dangcat.install.database.mysql;

public class MySqlInstallerMain {
    public static void main(String[] args) throws Exception {
        MySqlInstaller mySqlInstaller = new MySqlInstaller();
        mySqlInstaller.setServiceName("MySql-scores");
        mySqlInstaller.setServiceDisplayName("dangcat-scores");
        mySqlInstaller.setBaseDir("D:/Program Files/mysql/mysql-5.1.62-win32");
        mySqlInstaller.setDataDir("D:\\Work\\Database\\Mysql 5.1\\scores");
        mySqlInstaller.setPort(3106);
        mySqlInstaller.setDatabaseName("testdb");
        mySqlInstaller.setUser("admin");
        mySqlInstaller.setPassword("admin123");
        mySqlInstaller.setCharacterSet("utf-8");
        mySqlInstaller.getScripts().add("D:/Work/Database/Mysql 5.1/脚本/create.sql");
        mySqlInstaller.getScripts().add("D:/Work/Database/Mysql 5.1/脚本/init.sql");
        mySqlInstaller.config();
        mySqlInstaller.install();
        mySqlInstaller.start();
        mySqlInstaller.createDatabase();
    }
}
