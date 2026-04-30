package io.github.hanielcota.motdguard.ratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.config.ConfigData;
import io.github.hanielcota.motdguard.config.ConfigManager;
import java.net.InetSocketAddress;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

class RateLimiterTest {

  private ConfigManager mockConfigManager(boolean enabled, int maxPings, String blockMessage) {
    final ConfigManager manager = mock(ConfigManager.class);
    final var config =
        new ConfigData(
            new ConfigData.MotdConfig("Line1", "Line2"),
            new ConfigData.MaintenanceConfig(false, "Kick"),
            new ConfigData.RateLimitConfig(enabled, maxPings, blockMessage),
            new ConfigData.CooldownConfig(false, 1),
            new ConfigData.MessagesConfig(
                "a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l"));
    when(manager.getConfigData()).thenReturn(config);
    return manager;
  }

  private ServerPing dummyPing() {
    return ServerPing.builder()
        .description(net.kyori.adventure.text.Component.text("test"))
        .version(new ServerPing.Version(760, "1.19.2"))
        .build();
  }

  @Test
  void shouldAllowPingWhenDisabled() {
    final var limiter = new RateLimiter(mockConfigManager(false, 10, "Block"));
    final var address = new InetSocketAddress("127.0.0.1", 12345);

    final ServerPing result = limiter.tryBlockPing(address, dummyPing());

    assertNull(result);
  }

  @Test
  void shouldAllowPingWithinLimit() {
    final var limiter = new RateLimiter(mockConfigManager(true, 5, "Block"));
    final var address = new InetSocketAddress("127.0.0.1", 12345);

    final ServerPing result = limiter.tryBlockPing(address, dummyPing());

    assertNull(result);
  }

  @Test
  void shouldBlockPingAfterExceedingLimit() {
    final var limiter = new RateLimiter(mockConfigManager(true, 1, "<red>Too many"));
    final var address = new InetSocketAddress("127.0.0.1", 12345);
    final ServerPing original = dummyPing();

    assertNull(limiter.tryBlockPing(address, original));
    final ServerPing blocked = limiter.tryBlockPing(address, original);

    assertNotNull(blocked);
    final String plain = PlainTextComponentSerializer.plainText().serialize(blocked.getDescriptionComponent());
    assertEquals("Too many", plain);
  }

  @Test
  void shouldResetLimitsOnRefresh() {
    final ConfigManager manager = mockConfigManager(true, 1, "Block");
    final var limiter = new RateLimiter(manager);
    final var address = new InetSocketAddress("127.0.0.1", 12345);

    assertNull(limiter.tryBlockPing(address, dummyPing()));
    assertNotNull(limiter.tryBlockPing(address, dummyPing()));

    limiter.refresh();

    assertNull(limiter.tryBlockPing(address, dummyPing()));
  }
}
