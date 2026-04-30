package io.github.hanielcota.motdguard.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public final class CooldownService {

  private final boolean enabled;
  private final Cache<UUID, Boolean> cache;

  public CooldownService(final boolean enabled, final Duration cooldownDuration) {
    if (Objects.requireNonNull(cooldownDuration, "cooldownDuration").isZero()
        || cooldownDuration.isNegative()) {
      throw new IllegalArgumentException("cooldownDuration must be positive");
    }

    this.enabled = enabled;
    this.cache = Caffeine.newBuilder().expireAfterWrite(cooldownDuration).build();
  }

  public boolean isOnCooldown(final UUID playerId) {
    if (!enabled) {
      return false;
    }

    Objects.requireNonNull(playerId, "playerId");

    return cache.getIfPresent(playerId) != null;
  }

  public void setUsed(final UUID playerId) {
    if (!enabled) {
      return;
    }

    cache.put(Objects.requireNonNull(playerId, "playerId"), Boolean.TRUE);
  }

  public void clearCooldown(final UUID playerId) {
    cache.invalidate(Objects.requireNonNull(playerId, "playerId"));
  }
}
