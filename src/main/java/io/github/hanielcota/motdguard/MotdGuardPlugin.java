package io.github.hanielcota.motdguard;

import co.aikar.commands.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.hanielcota.motdguard.command.MotdGuardCommand;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.listener.LoginListener;
import io.github.hanielcota.motdguard.listener.PingListener;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import io.github.hanielcota.motdguard.util.CooldownService;
import io.github.hanielcota.motdguard.util.PluginExceptionHandler;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Plugin(
    id = "motdguard",
    name = "MotdGuard",
    version = "1.0.0-rc.1",
    description = "Dynamic MOTD with maintenance mode and rate limiting",
    authors = {"HanielCota"})
public final class MotdGuardPlugin {

  private final ProxyServer server;
  private final Path dataDirectory;
  private VelocityCommandManager commandManager;
  private PluginExceptionHandler exceptionHandler;

  @Inject
  public MotdGuardPlugin(final ProxyServer server, @DataDirectory final Path dataDirectory) {
    this.server = Objects.requireNonNull(server, "server");
    this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
  }

  @Subscribe
  public void onProxyInitialize(final ProxyInitializeEvent event) {
    this.exceptionHandler = new PluginExceptionHandler(dataDirectory);

    try {
      final var configManager = new ConfigManager(dataDirectory);
      configManager.load();

      final var maintenanceManager = new MaintenanceManager(configManager);
      final var rateLimiter = new RateLimiter(configManager);
      final var motdProvider = new MotdProvider(configManager);

      server.getEventManager().register(this, new PingListener(motdProvider, rateLimiter));
      server.getEventManager().register(this, new LoginListener(maintenanceManager));

      final var cooldownConfig = configManager.getConfigData().cooldown();
      final var cooldown =
          new CooldownService(
              cooldownConfig.enabled(), Duration.ofSeconds(cooldownConfig.durationSeconds()));

      commandManager = new VelocityCommandManager(server, this);
      commandManager.registerCommand(
          new MotdGuardCommand(
              configManager,
              maintenanceManager,
              rateLimiter,
              motdProvider,
              cooldown,
              exceptionHandler));

      log.info("MotdGuard enabled successfully.");
    } catch (final Exception e) {
      exceptionHandler.caughtException("plugin initialization", e);
      throw e;
    }
  }

  @Subscribe
  public void onProxyShutdown(final ProxyShutdownEvent event) {
    if (commandManager != null) {
      commandManager.unregisterCommands();
    }

    log.info("MotdGuard disabled.");
  }
}
