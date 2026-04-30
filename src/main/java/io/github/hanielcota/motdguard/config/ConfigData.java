package io.github.hanielcota.motdguard.config;

import static java.util.Objects.requireNonNullElse;

public record ConfigData(
    MotdConfig motd,
    MaintenanceConfig maintenance,
    RateLimitConfig rateLimit,
    MessagesConfig messages) {

  public record MotdConfig(String line1, String line2) {
    private static final String DEFAULT_LINE1 = "<#00FF00>MeuServidor";
    private static final String DEFAULT_LINE2 = "<#FFFFFF>Modo Hardcore Ativo";

    public MotdConfig {
      line1 = requireNonNullElse(line1, DEFAULT_LINE1);
      line2 = requireNonNullElse(line2, DEFAULT_LINE2);
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
    private static final String RELOAD_SUCCESS = "&aConfiguration reloaded successfully.";
    private static final String RELOAD_FAILURE = "&cFailed to reload configuration. Check console.";
    private static final String MAINTENANCE_ENABLED = "&aMaintenance mode enabled.";
    private static final String MAINTENANCE_DISABLED = "&aMaintenance mode disabled.";
    private static final String MAINTENANCE_TOGGLED = "&aMaintenance mode {status}.";
    private static final String HELP_HEADER = "&aMotdGuard Commands:";
    private static final String HELP_RELOAD = "&e/motdguard reload - Reload configuration";
    private static final String HELP_MAINTENANCE = "&e/motdguard maintenance - Toggle maintenance mode";
    private static final String HELP_MAINTENANCE_ON = "&e/motdguard maintenance on - Enable maintenance";
    private static final String HELP_MAINTENANCE_OFF = "&e/motdguard maintenance off - Disable maintenance";
    private static final String COOLDOWN_MESSAGE = "&cAguarde antes de usar outro comando.";

    public MessagesConfig {
      reloadSuccess = requireNonNullElse(reloadSuccess, RELOAD_SUCCESS);
      reloadFailure = requireNonNullElse(reloadFailure, RELOAD_FAILURE);
      maintenanceEnabled = requireNonNullElse(maintenanceEnabled, MAINTENANCE_ENABLED);
      maintenanceDisabled = requireNonNullElse(maintenanceDisabled, MAINTENANCE_DISABLED);
      maintenanceToggled = requireNonNullElse(maintenanceToggled, MAINTENANCE_TOGGLED);
      helpHeader = requireNonNullElse(helpHeader, HELP_HEADER);
      helpReload = requireNonNullElse(helpReload, HELP_RELOAD);
      helpMaintenance = requireNonNullElse(helpMaintenance, HELP_MAINTENANCE);
      helpMaintenanceOn = requireNonNullElse(helpMaintenanceOn, HELP_MAINTENANCE_ON);
      helpMaintenanceOff = requireNonNullElse(helpMaintenanceOff, HELP_MAINTENANCE_OFF);
      cooldownMessage = requireNonNullElse(cooldownMessage, COOLDOWN_MESSAGE);
    }
  }

  private static final MotdConfig DEFAULT_MOTD = new MotdConfig(null, null);
  private static final MaintenanceConfig DEFAULT_MAINTENANCE = new MaintenanceConfig(false, null);
  private static final RateLimitConfig DEFAULT_RATELIMIT = new RateLimitConfig(true, 60, null);
  private static final MessagesConfig DEFAULT_MESSAGES =
      new MessagesConfig(null, null, null, null, null, null, null, null, null, null, null);

  public ConfigData {
    motd = requireNonNullElse(motd, DEFAULT_MOTD);
    maintenance = requireNonNullElse(maintenance, DEFAULT_MAINTENANCE);
    rateLimit = requireNonNullElse(rateLimit, DEFAULT_RATELIMIT);
    messages = requireNonNullElse(messages, DEFAULT_MESSAGES);
  }
}