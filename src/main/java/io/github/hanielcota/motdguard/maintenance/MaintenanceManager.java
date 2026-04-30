package io.github.hanielcota.motdguard.maintenance;

import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class MaintenanceManager {

  private final ConfigManager configManager;
  private final AtomicReference<State> state = new AtomicReference<>();

  public MaintenanceManager(final ConfigManager configManager) {
    this.configManager = configManager;
    loadInitialState();
  }

  public State getState() {
    final State snapshot = state.get();

    if (snapshot == null) {
      return new State(false, Component.empty());
    }

    return snapshot;
  }

  public boolean isEnabled() {
    return getState().enabled();
  }

  public Component getKickMessage() {
    return getState().kickMessage();
  }

  public void setEnabled(final boolean value) {
    state.updateAndGet(
        current -> {
          if (current == null) {
            return new State(value, Component.empty());
          }

          return new State(value, current.kickMessage());
        });

    logMaintenanceSet(value);
  }

  public boolean toggle() {
    final State previous =
        state.getAndUpdate(
            current -> {
              if (current == null) {
                return new State(true, Component.empty());
              }

              return new State(!current.enabled(), current.kickMessage());
            });

    boolean newValue = true;
    if (previous != null) {
      newValue = !previous.enabled();
    }

    logMaintenanceSet(newValue);

    return newValue;
  }

  /**
   * Refreshes the kick message from configuration while preserving the current runtime {@code
   * enabled} state.
   *
   * <p>The {@code enabled} field in config is only applied at startup via {@link
   * #loadInitialState()}. Runtime toggles by operators persist across reloads.
   */
  public void refresh() {
    final Component newKickMessage =
        MiniMessageUtil.deserialize(configManager.getConfigData().maintenance().kickMessage());

    state.updateAndGet(
        current -> {
          if (current == null) {
            return new State(false, newKickMessage);
          }

          return new State(current.enabled(), newKickMessage);
        });

    log.info("Maintenance configuration refreshed (kick message updated, enabled state preserved)");
  }

  private void loadInitialState() {
    final var maintenanceConfig = configManager.getConfigData().maintenance();

    state.set(
        new State(
            maintenanceConfig.enabled(),
            MiniMessageUtil.deserialize(maintenanceConfig.kickMessage())));

    log.info("Maintenance initial state loaded from config");
  }

  private static void logMaintenanceSet(final boolean value) {
    log.info("Maintenance mode set to: {}", value);
  }

  public record State(boolean enabled, Component kickMessage) {}
}
