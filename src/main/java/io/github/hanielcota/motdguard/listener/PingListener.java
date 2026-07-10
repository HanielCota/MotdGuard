package io.github.hanielcota.motdguard.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class PingListener {

    @NonNull private final MotdProvider motdProvider;

    @NonNull private final RateLimiter rateLimiter;

    @NonNull private final MaintenanceManager maintenanceManager;

    @Subscribe
    public void onProxyPing(final ProxyPingEvent event) {
        final var original = event.getPing();
        final var address = event.getConnection().getRemoteAddress();

        // Rate limiting runs first so an abusive address is rejected before any presentation work,
        // and never learns whether the proxy is in maintenance.
        if (rateLimiter.isBlocked(address)) {
            event.setPing(motdProvider.buildBlockedMotd(original, rateLimiter.blockMessage()));
            return;
        }

        if (maintenanceManager.isEnabled()) {
            event.setPing(motdProvider.buildMaintenanceMotd(original));
            return;
        }

        event.setPing(motdProvider.buildMotd(original));
    }
}
