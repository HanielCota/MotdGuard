package io.github.hanielcota.motdguard.config;

import static io.github.hanielcota.motdguard.config.ConfigValidation.defaultIfBlank;
import static io.github.hanielcota.motdguard.config.ConfigValidation.requireText;

import io.github.hanielcota.motdguard.constants.PluginConstants;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import net.kyori.adventure.text.Component;

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

    maintenanceStatusEnabled = defaultIfBlank(maintenanceStatusEnabled, "enabled");
    maintenanceStatusDisabled = defaultIfBlank(maintenanceStatusDisabled, "disabled");

    requireText(helpHeader, "messages.help-header");
    requireText(helpReload, "messages.help-reload");
    requireText(helpMaintenance, "messages.help-maintenance");
    requireText(helpMaintenanceOn, "messages.help-maintenance-on");
    requireText(helpMaintenanceOff, "messages.help-maintenance-off");

    requireText(cooldownMessage, "messages.cooldown-message");

    MiniMessageUtil.assertValid(reloadSuccess, "messages.reload-success");
    MiniMessageUtil.assertValid(reloadFailure, "messages.reload-failure");
    MiniMessageUtil.assertValid(maintenanceEnabled, "messages.maintenance-enabled");
    MiniMessageUtil.assertValid(maintenanceDisabled, "messages.maintenance-disabled");
    MiniMessageUtil.assertValid(maintenanceToggled, "messages.maintenance-toggled");
    MiniMessageUtil.assertValid(maintenanceStatusEnabled, "messages.maintenance-status-enabled");
    MiniMessageUtil.assertValid(maintenanceStatusDisabled, "messages.maintenance-status-disabled");
    MiniMessageUtil.assertValid(helpHeader, "messages.help-header");
    MiniMessageUtil.assertValid(helpReload, "messages.help-reload");
    MiniMessageUtil.assertValid(helpMaintenance, "messages.help-maintenance");
    MiniMessageUtil.assertValid(helpMaintenanceOn, "messages.help-maintenance-on");
    MiniMessageUtil.assertValid(helpMaintenanceOff, "messages.help-maintenance-off");
    MiniMessageUtil.assertValid(cooldownMessage, "messages.cooldown-message");
  }

  public Component reloadSuccessComponent() {
    return deserialize(reloadSuccess);
  }

  public Component reloadFailureComponent() {
    return deserialize(reloadFailure);
  }

  public Component maintenanceEnabledComponent() {
    return deserialize(maintenanceEnabled);
  }

  public Component maintenanceDisabledComponent() {
    return deserialize(maintenanceDisabled);
  }

  public Component maintenanceToggledComponent(final String status) {
    return deserialize(maintenanceToggled.replace(PluginConstants.STATUS_PLACEHOLDER, status));
  }

  public Component helpHeaderComponent() {
    return deserialize(helpHeader);
  }

  public Component helpReloadComponent() {
    return deserialize(helpReload);
  }

  public Component helpMaintenanceComponent() {
    return deserialize(helpMaintenance);
  }

  public Component helpMaintenanceOnComponent() {
    return deserialize(helpMaintenanceOn);
  }

  public Component helpMaintenanceOffComponent() {
    return deserialize(helpMaintenanceOff);
  }

  public Component cooldownMessageComponent() {
    return deserialize(cooldownMessage);
  }

  private static Component deserialize(final String text) {
    return MiniMessageUtil.deserialize(text);
  }
}
