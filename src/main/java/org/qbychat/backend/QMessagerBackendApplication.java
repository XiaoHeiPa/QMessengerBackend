package org.qbychat.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.qbychat.backend.mapper")
@SpringBootApplication
public class QMessagerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QMessagerBackendApplication.class, args);
    }

}
