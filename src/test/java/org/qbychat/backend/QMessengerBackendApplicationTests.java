package org.qbychat.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

@SpringBootTest
class QMessengerBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void newPassword(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("114514"));
    }
}
