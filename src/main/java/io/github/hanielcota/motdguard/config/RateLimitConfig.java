package io.github.hanielcota.motdguard.config;

public record RateLimitConfig(
    boolean enabled,
    int maxPingsPerMinute,
    String blockMessage
) {
    public RateLimitConfig {
        if (maxPingsPerMinute < 1) {
            maxPingsPerMinute = 60;
        }
        blockMessage = blockMessage != null ? blockMessage : "Muitas requisições. Aguarde.";
    }
}