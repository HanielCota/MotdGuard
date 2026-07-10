package io.github.hanielcota.motdguard.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.aikar.commands.CommandIssuer;
import com.velocitypowered.api.command.CommandSource;
import io.github.hanielcota.motdguard.config.ConfigData;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.CooldownConfig;
import io.github.hanielcota.motdguard.config.MaintenanceConfig;
import io.github.hanielcota.motdguard.config.MessagesConfig;
import io.github.hanielcota.motdguard.config.MotdConfig;
import io.github.hanielcota.motdguard.config.RateLimitConfig;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import io.github.hanielcota.motdguard.util.CooldownService;
import io.github.hanielcota.motdguard.util.PluginExceptionHandler;
import java.time.Duration;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

class MotdGuardCommandTest {

  private static ConfigData configData(final CooldownConfig cooldown) {
    return new ConfigData(
        new MotdConfig("L1", "L2"),
        new MaintenanceConfig(false, "Kick"),
        new RateLimitConfig(false, 10, "Block"),
        cooldown,
        new MessagesConfig(
            "a", "b", "c", "d", "e", "enabled", "disabled", "h", "r", "m", "mo", "mf", "cd"));
  }

  private record Mocks(
      ConfigManager configManager,
      MaintenanceManager maintenanceManager,
      RateLimiter rateLimiter,
      MotdProvider motdProvider,
      PluginExceptionHandler exceptionHandler,
      CommandSource source) {}

  private Mocks mocks() {
    final var configManager = mock(ConfigManager.class);
    when(configManager.getConfigData()).thenReturn(configData(new CooldownConfig(false, 1)));

    final var source = mock(CommandSource.class);
    return new Mocks(
        configManager,
        mock(MaintenanceManager.class),
        mock(RateLimiter.class),
        mock(MotdProvider.class),
        mock(PluginExceptionHandler.class),
        source);
  }

  private CommandIssuer issuer(final boolean player, final CommandSource source) {
    final var issuer = mock(CommandIssuer.class);
    when(issuer.isPlayer()).thenReturn(player);
    when(issuer.getUniqueId()).thenReturn(UUID.randomUUID());
    when(issuer.getIssuer()).thenReturn(source);
    return issuer;
  }

  @Test
  void reloadShouldRefreshServicesAndNotifySuccess() {
    final var m = mocks();
    final var cooldown = mock(CooldownService.class);
    final var command =
        new MotdGuardCommand(
            m.configManager(),
            m.maintenanceManager(),
            m.rateLimiter(),
            m.motdProvider(),
            cooldown,
            m.exceptionHandler());

    command.onReload(issuer(false, m.source()));

    verify(m.configManager()).reload();
    verify(m.maintenanceManager()).refresh();
    verify(m.rateLimiter()).refresh();
    verify(m.motdProvider()).refresh();
    verify(m.exceptionHandler(), never()).caughtException(anyString(), any());
    verify(m.source(), atLeastOnce()).sendMessage(any(Component.class));
  }

  @Test
  void reloadShouldRouteFailureToExceptionHandler() {
    final var m = mocks();
    doThrow(new IllegalStateException("boom")).when(m.configManager()).reload();
    final var command =
        new MotdGuardCommand(
            m.configManager(),
            m.maintenanceManager(),
            m.rateLimiter(),
            m.motdProvider(),
            mock(CooldownService.class),
            m.exceptionHandler());

    command.onReload(issuer(false, m.source()));

    verify(m.exceptionHandler())
        .caughtException(eq("configuration reload"), any(IllegalStateException.class));
    verify(m.maintenanceManager(), never()).refresh();
    verify(m.source(), atLeastOnce()).sendMessage(any(Component.class));
  }

  @Test
  void secondCommandShouldBeBlockedByCooldown() {
    final var m = mocks();
    when(m.configManager().getConfigData()).thenReturn(configData(new CooldownConfig(true, 3600)));
    final var cooldown = new CooldownService(true, Duration.ofHours(1));
    final var command =
        new MotdGuardCommand(
            m.configManager(),
            m.maintenanceManager(),
            m.rateLimiter(),
            m.motdProvider(),
            cooldown,
            m.exceptionHandler());

    final var playerIssuer = issuer(true, m.source());
    command.onReload(playerIssuer);
    command.onReload(playerIssuer);

    verify(m.configManager(), times(1)).reload();
  }
}
