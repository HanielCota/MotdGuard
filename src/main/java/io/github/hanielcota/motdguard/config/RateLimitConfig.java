package io.github.hanielcota.motdguard.config;

import static io.github.hanielcota.motdguard.config.ConfigValidation.requireText;

import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import net.kyori.adventure.text.Component;

public record RateLimitConfig(boolean enabled, int maxPingsPerMinute, String blockMessage) {

  private static final int MAX_PINGS_PER_MINUTE = 6000;

  public RateLimitConfig {
    if (enabled && maxPingsPerMinute < 1) {
      throw new IllegalArgumentException("rate-limit.max-pings-per-minute must be at least 1");
    }
    if (enabled && maxPingsPerMinute > MAX_PINGS_PER_MINUTE) {
      throw new IllegalArgumentException(
          "rate-limit.max-pings-per-minute must be at most " + MAX_PINGS_PER_MINUTE);
    }
    requireText(blockMessage, "rate-limit.block-message");

    MiniMessageUtil.deserializeStrict(blockMessage, "rate-limit.block-message");
  }

  public Component blockMessageComponent() {
    return MiniMessageUtil.deserialize(blockMessage);
  }
}
