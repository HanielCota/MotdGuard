package io.github.hanielcota.motdguard;

/**
 * Marks a component whose runtime state can be refreshed from the current configuration.
 *
 * <p>Implementations read whatever they need from {@link
 * io.github.hanielcota.motdguard.config.ConfigManager} and atomically swap their internal snapshot,
 * so a configuration reload applies without restarting the proxy. The {@code reload} command
 * invokes {@link #refresh()} on every registered {@code Reloadable} after the configuration is
 * reloaded.
 */
public interface Reloadable {

  /** Refreshes this component from the current configuration. */
  void refresh();
}
