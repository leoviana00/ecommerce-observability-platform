package io.viana.monitor_lag_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitorLagServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorLagServiceApplication.class, args);
    }
}
