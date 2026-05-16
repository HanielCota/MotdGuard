package io.github.hanielcota.motdguard.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.Objects;

public record ConfigData(
    MotdConfig motd,
    MaintenanceConfig maintenance,
    @JsonAlias("ratelimit") RateLimitConfig rateLimit,
    CooldownConfig cooldown,
    MessagesConfig messages) {

  public ConfigData {
    Objects.requireNonNull(motd, "Missing [motd] section");
    Objects.requireNonNull(maintenance, "Missing [maintenance] section");
    Objects.requireNonNull(rateLimit, "Missing [rate-limit] section");
    Objects.requireNonNull(cooldown, "Missing [cooldown] section");
    Objects.requireNonNull(messages, "Missing [messages] section");
  }
}
