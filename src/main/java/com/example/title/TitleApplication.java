package com.example.title;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.title.mapper")
@SpringBootApplication
public class TitleApplication {

    public static void main(String[] args) {
        SpringApplication.run(TitleApplication.class, args);
    }

}
