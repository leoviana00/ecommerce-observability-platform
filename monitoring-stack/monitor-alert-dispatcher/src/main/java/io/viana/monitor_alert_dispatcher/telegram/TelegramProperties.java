package io.viana.monitor_alert_dispatcher.telegram;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {

    /**
     * Habilita/desabilita envio de alertas via Telegram.
     */
    private boolean enabled = false;

    /**
     * Token do bot, obtido via BotFather.
     * Ex: 123456789:ABCdefghijk...
     */
    private String botToken;

    /**
     * Chat ID para onde enviar.
     * Pode ser grupo, canal ou usuário.
     */
    private String chatId;

    /**
     * Timeout de resposta HTTP (ms)
     */
    private long timeoutMs = 5000;

    /**
     * Número máximo de retries.
     * 0 => sem retry
     */
    private long maxRetries = 3;
}
