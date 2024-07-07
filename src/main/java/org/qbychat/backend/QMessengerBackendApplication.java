package org.qbychat.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@MapperScan("org.qbychat.backend.mapper")
@SpringBootApplication
public class QMessengerBackendApplication {
    public static final File CONFIG_DIR = new File("config");

    public static void main(String[] args) {
        CONFIG_DIR.mkdirs();
        SpringApplication.run(QMessengerBackendApplication.class, args);
    }

}
