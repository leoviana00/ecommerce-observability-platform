package io.viana.ecommerce_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ErrorHandlingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        return chain.filter(exchange)
                .onErrorResume(ex -> {
                    log.error("❌ Erro durante o processamento da requisição para {}: {}",
                              exchange.getRequest().getURI().getPath(),
                              ex.getMessage());

                    exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return -90;
    }
}