package io.viana.ecommerce_gateway.config;

import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

// Marca a classe como uma fonte de definição de beans
@Configuration
public class RetryConfiguration {

    // Define um bean que fornece a configuração centralizada do Retry para o Resilience4j
    @Bean
    public RetryConfig retryConfig() {
        // Inicia a construção de uma configuração de Retry customizada
        return RetryConfig.custom()
                // Define o número máximo de tentativas, incluindo a tentativa inicial (total de 3)
                .maxAttempts(3)
                // Define a duração da espera entre cada tentativa (300 milissegundos)
                .waitDuration(Duration.ofMillis(300))
                // Especifica quais exceções acionarão uma nova tentativa
                .retryExceptions(
                        // Exceções de I/O, como problemas de conexão de rede
                        java.io.IOException.class,
                        // Exceções de Timeout, indicando que a requisição demorou demais
                        java.util.concurrent.TimeoutException.class
                )
                // Constrói e finaliza a configuração
                .build();
    }
}