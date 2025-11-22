package io.viana.ecommerce_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Configuração do Rate Limiter.
 * Define a chave que será usada para limitar as requisições.
 * Usamos o IP de origem como chave para proteger contra ataques simples de DoS.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Define o KeyResolver baseado no endereço IP de origem.
     * @return KeyResolver que usa o IP remoto da requisição.
     */
    @Bean
    public KeyResolver ipAddressKeyResolver() {
        return exchange -> Mono.just(
            // Tenta obter o IP do cabeçalho Forwarded (se atrás de um proxy/load balancer)
            // Se não, usa o IP remoto direto
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }
}