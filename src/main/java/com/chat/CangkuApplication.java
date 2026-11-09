package com.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@MapperScan("com.chat.mapper")
@SpringBootApplication

@CrossOrigin
public class CangkuApplication {

    public static void main(String[] args) {
        SpringApplication.run(CangkuApplication.class, args);
    }

}
