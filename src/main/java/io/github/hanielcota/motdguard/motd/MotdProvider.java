package io.github.hanielcota.motdguard.motd;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.Reloadable;
import io.github.hanielcota.motdguard.config.ConfigManager;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class MotdProvider implements Reloadable {

  private final ConfigManager configManager;
  private final AtomicReference<Component> cached = new AtomicReference<>(Component.empty());

  @Inject
  public MotdProvider(final ConfigManager configManager) {
    this.configManager = Objects.requireNonNull(configManager, "configManager");
    refresh();
  }

  public ServerPing buildMotd(final ServerPing original) {
    final Component motd = cached.get();

    return Objects.requireNonNull(original, "original").asBuilder().description(motd).build();
  }

  public void refresh() {
    final var motdConfig = configManager.getConfigData().motd();

    final Component line1 = motdConfig.line1Component();
    final Component line2 = motdConfig.line2Component();

    cached.set(Component.text().append(line1).append(Component.newline()).append(line2).build());

    log.info("MOTD refreshed");
  }
}
