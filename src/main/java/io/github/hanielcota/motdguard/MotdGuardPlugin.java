package io.github.hanielcota.motdguard;

import co.aikar.commands.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Main plugin class for MotdGuard, a Velocity plugin providing dynamic MOTD, maintenance mode, and
 * rate limiting features.
 *
 * <p>This plugin is annotated with Velocity's {@link Plugin} annotation and is automatically loaded
 * when Velocity starts. It handles initialization of all services, registration of event listeners,
 * and command registration.
 *
 * @author HanielCota
 * @version 1.0.0
 */
@Slf4j
@Plugin(
    id = "motdguard",
    name = "MotdGuard",
    version = "1.0.0",
    description = "Dynamic MOTD with maintenance mode and rate limiting",
    authors = {"HanielCota"})
public final class MotdGuardPlugin {

  private final ProxyServer server;
  private final Path dataDirectory;

  @Inject
  public MotdGuardPlugin(final ProxyServer server, @DataDirectory final Path dataDirectory) {
    this.server = server;
    this.dataDirectory = dataDirectory;
  }

  @Subscribe
  private void onProxyInitialize(final ProxyInitializeEvent event) {
    final var previousHandler = Thread.getDefaultUncaughtExceptionHandler();
    final var exceptionHandler = new PluginExceptionHandler(dataDirectory, previousHandler);
    Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

    try {
      final var configManager = new ConfigManager(dataDirectory);
      configManager.load();

      final var maintenanceManager = new MaintenanceManager(configManager);
      final var rateLimiter = new RateLimiter(configManager);
      final var motdProvider = new MotdProvider(configManager);

      server.getEventManager().register(this, new PingListener(motdProvider, rateLimiter));
      server.getEventManager().register(this, new LoginListener(maintenanceManager));

      final var cooldown = new CooldownService(Duration.ofMinutes(1));
      server.getScheduler()
          .buildTask(this, cooldown::cleanup)
          .delay(Duration.ofMinutes(5))
          .repeat(Duration.ofMinutes(5))
          .schedule();

      final var commandManager = new VelocityCommandManager(server, this);
      commandManager.registerCommand(
          new MotdGuardCommand(
              configManager, maintenanceManager, rateLimiter, motdProvider, cooldown));

      log.info("MotdGuard enabled successfully.");
    } catch (final Exception e) {
      exceptionHandler.caughtException("plugin initialization", e);
      throw e;
    }
  }
}
