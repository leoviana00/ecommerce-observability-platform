package io.viana.monitor_alert_dispatcher.client;

import io.viana.monitor_alert_dispatcher.telegram.TelegramProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class TelegramClient {

    private final WebClient webClient;
    private final TelegramProperties properties;

    public TelegramClient(WebClient.Builder webClientBuilder,
                          TelegramProperties properties) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.telegram.org")
                .build();
        this.properties = properties;
    }

    public Mono<Void> sendMessage(String text) {
        // Se estiver desabilitado por configuração, não faz nada.
        if (!properties.isEnabled()) {
            log.debug("Telegram desabilitado via configuração. Mensagem não será enviada.");
            return Mono.empty();
        }

        String token = properties.getBotToken();
        String chatId = properties.getChatId();

        if (isBlank(token) || isBlank(chatId)) {
            log.warn("Telegram desabilitado: botToken ou chatId não configurados corretamente.");
            return Mono.empty();
        }

        long timeoutMs = properties.getTimeoutMs() > 0 ? properties.getTimeoutMs() : 5000;
        long maxRetries = properties.getMaxRetries() >= 0 ? properties.getMaxRetries() : 0;

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/bot" + token + "/sendMessage")
                        .queryParam("chat_id", chatId)
                        .queryParam("text", text)
                        .build()
                )
                // Ponto-chave: usamos exchangeToMono e ignoramos o corpo em caso de sucesso
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.debug("Telegram respondeu com status 2xx: {}", response.statusCode());
                        // Ignora o body; só consideramos o sucesso do status.
                        return Mono.<Void>empty();
                    }

                    // Em caso de erro, logamos o body para diagnóstico e falhamos.
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> {
                                log.error("Erro ao chamar Telegram. Status={} Body={}",
                                        response.statusCode(), body);
                                return Mono.error(new IllegalStateException(
                                        "Falha ao enviar mensagem para Telegram: " + response.statusCode()
                                ));
                            });
                })
                // Timeout de leitura da resposta (evita ‘Did not observe any item…’)
                .timeout(Duration.ofMillis(timeoutMs))
                // Retry controlado
                .retryWhen(
                        Retry.fixedDelay(maxRetries, Duration.ofMillis(500))
                                .doBeforeRetry(retrySignal -> log.warn(
                                        "Retry {}/{} ao enviar mensagem para Telegram",
                                        retrySignal.totalRetries() + 1,
                                        maxRetries
                                ))
                )
                // Se mesmo após retries der erro, loga e completa sem propagar
                .doOnError(ex -> log.error("Retries do Telegram esgotados", ex))
                .onErrorResume(ex -> Mono.empty());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
