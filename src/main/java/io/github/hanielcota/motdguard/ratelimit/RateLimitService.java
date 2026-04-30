package io.github.hanielcota.motdguard.ratelimit;

import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.config.ConfigManager;

import java.net.InetSocketAddress;

public final class RateLimitService implements RateLimiter {

    private final RateLimitChecker checker;
    private final BlockedPingProvider blockedPingProvider;

    public RateLimitService(final ConfigManager configManager) {
        this.checker = new RateLimitCheckerImpl(configManager);
        this.blockedPingProvider = new BlockedPingProviderImpl(configManager);
    }

    @Override
    public boolean isAllowed(final InetSocketAddress address) {
        return checker.isAllowed(address);
    }

    @Override
    public ServerPing buildBlockedPing(final ServerPing original) {
        return blockedPingProvider.getBlockedPing(original);
    }

    @Override
    public void refresh() {
        checker.refresh();
        blockedPingProvider.refresh();
    }
}