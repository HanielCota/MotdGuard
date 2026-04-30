package io.github.hanielcota.motdguard.config;

public record MaintenanceConfig(
    boolean enabled,
    String kickMessage
) {
    public MaintenanceConfig {
        kickMessage = kickMessage != null ? kickMessage : "<red>Servidor em manutenção. Volte em breve!";
    }
}