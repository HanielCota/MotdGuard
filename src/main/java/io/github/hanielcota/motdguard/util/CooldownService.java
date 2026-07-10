package io.github.hanielcota.motdguard.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import io.github.hanielcota.motdguard.Reloadable;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.CooldownConfig;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class CooldownService implements Reloadable {

  private final ConfigManager configManager;
  private final AtomicReference<State> state;

  @Inject
  public CooldownService(final ConfigManager configManager) {
    this.configManager = Objects.requireNonNull(configManager, "configManager");
    this.state = new AtomicReference<>(createState(currentConfig()));
  }

  /**
   * Applies the current cooldown configuration.
   *
   * <p>When neither {@code enabled} nor the duration changed, the call is a no-op: the existing
   * cache is kept so that cooldowns already in progress survive a configuration reload. Rebuilding
   * the cache unconditionally would wipe every active cooldown, which makes the cooldown on the
   * {@code reload} command itself ineffective.
   */
  @Override
  public void refresh() {
    final CooldownConfig next = currentConfig();

    state.updateAndGet(
        current -> {
          if (current.enabled() == next.enabled()
              && current.duration().equals(Duration.ofSeconds(next.durationSeconds()))) {
            return current;
          }

          return createState(next);
        });
  }

  private CooldownConfig currentConfig() {
    return configManager.getConfigData().cooldown();
  }

  private static State createState(final CooldownConfig config) {
    final Duration duration = Duration.ofSeconds(config.durationSeconds());

    if (config.enabled() && (duration.isZero() || duration.isNegative())) {
      throw new IllegalArgumentException("cooldownDuration must be positive");
    }

    final Duration expiration =
        !config.enabled() || duration.isZero() || duration.isNegative()
            ? Duration.ofSeconds(1)
            : duration;

    return new State(
        config.enabled(), duration, Caffeine.newBuilder().expireAfterWrite(expiration).build());
  }

  public boolean isOnCooldown(final UUID playerId) {
    final State snapshot = state.get();

    if (!snapshot.enabled()) {
      return false;
    }

    Objects.requireNonNull(playerId, "playerId");

    return snapshot.cache().getIfPresent(playerId) != null;
  }

  public void setUsed(final UUID playerId) {
    final State snapshot = state.get();

    if (!snapshot.enabled()) {
      return;
    }

    snapshot.cache().put(Objects.requireNonNull(playerId, "playerId"), Boolean.TRUE);
  }

  /**
   * Atomically marks {@code playerId} as having used a command and reports whether it was already
   * on cooldown.
   *
   * @return {@code true} if the player was already on cooldown (the caller should block the
   *     command); {@code false} if the mark was just placed (the command may proceed). Always
   *     {@code false} when the service is disabled.
   */
  public boolean tryAcquire(final UUID playerId) {
    final State snapshot = state.get();

    if (!snapshot.enabled()) {
      return false;
    }

    Objects.requireNonNull(playerId, "playerId");

    return snapshot.cache().asMap().putIfAbsent(playerId, Boolean.TRUE) != null;
  }

  public void clearCooldown(final UUID playerId) {
    state.get().cache().invalidate(Objects.requireNonNull(playerId, "playerId"));
  }

  private record State(boolean enabled, Duration duration, Cache<UUID, Boolean> cache) {}
}
