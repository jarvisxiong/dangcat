package org.dangcat.business.code.service;

public class TestBusinessCodeGenerator {
    public static void main(String[] args) throws Exception {
        BusinessCodeGenerator businessCodeGenerator = new BusinessCodeGenerator("Staff", "UserInfo");
        businessCodeGenerator.setPackageName("com.dangcat.business.user");
        businessCodeGenerator.setOutputDir("./log/src");
        businessCodeGenerator.generate();
    }
}
