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
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.maintenance.MaintenanceService;
import io.github.hanielcota.motdguard.motd.MotdFetcherImpl;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.motd.MotdService;
import io.github.hanielcota.motdguard.motd.MotdBuilderImpl;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import io.github.hanielcota.motdguard.ratelimit.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Plugin(
    id = "motdguard",
    name = "MotdGuard",
    version = "1.0.0",
    description = "Dynamic MOTD with maintenance mode and rate limiting",
    authors = {"HanielCota"})
public final class MotdGuardPlugin {

    private static final Logger log = LoggerFactory.getLogger(MotdGuardPlugin.class);

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

            final var maintenanceManager = createMaintenanceService(configManager);
            final var rateLimiter = createRateLimitService(configManager);
            final var motdProvider = createMotdService(configManager);

            registerListeners(motdProvider, maintenanceManager, rateLimiter);
            registerCommands(configManager, maintenanceManager, rateLimiter, motdProvider);

            log.info("MotdGuard enabled successfully.");
        } catch (final Exception e) {
            exceptionHandler.caughtException("plugin initialization", e);
            throw e;
        }
    }

    private MaintenanceManager createMaintenanceService(final ConfigManager configManager) {
        final var service = new MaintenanceService(configManager);
        service.syncFromConfig();
        return service;
    }

    private RateLimiter createRateLimitService(final ConfigManager configManager) {
        return new RateLimitService(configManager);
    }

    private MotdProvider createMotdService(final ConfigManager configManager) {
        final var fetcher = new MotdFetcherImpl(configManager);
        return new MotdService(new MotdBuilderImpl(fetcher), fetcher);
    }

    private void registerListeners(
            final MotdProvider motdProvider,
            final MaintenanceManager maintenanceManager,
            final RateLimiter rateLimiter
    ) {
        server.getEventManager().register(this, new PingListener(motdProvider, rateLimiter));
        server.getEventManager().register(this, new LoginListener(maintenanceManager));
    }

    private void registerCommands(
            final ConfigManager configManager,
            final MaintenanceManager maintenanceManager,
            final RateLimiter rateLimiter,
            final MotdProvider motdProvider
    ) {
        final var commandManager = new VelocityCommandManager(server, this);
        commandManager.registerCommand(new MotdGuardCommand(
                configManager, maintenanceManager, rateLimiter, motdProvider
        ));
    }
}