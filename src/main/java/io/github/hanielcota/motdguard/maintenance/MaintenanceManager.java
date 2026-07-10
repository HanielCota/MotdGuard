package io.github.hanielcota.motdguard.maintenance;

import com.google.inject.Inject;
import io.github.hanielcota.motdguard.Reloadable;
import io.github.hanielcota.motdguard.config.ConfigManager;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class MaintenanceManager implements Reloadable {

  private final ConfigManager configManager;
  private final AtomicReference<State> state;

  @Inject
  public MaintenanceManager(final ConfigManager configManager) {
    this.configManager = Objects.requireNonNull(configManager, "configManager");

    final var maintenanceConfig = configManager.getConfigData().maintenance();
    this.state =
        new AtomicReference<>(
            new State(maintenanceConfig.enabled(), maintenanceConfig.kickMessageComponent()));

    log.info("Maintenance initial state loaded from config");
  }

  public State getState() {
    return state.get();
  }

  public boolean isEnabled() {
    return state.get().enabled();
  }

  public Component getKickMessage() {
    return state.get().kickMessage();
  }

  public void setEnabled(final boolean value) {
    state.updateAndGet(current -> new State(value, current.kickMessage()));
    logMaintenanceSet(value);
  }

  public boolean toggle() {
    final State updated =
        state.updateAndGet(current -> new State(!current.enabled(), current.kickMessage()));

    logMaintenanceSet(updated.enabled());

    return updated.enabled();
  }

  /**
   * Refreshes the kick message from configuration while preserving the current runtime {@code
   * enabled} state.
   *
   * <p>The {@code enabled} field in config is only applied at startup via the constructor. Runtime
   * toggles by operators persist across reloads.
   */
  public void refresh() {
    final Component newKickMessage =
        configManager.getConfigData().maintenance().kickMessageComponent();

    state.updateAndGet(current -> new State(current.enabled(), newKickMessage));

    log.info("Maintenance configuration refreshed (kick message updated, enabled state preserved)");
  }

  private static void logMaintenanceSet(final boolean value) {
    log.info("Maintenance mode set to: {}", value);
  }

  public record State(boolean enabled, Component kickMessage) {}
}
