package io.github.hanielcota.motdguard;

import co.aikar.commands.VelocityCommandManager;
import com.google.inject.Guice;
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
import io.github.hanielcota.motdguard.util.PluginExceptionHandler;
import java.nio.file.Path;
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

  @Inject
  public MotdGuardPlugin(final ProxyServer server, @DataDirectory final Path dataDirectory) {
    this.server = Objects.requireNonNull(server, "server");
    this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
  }

  @Subscribe
  public void onProxyInitialize(final ProxyInitializeEvent event) {
    final var injector = Guice.createInjector(new MotdGuardModule(server, dataDirectory));
    final var exceptionHandler = injector.getInstance(PluginExceptionHandler.class);

    try {
      final var configManager = injector.getInstance(ConfigManager.class);
      configManager.load();

      server.getEventManager().register(this, injector.getInstance(PingListener.class));
      server.getEventManager().register(this, injector.getInstance(LoginListener.class));

      commandManager = new VelocityCommandManager(server, this);
      commandManager.registerCommand(injector.getInstance(MotdGuardCommand.class));

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
