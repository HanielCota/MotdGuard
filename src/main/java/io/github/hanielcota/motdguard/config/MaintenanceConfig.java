package io.github.hanielcota.motdguard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class MaintenanceConfig {

    private final boolean enabled = false;

    @JsonProperty("kick-message")
    private final String kickMessage = "<red>Servidor em manutenção. Volte em breve!";
}
