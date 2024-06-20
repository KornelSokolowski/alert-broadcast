package com.app.alertbroadcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertBroadcastApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertBroadcastApplication.class, args);


    }
}
