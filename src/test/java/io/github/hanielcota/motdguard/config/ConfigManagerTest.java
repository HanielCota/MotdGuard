package io.github.hanielcota.motdguard.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigManagerTest {

  @TempDir Path dataDir;

  private static String validConfig(final String motdLine1) {
    return String.join(
        "\n",
        "[motd]",
        "line1 = \"" + motdLine1 + "\"",
        "line2 = \"Second\"",
        "",
        "[maintenance]",
        "enabled = false",
        "kick-message = \"<red>Kick\"",
        "",
        "[rate-limit]",
        "enabled = true",
        "max-pings-per-minute = 60",
        "block-message = \"Block\"",
        "",
        "[cooldown]",
        "enabled = true",
        "duration-seconds = 10",
        "",
        "[messages]",
        "reload-success = \"ok\"",
        "reload-failure = \"fail\"",
        "maintenance-enabled = \"on\"",
        "maintenance-disabled = \"off\"",
        "maintenance-toggled = \"{status}\"",
        "maintenance-status-enabled = \"enabled\"",
        "maintenance-status-disabled = \"disabled\"",
        "help-header = \"h\"",
        "help-reload = \"r\"",
        "help-maintenance = \"m\"",
        "help-maintenance-on = \"mo\"",
        "help-maintenance-off = \"mf\"",
        "cooldown-message = \"cd\"");
  }

  @Test
  void shouldThrowWhenNotLoaded() {
    final var manager = new ConfigManager(dataDir);

    assertThrows(IllegalStateException.class, manager::getConfigData);
  }

  @Test
  void shouldCreateDefaultConfigWhenMissing() {
    final var manager = new ConfigManager(dataDir);

    manager.load();

    assertTrue(Files.exists(dataDir.resolve("config.toml")));
    assertEquals("<#00FF00>MeuServidor", manager.getConfigData().motd().line1());
  }

  @Test
  void shouldLoadExistingConfig() throws Exception {
    Files.writeString(dataDir.resolve("config.toml"), validConfig("<green>First"));
    final var manager = new ConfigManager(dataDir);

    manager.load();

    assertEquals("<green>First", manager.getConfigData().motd().line1());
  }

  @Test
  void shouldSwapConfigurationOnReload() throws Exception {
    final Path file = dataDir.resolve("config.toml");
    Files.writeString(file, validConfig("<green>First"));
    final var manager = new ConfigManager(dataDir);
    manager.load();

    Files.writeString(file, validConfig("<blue>Updated"));
    manager.reload();

    assertEquals("<blue>Updated", manager.getConfigData().motd().line1());
  }

  @Test
  void shouldKeepPreviousConfigurationWhenReloadFails() throws Exception {
    final Path file = dataDir.resolve("config.toml");
    Files.writeString(file, validConfig("<green>First"));
    final var manager = new ConfigManager(dataDir);
    manager.load();

    // Missing required sections -> ConfigData rejects the instance -> reload throws.
    Files.writeString(file, "[maintenance]\nenabled = false\nkick-message = \"x\"\n");

    assertThrows(IllegalStateException.class, manager::reload);
    assertEquals("<green>First", manager.getConfigData().motd().line1());
  }

  @Test
  void shouldRejectMalformedTomlOnLoad() throws Exception {
    Files.writeString(dataDir.resolve("config.toml"), "this = = is not valid toml ][");
    final var manager = new ConfigManager(dataDir);

    assertThrows(IllegalStateException.class, manager::load);
  }
}
