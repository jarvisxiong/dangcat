package org.dangcat.business.code.entity;

import org.dangcat.commons.database.Database;
import org.dangcat.commons.database.MySqlDatabase;

public class TestDomainCodeGenerator
{
    public static void main(String[] args) throws Exception
    {
        Database database = new MySqlDatabase();
        database.setServer("localhost");
        database.setName("dangcat");
        database.setPort(3506);
        database.setUser("root");
        database.setPassword("dangcat2014");

        DomainCodeGenerator domainCodeGenerator = new DomainCodeGenerator();
        domainCodeGenerator.setBaseClassName("org.dangcat.persistence.entity.EntityBase");
        domainCodeGenerator.setPackageName("com.dangcat.business.domain");
        domainCodeGenerator.setOutputDir("./log/src");
        domainCodeGenerator.setDatabase(database);
        domainCodeGenerator.generate();
    }
}
