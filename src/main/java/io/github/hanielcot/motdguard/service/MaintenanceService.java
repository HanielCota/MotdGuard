package io.github.hanielcot.motdguard.service;

import com.velocitypowered.api.proxy.Player;
import io.github.hanielcot.motdguard.config.ConfigManager;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Slf4j
public final class MaintenanceService {

  private final ConfigManager configManager;
  private final AtomicBoolean enabled = new AtomicBoolean();
  private final AtomicReference<Component> kickMessage = new AtomicReference<>();

  public MaintenanceService(final ConfigManager configManager) {
    this.configManager = configManager;
    refresh();
  }

  public boolean isEnabled() {
    return enabled.get();
  }

  public void setEnabled(final boolean enabled) {
    this.enabled.set(enabled);
    log.info("Maintenance mode set to: {}", enabled);
  }

  public void toggle() {
    boolean previous;
    boolean next;
    do {
      previous = enabled.get();
      next = !previous;
    } while (!enabled.compareAndSet(previous, next));
    log.info("Maintenance mode toggled: {}", next);
  }

  public boolean canBypass(final Player player) {
    return player.hasPermission("motdguard.bypass");
  }

  public Component getKickMessage() {
    return kickMessage.get();
  }

  public void syncFromConfig() {
    final boolean configEnabled = configManager.getConfigData().getMaintenance().isEnabled();
    enabled.set(configEnabled);
    log.info("Maintenance state synced from config: {}", configEnabled);
  }

  public void refresh() {
    final String raw = configManager.getConfigData().getMaintenance().getKickMessage();
    kickMessage.set(MiniMessage.miniMessage().deserialize(raw));
  }
}
