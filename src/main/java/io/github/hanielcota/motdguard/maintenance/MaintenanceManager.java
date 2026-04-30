package io.github.hanielcota.motdguard.maintenance;

import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class MaintenanceManager {

  private final ConfigManager configManager;
  private final AtomicBoolean enabled = new AtomicBoolean();
  private final AtomicReference<Component> kickMessage = new AtomicReference<>(Component.empty());

  public MaintenanceManager(final ConfigManager configManager) {
    this.configManager = configManager;
    refresh();
  }

  public boolean isEnabled() {
    return enabled.get();
  }

  public void setEnabled(final boolean value) {
    enabled.set(value);
    logStateChange(value);
  }

  public void toggle() {
    boolean previous;
    do {
      previous = enabled.get();
    } while (!enabled.compareAndSet(previous, !previous));
    logStateChange(!previous);
  }

  public Component getKickMessage() {
    return kickMessage.get();
  }

  public void refresh() {
    final var maintenance = configManager.getConfigData().maintenance();
    enabled.set(maintenance.enabled());
    kickMessage.set(MiniMessageUtil.deserialize(maintenance.kickMessage()));
    log.info("Maintenance state refreshed");
  }

  private void logStateChange(final boolean enabled) {
    log.info("Maintenance mode set to: {}", enabled);
  }
}
