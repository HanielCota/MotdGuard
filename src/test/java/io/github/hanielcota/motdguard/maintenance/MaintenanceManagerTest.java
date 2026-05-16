package io.github.hanielcota.motdguard.maintenance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.hanielcota.motdguard.config.ConfigData;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.CooldownConfig;
import io.github.hanielcota.motdguard.config.MaintenanceConfig;
import io.github.hanielcota.motdguard.config.MessagesConfig;
import io.github.hanielcota.motdguard.config.MotdConfig;
import io.github.hanielcota.motdguard.config.RateLimitConfig;
import org.junit.jupiter.api.Test;

class MaintenanceManagerTest {

  private ConfigManager mockConfigManager(boolean enabled, String kickMessage) {
    final ConfigManager manager = mock(ConfigManager.class);
    final var config =
        new ConfigData(
            new MotdConfig("Line1", "Line2"),
            new MaintenanceConfig(enabled, kickMessage),
            new RateLimitConfig(false, 10, "Block"),
            new CooldownConfig(false, 1),
            new MessagesConfig(
                "a", "b", "c", "d", "e", "enabled", "disabled", "g", "h", "i", "j", "k", "l"));
    when(manager.getConfigData()).thenReturn(config);
    return manager;
  }

  @Test
  void shouldLoadInitialStateFromConfig() {
    final var maintenanceManager = new MaintenanceManager(mockConfigManager(true, "<red>Kick"));

    assertTrue(maintenanceManager.isEnabled());
    assertEquals(
        "Kick",
        net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(maintenanceManager.getKickMessage()));
  }

  @Test
  void shouldDisableMaintenance() {
    final var maintenanceManager = new MaintenanceManager(mockConfigManager(true, "Kick"));

    maintenanceManager.setEnabled(false);

    assertFalse(maintenanceManager.isEnabled());
  }

  @Test
  void shouldToggleMaintenance() {
    final var maintenanceManager = new MaintenanceManager(mockConfigManager(false, "Kick"));

    assertFalse(maintenanceManager.isEnabled());

    final boolean toggled = maintenanceManager.toggle();

    assertTrue(toggled);
    assertTrue(maintenanceManager.isEnabled());
  }

  @Test
  void shouldPreserveEnabledStateOnRefresh() {
    final var maintenanceManager = new MaintenanceManager(mockConfigManager(false, "Old"));
    maintenanceManager.setEnabled(true);

    maintenanceManager.refresh();

    assertTrue(maintenanceManager.isEnabled());
  }
}
