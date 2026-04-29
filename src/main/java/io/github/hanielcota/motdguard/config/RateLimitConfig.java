package io.github.hanielcota.motdguard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class RateLimitConfig {

  private final boolean enabled = true;

  @JsonProperty("max-pings-per-minute")
  private final int maxPingsPerMinute = 60;

  @JsonProperty("block-message")
  private final String blockMessage = "Muitas requisições. Aguarde.";
}
