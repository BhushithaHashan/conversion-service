package com.springclone.conversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ConversionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConversionServiceApplication.class, args);
    }
}
