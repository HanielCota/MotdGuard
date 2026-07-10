package io.github.hanielcota.motdguard.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.hanielcota.motdguard.config.ConfigData;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.CooldownConfig;
import io.github.hanielcota.motdguard.config.MaintenanceConfig;
import io.github.hanielcota.motdguard.config.MessagesConfig;
import io.github.hanielcota.motdguard.config.MotdConfig;
import io.github.hanielcota.motdguard.config.RateLimitConfig;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CooldownServiceTest {

    private static ConfigManager configManager(final CooldownConfig cooldown) {
        final var manager = mock(ConfigManager.class);
        when(manager.getConfigData()).thenReturn(configData(cooldown));
        return manager;
    }

    private static ConfigData configData(final CooldownConfig cooldown) {
        return new ConfigData(
                new MotdConfig("L1", "L2"),
                new MaintenanceConfig(false, "Kick"),
                new RateLimitConfig(false, 10, "Block"),
                cooldown,
                new MessagesConfig("a", "b", "c", "d", "e", "enabled", "disabled", "h", "r", "m", "mo", "mf", "cd"));
    }

    @Test
    void disabledServiceShouldAlwaysAllow() {
        final var service = new CooldownService(configManager(new CooldownConfig(false, 1)));
        final UUID playerId = UUID.randomUUID();

        assertFalse(service.tryAcquire(playerId));
        assertFalse(service.tryAcquire(playerId));
    }

    @Test
    void tryAcquireShouldBeAtomicAndReportContention() {
        final var service = new CooldownService(configManager(new CooldownConfig(true, 3600)));
        final UUID playerId = UUID.randomUUID();

        assertFalse(service.tryAcquire(playerId));
        assertTrue(service.tryAcquire(playerId));
    }

    @Test
    void shouldApplyRefreshedConfiguration() {
        final var manager = mock(ConfigManager.class);
        when(manager.getConfigData())
                .thenReturn(configData(new CooldownConfig(true, 3600)))
                .thenReturn(configData(new CooldownConfig(false, 10)));

        final var service = new CooldownService(manager);
        final UUID playerId = UUID.randomUUID();

        assertFalse(service.tryAcquire(playerId));
        assertTrue(service.tryAcquire(playerId));

        service.refresh();

        assertFalse(service.tryAcquire(playerId));
    }

    @Test
    void refreshWithUnchangedConfigShouldPreserveActiveCooldowns() {
        final var service = new CooldownService(configManager(new CooldownConfig(true, 3600)));
        final UUID playerId = UUID.randomUUID();

        assertFalse(service.tryAcquire(playerId));

        // A reload that does not change the cooldown config must not wipe in-progress cooldowns.
        service.refresh();

        assertTrue(service.tryAcquire(playerId));
    }

    @Test
    void refreshWithChangedDurationShouldPreserveActiveCooldowns() {
        final var manager = mock(ConfigManager.class);
        when(manager.getConfigData())
                .thenReturn(configData(new CooldownConfig(true, 3600)))
                .thenReturn(configData(new CooldownConfig(true, 30)));

        final var service = new CooldownService(manager);
        final UUID playerId = UUID.randomUUID();

        assertFalse(service.tryAcquire(playerId));

        // A reload that changes the duration (but keeps the service enabled) must still preserve
        // in-progress cooldowns instead of wiping them.
        service.refresh();

        assertTrue(service.tryAcquire(playerId));
    }

    @Test
    void shouldRejectNullPlayerId() {
        final var service = new CooldownService(configManager(new CooldownConfig(true, 10)));
        assertThrows(NullPointerException.class, () -> service.tryAcquire(null));
    }
}
