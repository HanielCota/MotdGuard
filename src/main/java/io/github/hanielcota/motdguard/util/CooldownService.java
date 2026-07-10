package io.github.hanielcota.motdguard.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class CooldownService {

  private final AtomicReference<State> state;

  public CooldownService(final boolean enabled, final Duration cooldownDuration) {
    this.state = new AtomicReference<>(createState(enabled, cooldownDuration));
  }

  /**
   * Applies a new cooldown configuration.
   *
   * <p>When neither {@code enabled} nor {@code cooldownDuration} changed, the call is a no-op: the
   * existing cache is kept so that cooldowns already in progress survive a configuration reload.
   * Rebuilding the cache unconditionally would wipe every active cooldown, which makes the cooldown
   * on the {@code reload} command itself ineffective.
   */
  public void refresh(final boolean enabled, final Duration cooldownDuration) {
    Objects.requireNonNull(cooldownDuration, "cooldownDuration");

    state.updateAndGet(
        current -> {
          if (current.enabled() == enabled && current.duration().equals(cooldownDuration)) {
            return current;
          }

          return createState(enabled, cooldownDuration);
        });
  }

  private static State createState(final boolean enabled, final Duration cooldownDuration) {
    final Duration duration = Objects.requireNonNull(cooldownDuration, "cooldownDuration");

    if (enabled && (duration.isZero() || duration.isNegative())) {
      throw new IllegalArgumentException("cooldownDuration must be positive");
    }

    final Duration expiration =
        duration.isZero() || duration.isNegative() ? Duration.ofSeconds(1) : duration;

    return new State(enabled, duration, Caffeine.newBuilder().expireAfterWrite(expiration).build());
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
