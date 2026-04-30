package io.github.hanielcota.motdguard.config;

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
    String helpMaintenanceOff
) {
    public MessagesConfig {
        reloadSuccess = reloadSuccess != null ? reloadSuccess : "&aConfiguration reloaded successfully.";
        reloadFailure = reloadFailure != null ? reloadFailure : "&cFailed to reload configuration. Check console.";
        maintenanceEnabled = maintenanceEnabled != null ? maintenanceEnabled : "&aMaintenance mode enabled.";
        maintenanceDisabled = maintenanceDisabled != null ? maintenanceDisabled : "&aMaintenance mode disabled.";
        maintenanceToggled = maintenanceToggled != null ? maintenanceToggled : "&aMaintenance mode {status}.";
        helpHeader = helpHeader != null ? helpHeader : "&aMotdGuard Commands:";
        helpReload = helpReload != null ? helpReload : "&e/motdguard reload - Reload configuration";
        helpMaintenance = helpMaintenance != null ? helpMaintenance : "&e/motdguard maintenance - Toggle maintenance mode";
        helpMaintenanceOn = helpMaintenanceOn != null ? helpMaintenanceOn : "&e/motdguard maintenance on - Enable maintenance";
        helpMaintenanceOff = helpMaintenanceOff != null ? helpMaintenanceOff : "&e/motdguard maintenance off - Disable maintenance";
    }
}