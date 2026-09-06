package com.gsz.empvis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gsz.empvis.mapper")
public class EmpVisApplication {

    public static void main(String[] args) {

        SpringApplication.run(EmpVisApplication.class, args);
    }

}
