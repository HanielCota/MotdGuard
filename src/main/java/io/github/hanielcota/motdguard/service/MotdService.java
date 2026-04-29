package io.github.hanielcota.motdguard.service;

import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.config.ConfigManager;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class MotdService {

  private final ConfigManager configManager;
  private final AtomicReference<Component> cachedMotd = new AtomicReference<>();

  public MotdService(final ConfigManager configManager) {
    this.configManager = configManager;
    refresh();
  }

  public ServerPing buildMotd(final ServerPing original) {
    return original.asBuilder().description(cachedMotd.get()).build();
  }

  public void refresh() {
    final var motdConfig = configManager.getConfigData().getMotd();
    final Component line1 = MiniMessage.miniMessage().deserialize(motdConfig.getLine1());
    final Component line2 = MiniMessage.miniMessage().deserialize(motdConfig.getLine2());
    cachedMotd.set(Component.empty().append(line1).append(Component.newline()).append(line2));
  }
}
