package io.github.hanielcota.motdguard.config;

public record ConfigData(
    MotdConfig motd,
    MaintenanceConfig maintenance,
    RateLimitConfig rateLimit,
    MessagesConfig messages
) {
    public ConfigData {
        motd = motd != null ? motd : new MotdConfig(null, null);
        maintenance = maintenance != null ? maintenance : new MaintenanceConfig(false, null);
        rateLimit = rateLimit != null ? rateLimit : new RateLimitConfig(true, 60, null);
        messages = messages != null ? messages : new MessagesConfig(null, null, null, null, null, null, null, null, null, null);
    }
}