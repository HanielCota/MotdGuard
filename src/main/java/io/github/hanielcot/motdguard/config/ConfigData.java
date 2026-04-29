package io.github.hanielcot.motdguard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class ConfigData {

    private final MotdConfig motd = new MotdConfig();
    private final MaintenanceConfig maintenance = new MaintenanceConfig();

    @JsonProperty("ratelimit")
    private final RateLimitConfig rateLimit = new RateLimitConfig();

    private final MessagesConfig messages = new MessagesConfig();
}
