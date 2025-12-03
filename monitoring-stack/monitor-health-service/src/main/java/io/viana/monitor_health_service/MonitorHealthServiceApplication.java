package io.viana.monitor_health_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitorHealthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorHealthServiceApplication.class, args);
    }
}
