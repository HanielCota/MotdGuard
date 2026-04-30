package io.github.hanielcota.motdguard.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;

public final class PingListener {

    private final MotdProvider motdProvider;
    private final RateLimiter rateLimiter;

    public PingListener(final MotdProvider motdProvider, final RateLimiter rateLimiter) {
        this.motdProvider = motdProvider;
        this.rateLimiter = rateLimiter;
    }

    @Subscribe
    public void onProxyPing(final ProxyPingEvent event) {
        if (!rateLimiter.isAllowed(event.getConnection().getRemoteAddress())) {
            event.setPing(rateLimiter.buildBlockedPing(event.getPing()));
            return;
        }
        event.setPing(motdProvider.buildMotd(event.getPing()));
    }
}