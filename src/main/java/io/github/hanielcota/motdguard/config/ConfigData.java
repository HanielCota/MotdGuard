package io.github.hanielcota.motdguard.config;

import static java.util.Objects.requireNonNullElse;

public record ConfigData(
    MotdConfig motd,
    MaintenanceConfig maintenance,
    RateLimitConfig rateLimit,
    MessagesConfig messages) {

  public record MotdConfig(String line1, String line2) {
    private static final MotdConfig DEFAULTS = new MotdConfig("<#00FF00>MeuServidor", "<#FFFFFF>Modo Hardcore Ativo");

    public MotdConfig {
      line1 = requireNonNullElse(line1, DEFAULTS.line1);
      line2 = requireNonNullElse(line2, DEFAULTS.line2);
    }
  }

  public record MaintenanceConfig(boolean enabled, String kickMessage) {
    private static final String DEFAULT_KICK_MESSAGE =
        "<red>Servidor em manutenção. Volte em breve!";

    public MaintenanceConfig {
      kickMessage = requireNonNullElse(kickMessage, DEFAULT_KICK_MESSAGE);
    }
  }

  public record RateLimitConfig(boolean enabled, int maxPingsPerMinute, String blockMessage) {
    private static final String DEFAULT_BLOCK_MESSAGE = "Muitas requisições. Aguarde.";

    public RateLimitConfig {
      if (maxPingsPerMinute < 1) {
        maxPingsPerMinute = 60;
      }
      blockMessage = requireNonNullElse(blockMessage, DEFAULT_BLOCK_MESSAGE);
    }
  }

  public record MessagesConfig(
      String reloadSuccess,
      String reloadFailure,
      String maintenanceEnabled,
      String maintenanceDisabled,
      String maintenanceToggled,
      String helpHeader,
      String helpReload,
      String helpMaintenance,
      String helpMaintenanceOn,
      String helpMaintenanceOff,
      String cooldownMessage) {
    private static final MessagesConfig DEFAULTS =
        new MessagesConfig(
            "&aConfiguration reloaded successfully.",
            "&cFailed to reload configuration. Check console.",
            "&aMaintenance mode enabled.",
            "&aMaintenance mode disabled.",
            "&aMaintenance mode {status}.",
            "&aMotdGuard Commands:",
            "&e/motdguard reload - Reload configuration",
            "&e/motdguard maintenance - Toggle maintenance mode",
            "&e/motdguard maintenance on - Enable maintenance",
            "&e/motdguard maintenance off - Disable maintenance",
            "&cAguarde antes de usar outro comando.");

    public MessagesConfig {
      reloadSuccess = requireNonNullElse(reloadSuccess, DEFAULTS.reloadSuccess);
      reloadFailure = requireNonNullElse(reloadFailure, DEFAULTS.reloadFailure);
      maintenanceEnabled = requireNonNullElse(maintenanceEnabled, DEFAULTS.maintenanceEnabled);
      maintenanceDisabled = requireNonNullElse(maintenanceDisabled, DEFAULTS.maintenanceDisabled);
      maintenanceToggled = requireNonNullElse(maintenanceToggled, DEFAULTS.maintenanceToggled);
      helpHeader = requireNonNullElse(helpHeader, DEFAULTS.helpHeader);
      helpReload = requireNonNullElse(helpReload, DEFAULTS.helpReload);
      helpMaintenance = requireNonNullElse(helpMaintenance, DEFAULTS.helpMaintenance);
      helpMaintenanceOn = requireNonNullElse(helpMaintenanceOn, DEFAULTS.helpMaintenanceOn);
      helpMaintenanceOff = requireNonNullElse(helpMaintenanceOff, DEFAULTS.helpMaintenanceOff);
      cooldownMessage = requireNonNullElse(cooldownMessage, DEFAULTS.cooldownMessage);
    }
  }

  private static final ConfigData DEFAULTS = new ConfigData(
          new MotdConfig(null, null),
          new MaintenanceConfig(false, null),
          new RateLimitConfig(true, 60, null),
          new MessagesConfig(null, null, null, null, null, null, null, null, null, null, null));

  public ConfigData {
    motd = requireNonNullElse(motd, DEFAULTS.motd);
    maintenance = requireNonNullElse(maintenance, DEFAULTS.maintenance);
    rateLimit = requireNonNullElse(rateLimit, DEFAULTS.rateLimit);
    messages = requireNonNullElse(messages, DEFAULTS.messages);
  }
}
