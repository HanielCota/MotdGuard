package io.github.hanielcota.motdguard.ratelimit;

import com.velocitypowered.api.proxy.server.ServerPing;

public interface BlockedPingProvider {

    ServerPing getBlockedPing(ServerPing original);

    void refresh();
}