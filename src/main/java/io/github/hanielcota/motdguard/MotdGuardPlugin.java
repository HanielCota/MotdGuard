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
import io.github.hanielcota.motdguard.exception.PluginExceptionHandler;
import io.github.hanielcota.motdguard.listener.LoginListener;
import io.github.hanielcota.motdguard.listener.PingListener;
import io.github.hanielcota.motdguard.service.MaintenanceService;
import io.github.hanielcota.motdguard.service.MotdService;
import io.github.hanielcota.motdguard.service.RateLimitService;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

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
        final Thread.UncaughtExceptionHandler previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        final PluginExceptionHandler exceptionHandler = new PluginExceptionHandler(dataDirectory, previousHandler);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

        try {
            initializePlugin();
        } catch (final Exception e) {
            exceptionHandler.caughtException("plugin initialization", e);
            throw e;
        }
    }

    private void initializePlugin() {
        final ConfigManager configManager = new ConfigManager(dataDirectory);
        configManager.load();

        final MaintenanceService maintenanceService = new MaintenanceService(configManager);
        maintenanceService.syncFromConfig();

        final RateLimitService rateLimitService = new RateLimitService(configManager);
        final MotdService motdService = new MotdService(configManager);

        final PingListener pingListener = new PingListener(motdService, rateLimitService);
        final LoginListener loginListener = new LoginListener(maintenanceService);

        server.getEventManager().register(this, pingListener);
        server.getEventManager().register(this, loginListener);

        try {
            final VelocityCommandManager commandManager = new VelocityCommandManager(server, this);
            commandManager.registerCommand(new MotdGuardCommand(configManager, maintenanceService, rateLimitService, motdService));
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to register commands", e);
        }

        log.info("MotdGuard enabled successfully.");
    }
}
