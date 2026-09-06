package com.gsz.empvis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class EmpVisApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void generatePassword() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "123456";

        System.out.println(
                encoder.encode(password)
        );
    }
}
