package io.github.hanielcota.motdguard.util;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CooldownService {

    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();
    private final Duration cooldownDuration;

    public CooldownService(final Duration cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public boolean isOnCooldown(final String playerId) {
        final var lastUsed = cooldowns.get(playerId);
        if (lastUsed == null) return false;

        if (System.currentTimeMillis() - lastUsed < cooldownDuration.toMillis()) {
            return true;
        }
        cooldowns.remove(playerId);
        return false;
    }

    public void setUsed(final String playerId) {
        cooldowns.put(playerId, System.currentTimeMillis());
    }
}