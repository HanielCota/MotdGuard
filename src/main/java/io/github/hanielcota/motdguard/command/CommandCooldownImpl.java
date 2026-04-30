package io.github.hanielcota.motdguard.command;

import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.CooldownService;

import java.time.Duration;

public final class CommandCooldownImpl implements CommandCooldown {

    private final CooldownService cooldownService;

    public CommandCooldownImpl(final ConfigManager configManager) {
        this.cooldownService = new CooldownService(Duration.ofMinutes(1));
    }

    @Override
    public boolean isOnCooldown(final String playerId) {
        return cooldownService.isOnCooldown(playerId);
    }

    @Override
    public void setUsed(final String playerId) {
        cooldownService.setUsed(playerId);
    }
}