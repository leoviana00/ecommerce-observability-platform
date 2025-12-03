package io.viana.monitor_alert_dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonitorAlertDispatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorAlertDispatcherApplication.class, args);
    }
}
