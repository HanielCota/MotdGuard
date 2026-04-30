package io.github.hanielcota.motdguard.config;

import com.fasterxml.jackson.annotation.JsonAlias;

public record ConfigData(
    MotdConfig motd,
    MaintenanceConfig maintenance,
    @JsonAlias("ratelimit") RateLimitConfig rateLimit,
    CooldownConfig cooldown,
    MessagesConfig messages) {

  public ConfigData {
    if (motd == null) {
      throw new NullPointerException("Missing [motd] section");
    }
    if (maintenance == null) {
      throw new NullPointerException("Missing [maintenance] section");
    }
    if (rateLimit == null) {
      throw new NullPointerException("Missing [rate-limit] section");
    }
    if (cooldown == null) {
      throw new NullPointerException("Missing [cooldown] section");
    }
    if (messages == null) {
      throw new NullPointerException("Missing [messages] section");
    }
  }

  public record MotdConfig(String line1, String line2) {

    public MotdConfig {
      requireText(line1, "motd.line1");
      requireText(line2, "motd.line2");
    }
  }

  public record MaintenanceConfig(boolean enabled, String kickMessage) {

    public MaintenanceConfig {
      requireText(kickMessage, "maintenance.kick-message");
    }
  }

  public record RateLimitConfig(boolean enabled, int maxPingsPerMinute, String blockMessage) {

    public RateLimitConfig {
      if (maxPingsPerMinute < 1) {
        throw new IllegalArgumentException("rate-limit.max-pings-per-minute must be at least 1");
      }
      requireText(blockMessage, "rate-limit.block-message");
    }
  }

  public record CooldownConfig(boolean enabled, int durationSeconds) {

    public CooldownConfig {
      if (durationSeconds < 1) {
        throw new IllegalArgumentException("cooldown.duration-seconds must be at least 1");
      }
    }
  }

  public record MessagesConfig(
      String reloadSuccess,
      String reloadFailure,
      String maintenanceEnabled,
      String maintenanceDisabled,
      String maintenanceToggled,
      String maintenanceStatusEnabled,
      String maintenanceStatusDisabled,
      String helpHeader,
      String helpReload,
      String helpMaintenance,
      String helpMaintenanceOn,
      String helpMaintenanceOff,
      String cooldownMessage) {

    public MessagesConfig {
      requireText(reloadSuccess, "messages.reload-success");
      requireText(reloadFailure, "messages.reload-failure");

      requireText(maintenanceEnabled, "messages.maintenance-enabled");
      requireText(maintenanceDisabled, "messages.maintenance-disabled");
      requireText(maintenanceToggled, "messages.maintenance-toggled");

      maintenanceStatusEnabled =
          requireText(
              defaultIfBlank(maintenanceStatusEnabled, "enabled"),
              "messages.maintenance-status-enabled");

      maintenanceStatusDisabled =
          requireText(
              defaultIfBlank(maintenanceStatusDisabled, "disabled"),
              "messages.maintenance-status-disabled");

      requireText(helpHeader, "messages.help-header");
      requireText(helpReload, "messages.help-reload");
      requireText(helpMaintenance, "messages.help-maintenance");
      requireText(helpMaintenanceOn, "messages.help-maintenance-on");
      requireText(helpMaintenanceOff, "messages.help-maintenance-off");

      requireText(cooldownMessage, "messages.cooldown-message");
    }
  }

  private static String requireText(final String value, final String path) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(path + " must not be blank");
    }
    return value;
  }

  private static String defaultIfBlank(final String value, final String fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }
    return value;
  }
}
