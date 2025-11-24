package io.viana.ecommerce_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

// Marca a classe como uma fonte de definição de beans
@Configuration
public class RateLimiterConfig {

    // Define um bean para o Resolvedor de Chaves (KeyResolver)
    @Bean
    public KeyResolver ipAddressKeyResolver() {
        // O KeyResolver recebe o ServerWebExchange (a requisição) e deve retornar uma chave única (Mono<String>)
        return exchange -> Mono.just(
            // Tenta obter o endereço IP real do cliente a partir do cabeçalho "X-Forwarded-For".
            // Este cabeçalho é frequentemente preenchido por proxies/balanceadores de carga (como NGINX ou AWS ALB).
            exchange.getRequest().getHeaders().getFirst("X-Forwarded-For") != null
                ? exchange.getRequest().getHeaders().getFirst("X-Forwarded-For")
                // Se "X-Forwarded-For" não existir, usa o endereço IP da conexão remota direta.
                : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }
}