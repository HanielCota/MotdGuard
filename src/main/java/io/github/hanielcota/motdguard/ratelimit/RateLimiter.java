package io.github.hanielcota.motdguard.ratelimit;

import com.velocitypowered.api.proxy.server.ServerPing;

import java.net.InetSocketAddress;

public interface RateLimiter {

    boolean isAllowed(InetSocketAddress address);

    ServerPing buildBlockedPing(ServerPing original);

    void refresh();
}