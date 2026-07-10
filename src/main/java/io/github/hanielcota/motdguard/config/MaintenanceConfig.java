package io.github.hanielcota.motdguard.config;

import static io.github.hanielcota.motdguard.config.ConfigValidation.requireText;

import net.kyori.adventure.text.Component;

public record MaintenanceConfig(boolean enabled, String kickMessage) {

  public MaintenanceConfig {
    requireText(kickMessage, "maintenance.kick-message");

    MiniMessageUtil.assertValid(kickMessage, "maintenance.kick-message");
  }

  public Component kickMessageComponent() {
    return MiniMessageUtil.deserialize(kickMessage);
  }
}
