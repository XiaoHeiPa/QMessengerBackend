package org.qbychat.backend.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class CodeGen {
    public static void main(String[] args) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/qbychat");
        ds.setUsername("root");
        ds.setPassword("dys2010");
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setBasePackage("org.qbychat.backend");
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setEntityJdkVersion(17);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setAuthor("zszf");
        globalConfig.setTablePrefix("db_");
        globalConfig.setTableDefGenerateEnable(true);
        Generator generator = new Generator(ds, globalConfig);
        generator.generate();
    }
}
