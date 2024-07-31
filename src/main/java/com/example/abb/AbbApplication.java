package com.example.abb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class AbbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbbApplication.class, args);
    }

}
