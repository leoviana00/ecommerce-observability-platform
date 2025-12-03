package io.viana.monitor_health_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthTarget {

    @NotBlank(message = "Target name must not be blank")
    private String name;

    @NotBlank(message = "Target url must not be blank")
    private String url;

    private String team;

    @Builder.Default
    private String criticality = "medium";

    @Min(value = 100, message = "Timeout must be >= 100ms")
    @Builder.Default
    private int timeoutMs = 2000;

    @Builder.Default
    private List<String> tags = Collections.emptyList();

    @JsonProperty("enabled")
    @NotNull(message = "Enabled flag must be provided")
    @Builder.Default
    private Boolean enabled = true;


    // -----------------------------------
    // Métodos utilitários não serializáveis
    // -----------------------------------

    @JsonIgnore
    public boolean enabledSafe() {
        return Boolean.TRUE.equals(enabled);
    }

    @JsonIgnore
    public boolean critical() {
        return "critical".equalsIgnoreCase(criticality) ||
               "high".equalsIgnoreCase(criticality);
    }

    @JsonIgnore
    public List<String> tagsSafe() {
        return tags != null ? tags : Collections.emptyList();
    }

    @JsonIgnore
    public String teamSafe() {
        return team != null ? team : "unknown";
    }

    @JsonIgnore
    public String criticalityNormalized() {
        return criticality != null ? criticality.toLowerCase() : "unknown";
    }
}
