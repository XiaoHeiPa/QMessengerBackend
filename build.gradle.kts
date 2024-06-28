plugins {
    java
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "org.qbychat"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven ( url = "https://maven.aliyun.com/repository/public" )
    maven ( url = "https://maven.aliyun.com/repository/spring" )
}

dependencies {
    implementation("com.google.firebase:firebase-admin:9.2.0")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.37")
    implementation("com.mybatis-flex:mybatis-flex-spring-boot-starter:1.9.2")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("org.projectlombok:lombok")
//    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.3")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    annotationProcessor("com.mybatis-flex:mybatis-flex-processor:1.9.2")
    implementation("com.mybatis-flex:mybatis-flex-codegen:1.8.3")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("com.eatthepath:java-otp:0.4.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
