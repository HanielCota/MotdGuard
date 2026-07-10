package io.github.hanielcota.motdguard.maintenance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MaintenanceManagerTest {

    @TempDir
    Path dataDir;

    private ConfigManager mockConfigManager(boolean enabled, String kickMessage) {
        final ConfigManager manager = mock(ConfigManager.class);
        final var config = new ConfigData(
                new MotdConfig("Line1", "Line2"),
                new MaintenanceConfig(enabled, kickMessage, null, null),
                new RateLimitConfig(false, 10, "Block"),
                new CooldownConfig(false, 1),
                new MessagesConfig("a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l"));
        when(manager.getConfigData()).thenReturn(config);
        return manager;
    }

    @Test
    void shouldLoadInitialStateFromConfig() {
        final var maintenanceManager = new MaintenanceManager(mockConfigManager(true, "<red>Kick"), dataDir);

        assertTrue(maintenanceManager.isEnabled());
        assertEquals(
                "Kick",
                net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                        .serialize(maintenanceManager.getState().kickMessage()));
    }

    @Test
    void shouldDisableMaintenance() {
        final var maintenanceManager = new MaintenanceManager(mockConfigManager(true, "Kick"), dataDir);

        maintenanceManager.setEnabled(false);

        assertFalse(maintenanceManager.isEnabled());
    }

    @Test
    void shouldToggleMaintenance() {
        final var maintenanceManager = new MaintenanceManager(mockConfigManager(false, "Kick"), dataDir);

        assertFalse(maintenanceManager.isEnabled());

        final boolean toggled = maintenanceManager.toggle();

        assertTrue(toggled);
        assertTrue(maintenanceManager.isEnabled());
    }

    @Test
    void shouldPreserveEnabledStateOnRefresh() {
        final var maintenanceManager = new MaintenanceManager(mockConfigManager(false, "Old"), dataDir);
        maintenanceManager.setEnabled(true);

        maintenanceManager.refresh();

        assertTrue(maintenanceManager.isEnabled());
    }

    @Test
    void shouldPersistEnabledStateAcrossInstances() {
        final var first = new MaintenanceManager(mockConfigManager(false, "Kick"), dataDir);
        first.setEnabled(true);

        // Simulates a proxy restart: a new instance reads the persisted state instead of the config
        // default (false).
        final var second = new MaintenanceManager(mockConfigManager(false, "Kick"), dataDir);

        assertTrue(second.isEnabled());
    }

    @Test
    void shouldUseConfigDefaultWhenNoPersistedState() {
        final var manager = new MaintenanceManager(mockConfigManager(true, "Kick"), dataDir);

        assertTrue(manager.isEnabled());
    }

    @Test
    void shouldIgnoreInvalidPersistedStateAndUseConfigDefault() throws Exception {
        Files.writeString(dataDir.resolve("maintenance.state"), "not-a-boolean");

        final var manager = new MaintenanceManager(mockConfigManager(true, "Kick"), dataDir);

        assertTrue(manager.isEnabled());
    }
}
