package io.github.hanielcota.motdguard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public final class MessagesConfig {

    @JsonProperty("reload-success")
    private final String reloadSuccess = "&aConfiguration reloaded successfully.";

    @JsonProperty("reload-failure")
    private final String reloadFailure = "&cFailed to reload configuration. Check console.";

    @JsonProperty("maintenance-enabled")
    private final String maintenanceEnabled = "&aMaintenance mode enabled.";

    @JsonProperty("maintenance-disabled")
    private final String maintenanceDisabled = "&aMaintenance mode disabled.";

    @JsonProperty("maintenance-toggled")
    private final String maintenanceToggled = "&aMaintenance mode {status}.";

    @JsonProperty("help-header")
    private final String helpHeader = "&aMotdGuard Commands:";

    @JsonProperty("help-reload")
    private final String helpReload = "&e/motdguard reload - Reload configuration";

    @JsonProperty("help-maintenance")
    private final String helpMaintenance = "&e/motdguard maintenance - Toggle maintenance mode";

    @JsonProperty("help-maintenance-on")
    private final String helpMaintenanceOn = "&e/motdguard maintenance on - Enable maintenance";

    @JsonProperty("help-maintenance-off")
    private final String helpMaintenanceOff = "&e/motdguard maintenance off - Disable maintenance";
}
