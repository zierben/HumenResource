package com.hr.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hr.admin.mapper")
public class HrAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrAdminApplication.class, args);
    }
}
