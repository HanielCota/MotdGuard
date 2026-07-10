package io.github.hanielcota.motdguard.motd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        final ConfigManager manager = mock(ConfigManager.class);
        final var config = new ConfigData(
                new MotdConfig(line1, line2),
                new MaintenanceConfig(false, "Kick"),
                new RateLimitConfig(false, 10, "Block"),
                new CooldownConfig(false, 1),
                new MessagesConfig("a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l"));
        when(manager.getConfigData()).thenReturn(config);
        return manager;
    }

    @Test
    void shouldBuildMotdWithTwoLines() {
        final var provider = new MotdProvider(mockConfigManager("Line1", "Line2"));
        final ServerPing original = ServerPing.builder()
                .description(Component.text("test"))
                .version(new ServerPing.Version(1, "1.0"))
                .build();

        final ServerPing result = provider.buildMotd(original);

        assertNotNull(result);
        final String plain = PlainTextComponentSerializer.plainText().serialize(result.getDescriptionComponent());
        assertEquals("Line1\nLine2", plain);
    }

    @Test
    void shouldRefreshMotd() {
        final ConfigManager manager = mockConfigManager("First", "Second");
        final var provider = new MotdProvider(manager);

        when(manager.getConfigData())
                .thenReturn(new ConfigData(
                        new MotdConfig("Updated", "MOTD"),
                        new MaintenanceConfig(false, "Kick"),
                        new RateLimitConfig(false, 10, "Block"),
                        new CooldownConfig(false, 1),
                        new MessagesConfig(
                                "a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l")));

        provider.refresh();
        final ServerPing original = ServerPing.builder()
                .description(Component.text("test"))
                .version(new ServerPing.Version(1, "1.0"))
                .build();
        final ServerPing result = provider.buildMotd(original);

        final String plain = PlainTextComponentSerializer.plainText().serialize(result.getDescriptionComponent());
        assertEquals("Updated\nMOTD", plain);
    }
}
