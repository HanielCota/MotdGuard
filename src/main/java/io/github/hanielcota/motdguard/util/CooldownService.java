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

  public void refresh(final boolean enabled, final Duration cooldownDuration) {
    state.set(createState(enabled, cooldownDuration));
  }

  private static State createState(final boolean enabled, final Duration cooldownDuration) {
    final Duration duration = Objects.requireNonNull(cooldownDuration, "cooldownDuration");

    if (enabled && (duration.isZero() || duration.isNegative())) {
      throw new IllegalArgumentException("cooldownDuration must be positive");
    }

    final Duration expiration =
        duration.isZero() || duration.isNegative() ? Duration.ofSeconds(1) : duration;

    return new State(enabled, Caffeine.newBuilder().expireAfterWrite(expiration).build());
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

  public void clearCooldown(final UUID playerId) {
    state.get().cache().invalidate(Objects.requireNonNull(playerId, "playerId"));
  }

  private record State(boolean enabled, Cache<UUID, Boolean> cache) {}
}
