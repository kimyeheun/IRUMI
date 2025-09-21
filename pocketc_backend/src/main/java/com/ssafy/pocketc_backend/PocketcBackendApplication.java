package com.ssafy.pocketc_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PocketcBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PocketcBackendApplication.class, args);
    }
}
