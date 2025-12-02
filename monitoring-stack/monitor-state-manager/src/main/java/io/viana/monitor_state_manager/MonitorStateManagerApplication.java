package io.viana.monitor_state_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitorStateManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorStateManagerApplication.class, args);
    }
}
