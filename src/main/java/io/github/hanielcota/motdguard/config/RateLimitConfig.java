package io.github.hanielcota.motdguard.config;

import static io.github.hanielcota.motdguard.config.ConfigValidation.requireText;

import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import net.kyori.adventure.text.Component;

public record RateLimitConfig(boolean enabled, int maxPingsPerMinute, String blockMessage) {

  public RateLimitConfig {
    if (enabled && maxPingsPerMinute < 1) {
      throw new IllegalArgumentException("rate-limit.max-pings-per-minute must be at least 1");
    }
    requireText(blockMessage, "rate-limit.block-message");
  }

  public Component blockMessageComponent() {
    return MiniMessageUtil.deserialize(blockMessage);
  }
}
