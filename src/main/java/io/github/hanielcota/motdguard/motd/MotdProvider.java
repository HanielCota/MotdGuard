package io.github.hanielcota.motdguard.motd;

import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class MotdProvider {

  private final ConfigManager configManager;
  private final AtomicReference<Component> cached = new AtomicReference<>(Component.empty());

  public MotdProvider(final ConfigManager configManager) {
    this.configManager = configManager;
    refresh();
  }

  public ServerPing buildMotd(final ServerPing original) {
    return original.asBuilder().description(cached.get()).build();
  }

  public void refresh() {
    final var motdConfig = configManager.getConfigData().motd();
    final Component line1 = MiniMessageUtil.deserialize(motdConfig.line1());
    final Component line2 = MiniMessageUtil.deserialize(motdConfig.line2());

    final Component newCached = Component.text()
            .append(line1)
            .append(Component.newline())
            .append(line2)
            .build();

    cached.set(newCached);
    log.info("MOTD refreshed");
  }
}
