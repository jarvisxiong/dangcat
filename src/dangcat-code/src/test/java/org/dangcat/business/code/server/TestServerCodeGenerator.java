package org.dangcat.business.code.server;

public class TestServerCodeGenerator {
    public static void main(String[] args) throws Exception {
        ServerCodeGenerator serverCodeGenerator = new ServerCodeGenerator("com.dangcat.money", "money");
        serverCodeGenerator.setOutputDir("./log/money-service");
        serverCodeGenerator.generate();
    }
}
