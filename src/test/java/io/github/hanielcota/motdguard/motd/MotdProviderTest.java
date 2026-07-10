package io.github.hanielcota.motdguard.motd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.config.ConfigData;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.CooldownConfig;
import io.github.hanielcota.motdguard.config.MaintenanceConfig;
import io.github.hanielcota.motdguard.config.MessagesConfig;
import io.github.hanielcota.motdguard.config.MotdConfig;
import io.github.hanielcota.motdguard.config.RateLimitConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

class MotdProviderTest {

    private ConfigManager mockConfigManager(String line1, String line2) {
        return mockConfigManager(line1, line2, null, null);
    }

    private ConfigManager mockConfigManager(String line1, String line2, String maintenanceLine1, String maintenanceLine2) {
        final ConfigManager manager = mock(ConfigManager.class);
        final var config = new ConfigData(
                new MotdConfig(line1, line2),
                new MaintenanceConfig(false, "Kick", maintenanceLine1, maintenanceLine2),
                new RateLimitConfig(false, 10, "Block"),
                new CooldownConfig(false, 1),
                new MessagesConfig("a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l"));
        when(manager.getConfigData()).thenReturn(config);
        return manager;
    }

    private static ServerPing originalPing() {
        return ServerPing.builder()
                .description(Component.text("test"))
                .version(new ServerPing.Version(1, "1.0"))
                .build();
    }

    private static String plain(final ServerPing ping) {
        return PlainTextComponentSerializer.plainText().serialize(ping.getDescriptionComponent());
    }

    @Test
    void shouldBuildMotdWithTwoLines() {
        final var provider = new MotdProvider(mockConfigManager("Line1", "Line2"));

        final ServerPing result = provider.buildMotd(originalPing());

        assertNotNull(result);
        assertEquals("Line1\nLine2", plain(result));
    }

    @Test
    void shouldRefreshMotd() {
        final ConfigManager manager = mockConfigManager("First", "Second");
        final var provider = new MotdProvider(manager);

        when(manager.getConfigData())
                .thenReturn(new ConfigData(
                        new MotdConfig("Updated", "MOTD"),
                        new MaintenanceConfig(false, "Kick", null, null),
                        new RateLimitConfig(false, 10, "Block"),
                        new CooldownConfig(false, 1),
                        new MessagesConfig(
                                "a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l")));

        provider.refresh();

        assertEquals("Updated\nMOTD", plain(provider.buildMotd(originalPing())));
    }

    @Test
    void shouldUseMaintenanceMotdWhenConfigured() {
        final var provider = new MotdProvider(mockConfigManager("Normal1", "Normal2", "<red>Maint1", "Maint2"));

        assertEquals("Maint1\nMaint2", plain(provider.buildMaintenanceMotd(originalPing())));
    }

    @Test
    void shouldFallbackToNormalMotdWhenMaintenanceMotdAbsent() {
        final var provider = new MotdProvider(mockConfigManager("Normal1", "Normal2"));

        assertEquals("Normal1\nNormal2", plain(provider.buildMaintenanceMotd(originalPing())));
    }

    @Test
    void shouldResolvePlaceholdersFromOriginalPing() {
        final var provider = new MotdProvider(mockConfigManager("Online: {online}/{max}", "{version}"));

        assertEquals("Online: 0/0\n1.0", plain(provider.buildMotd(originalPing())));
    }

    @Test
    void shouldBuildBlockedMotdHidingDetails() {
        final var provider = new MotdProvider(mockConfigManager("Line1", "Line2"));

        final ServerPing result = provider.buildBlockedMotd(originalPing(), Component.text("Blocked"));

        assertEquals("Blocked", plain(result));
        assertEquals(0, result.getVersion().getProtocol());
        assertTrue(result.getPlayers().isEmpty());
    }
}
