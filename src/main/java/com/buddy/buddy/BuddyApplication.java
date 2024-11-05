package com.buddy.buddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuddyApplication.class, args);
    }

}
