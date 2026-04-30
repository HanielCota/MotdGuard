package io.github.hanielcota.motdguard.config;

public record MotdConfig(
    String line1,
    String line2
) {
    public MotdConfig {
        line1 = line1 != null ? line1 : "<#00FF00>MeuServidor";
        line2 = line2 != null ? line2 : "<#FFFFFF>Modo Hardcore Ativo";
    }
}