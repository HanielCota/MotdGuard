package io.github.hanielcota.motdguard;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.hanielcota.motdguard.command.CooldownService;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Wires the MotdGuard object graph with Guice.
 *
 * <p>The Velocity proxy injects the {@link ProxyServer} and plugin {@link DataDirectory} into the
 * entry point; this module publishes them to the injector so the rest of the graph can depend on
 * them. All long-lived services are bound as singletons so a configuration reload mutates shared
 * state rather than constructing parallel instances.
 */
public final class MotdGuardModule extends AbstractModule {

    private final ProxyServer server;
    private final Path dataDirectory;

    public MotdGuardModule(final ProxyServer server, final Path dataDirectory) {
        this.server = Objects.requireNonNull(server, "server");
        this.dataDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory");
    }

    @Override
    protected void configure() {
        bind(ProxyServer.class).toInstance(server);
        bind(Path.class).annotatedWith(DataDirectory.class).toInstance(dataDirectory);

        bind(ConfigManager.class).in(Singleton.class);
        bind(MaintenanceManager.class).in(Singleton.class);
        bind(RateLimiter.class).in(Singleton.class);
        bind(MotdProvider.class).in(Singleton.class);
        bind(CooldownService.class).in(Singleton.class);
        bind(PluginExceptionHandler.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    List<Reloadable> reloadables(
            final MaintenanceManager maintenanceManager,
            final RateLimiter rateLimiter,
            final MotdProvider motdProvider,
            final CooldownService cooldownService) {
        return List.of(maintenanceManager, rateLimiter, motdProvider, cooldownService);
    }
}
