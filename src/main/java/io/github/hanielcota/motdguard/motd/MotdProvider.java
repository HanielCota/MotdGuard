package io.github.hanielcota.motdguard.motd;

import com.velocitypowered.api.proxy.server.ServerPing;

public interface MotdProvider {

    ServerPing buildMotd(ServerPing original);

    void refresh();
}