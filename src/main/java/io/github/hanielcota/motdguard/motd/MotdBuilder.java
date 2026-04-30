package io.github.hanielcota.motdguard.motd;

import com.velocitypowered.api.proxy.server.ServerPing;

public interface MotdBuilder {

    ServerPing build(ServerPing original);
}