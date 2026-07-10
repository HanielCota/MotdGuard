package io.github.hanielcota.motdguard.config;

public record CooldownConfig(boolean enabled, int durationSeconds) {

    public CooldownConfig {
        if (enabled && durationSeconds < 1) {
            throw new IllegalArgumentException("cooldown.duration-seconds must be at least 1");
        }
    }
}
