package io.github.hanielcota.motdguard.ratelimit;

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
import java.net.InetSocketAddress;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

class RateLimiterTest {

    private static final InetSocketAddress ADDRESS = new InetSocketAddress("127.0.0.1", 12345);

    private ConfigManager mockConfigManager(boolean enabled, int maxPings, String blockMessage) {
        final ConfigManager manager = mock(ConfigManager.class);
        final var config = new ConfigData(
                new MotdConfig("Line1", "Line2"),
                new MaintenanceConfig(false, "Kick", null, null),
                new RateLimitConfig(enabled, maxPings, blockMessage),
                new CooldownConfig(false, 1),
                new MessagesConfig("a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l"));
        when(manager.getConfigData()).thenReturn(config);
        return manager;
    }

    @Test
    void shouldAllowPingWhenDisabled() {
        final var limiter = new RateLimiter(mockConfigManager(false, 10, "Block"));

        assertFalse(limiter.isBlocked(ADDRESS));
    }

    @Test
    void shouldAllowPingWithinLimit() {
        final var limiter = new RateLimiter(mockConfigManager(true, 5, "Block"));

        assertFalse(limiter.isBlocked(ADDRESS));
    }

    @Test
    void shouldBlockPingAfterExceedingLimit() {
        final var limiter = new RateLimiter(mockConfigManager(true, 1, "<red>Too many"));

        assertFalse(limiter.isBlocked(ADDRESS));
        assertTrue(limiter.isBlocked(ADDRESS));

        final String plain = PlainTextComponentSerializer.plainText().serialize(limiter.blockMessage());
        assertEquals("Too many", plain);
    }

    @Test
    void shouldResetLimitsOnRefresh() {
        final ConfigManager manager = mockConfigManager(true, 1, "Block");
        final var limiter = new RateLimiter(manager);

        assertFalse(limiter.isBlocked(ADDRESS));
        assertTrue(limiter.isBlocked(ADDRESS));

        limiter.refresh();

        assertFalse(limiter.isBlocked(ADDRESS));
    }

    @Test
    void shouldBlockPingWhenIpCannotBeDetermined() {
        final var limiter =
                new RateLimiter(mockConfigManager(true, 5, "<red>Block"), ignored -> java.util.Optional.empty());

        assertTrue(limiter.isBlocked(ADDRESS));
    }
}
