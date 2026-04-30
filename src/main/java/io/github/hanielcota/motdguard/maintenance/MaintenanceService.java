package io.github.hanielcota.motdguard.maintenance;

import com.velocitypowered.api.proxy.Player;
import io.github.hanielcota.motdguard.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MaintenanceService implements MaintenanceManager {

  private static final Logger log = LoggerFactory.getLogger(MaintenanceService.class);

  private final ConfigManager configManager;
  private final MaintenanceState state;
  private final KickMessageProvider kickMessage;

  public MaintenanceService(final ConfigManager configManager) {
    this.configManager = configManager;
    this.state = new MaintenanceStateImpl();
    this.kickMessage = new KickMessageProviderImpl(configManager);
  }

  @Override
  public boolean isEnabled() {
    return state.isEnabled();
  }

  @Override
  public void setEnabled(final boolean value) {
    state.setEnabled(value);
  }

  @Override
  public void toggle() {
    state.toggle();
  }

  @Override
  public boolean canBypass(final Player player) {
    return state.canBypass(player);
  }

  @Override
  public Component getKickMessage() {
    return kickMessage.get();
  }

  @Override
  public void syncFromConfig() {
    final boolean configEnabled = configManager.getConfigData().maintenance().enabled();
    state.setEnabled(configEnabled);
    log.info("Maintenance state synced from config: {}", configEnabled);
  }

  @Override
  public void refresh() {
    kickMessage.refresh();
  }
}
