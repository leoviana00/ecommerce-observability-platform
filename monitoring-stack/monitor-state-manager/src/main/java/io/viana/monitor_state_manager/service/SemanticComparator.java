package io.viana.monitor_state_manager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Faz comparação "semântica" entre o JSON antigo e o novo.
 * Aqui estamos considerando mudança apenas se o "status" mudou.
 * (Você pode enriquecer depois para olhar outros campos.)
 */
@Component
@RequiredArgsConstructor
public class SemanticComparator {

    private final ObjectMapper objectMapper;

    public boolean changed(String oldJson, String newJson) {
        if (oldJson == null) {
            return true;
        }

        try {
            JsonNode oldNode = objectMapper.readTree(oldJson);
            JsonNode newNode = objectMapper.readTree(newJson);

            String oldStatus = getText(oldNode, "status");
            String newStatus = getText(newNode, "status");

            // se status mudou, consideramos mudança
            return !oldStatus.equals(newStatus);

        } catch (Exception e) {
            // em caso de erro na comparação, preferimos considerar como mudança
            return true;
        }
    }

    private String getText(JsonNode node, String field) {
        JsonNode valueNode = node.get(field);
        return valueNode != null && !valueNode.isNull() ? valueNode.asText() : "";
    }
}
