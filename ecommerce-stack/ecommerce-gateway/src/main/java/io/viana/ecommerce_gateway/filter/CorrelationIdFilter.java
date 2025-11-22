package io.viana.ecommerce_gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate().header(HEADER, correlationId).build())
                .response(exchange.getResponse())
                .build();

        mutatedExchange.getResponse().getHeaders().add(HEADER, correlationId);

        System.out.println("➡️  [" + correlationId + "] " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI().getPath());

        return chain.filter(mutatedExchange)
                .doFinally(signal -> MDC.clear());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
