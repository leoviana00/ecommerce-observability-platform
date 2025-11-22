package io.viana.ecommerce_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        long start = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        log.info("➡️ {} {}", method, path);

        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                long time = System.currentTimeMillis() - start;
                int status = exchange.getResponse().getStatusCode().value();
                log.info("⬅️ {} {} (status={}, {}ms)", method, path, status, time);
            }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
