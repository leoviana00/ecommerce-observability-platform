package io.viana.ecommerce_gateway.config;

import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryConfiguration {

    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(300))
                .retryExceptions(
                        java.io.IOException.class,
                        java.util.concurrent.TimeoutException.class
                )
                .build();
    }
}
