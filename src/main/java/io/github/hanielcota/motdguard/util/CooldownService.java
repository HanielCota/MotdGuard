package io.github.hanielcota.motdguard.util;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory cooldown tracker for commands.
 *
 * <p>Tracks the last time a player used a command and checks if the cooldown period has elapsed.
 * Uses a {@link ConcurrentHashMap} for thread-safe operations.
 */
public final class CooldownService {

  private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
  private final Duration cooldownDuration;

  public CooldownService(final Duration cooldownDuration) {
    this.cooldownDuration = cooldownDuration;
  }

  public boolean isOnCooldown(final UUID playerId) {
    final var lastUsed = cooldowns.get(playerId);
    return lastUsed != null && System.currentTimeMillis() - lastUsed < cooldownDuration.toMillis();
  }

  public void setUsed(final UUID playerId) {
    cooldowns.put(playerId, System.currentTimeMillis());
  }

  public void cleanup() {
    final long now = System.currentTimeMillis();
    cooldowns.entrySet().removeIf(entry -> now - entry.getValue() >= cooldownDuration.toMillis());
  }
}
