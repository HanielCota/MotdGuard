package io.github.hanielcota.motdguard.command;

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

        final var nextDuration = Duration.ofSeconds(next.durationSeconds());

        state.updateAndGet(current -> {
            if (current.enabled() == next.enabled() && current.duration().equals(nextDuration)) {
                return current;
            }

            final State rebuilt = createState(next);

            // Carry in-progress cooldowns over so a reload that changes the duration (but keeps the
            // service enabled) does not silently free players still on cooldown — notably the reload
            // command itself. New entries get a fresh full-duration window; existing ones survive.
            if (current.enabled() && rebuilt.enabled()) {
                rebuilt.cache().putAll(current.cache().asMap());
            }

            return rebuilt;
        });
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

    private CooldownConfig currentConfig() {
        return configManager.getConfigData().cooldown();
    }

    private static State createState(final CooldownConfig config) {
        final var duration = Duration.ofSeconds(config.durationSeconds());
        final var nonPositiveDuration = duration.isZero() || duration.isNegative();

        if (config.enabled() && nonPositiveDuration) {
            throw new IllegalArgumentException("cooldownDuration must be positive");
        }

        final Duration expiration = !config.enabled() ? Duration.ofSeconds(1) : duration;

        return new State(
                config.enabled(),
                duration,
                Caffeine.newBuilder().expireAfterWrite(expiration).build());
    }

    private record State(boolean enabled, Duration duration, Cache<UUID, Boolean> cache) {}
}
