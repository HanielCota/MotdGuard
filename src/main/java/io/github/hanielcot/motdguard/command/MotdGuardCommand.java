package io.github.hanielcot.motdguard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.proxy.Player;
import io.github.hanielcot.motdguard.config.ConfigManager;
import io.github.hanielcot.motdguard.service.MaintenanceService;
import io.github.hanielcot.motdguard.service.MotdService;
import io.github.hanielcot.motdguard.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@Slf4j
@RequiredArgsConstructor
@CommandAlias("motdguard|mg")
@CommandPermission("motdguard.admin")
@Description("Manage MOTD and maintenance mode")
public class MotdGuardCommand extends BaseCommand {

    private final ConfigManager configManager;
    private final MaintenanceService maintenanceService;
    private final RateLimitService rateLimitService;
    private final MotdService motdService;

    @Default
    @Description("Show help")
    public void onDefault(final Player player) {
        final var msg = configManager.getConfigData().getMessages();
        player.sendMessage(legacy(msg.getHelpHeader()));
        player.sendMessage(legacy(msg.getHelpReload()));
        player.sendMessage(legacy(msg.getHelpMaintenance()));
        player.sendMessage(legacy(msg.getHelpMaintenanceOn()));
        player.sendMessage(legacy(msg.getHelpMaintenanceOff()));
    }

    @Subcommand("reload")
    @Description("Reload the configuration file")
    public void onReload(final Player player) {
        final var previousMessages = configManager.getConfigData().getMessages();
        try {
            configManager.reload();
            maintenanceService.refresh();
            maintenanceService.syncFromConfig();
            rateLimitService.refresh();
            motdService.refresh();
            final var msg = configManager.getConfigData().getMessages();
            player.sendMessage(legacy(msg.getReloadSuccess()));
        } catch (final Exception e) {
            log.error("Failed to reload configuration", e);
            player.sendMessage(legacy(previousMessages.getReloadFailure()));
        }
    }

    @Subcommand("maintenance|m")
    @Description("Toggle maintenance mode")
    public void onMaintenanceToggle(final Player player) {
        final var msg = configManager.getConfigData().getMessages();
        maintenanceService.toggle();

        final String status = maintenanceService.isEnabled() ? "enabled" : "disabled";
        player.sendMessage(legacy(msg.getMaintenanceToggled().replace("{status}", status)));
    }

    @Subcommand("maintenance on|m on")
    @Description("Enable maintenance mode")
    public void onMaintenanceOn(final Player player) {
        final var msg = configManager.getConfigData().getMessages();
        maintenanceService.setEnabled(true);
        player.sendMessage(legacy(msg.getMaintenanceEnabled()));
    }

    @Subcommand("maintenance off|m off")
    @Description("Disable maintenance mode")
    public void onMaintenanceOff(final Player player) {
        final var msg = configManager.getConfigData().getMessages();
        maintenanceService.setEnabled(false);
        player.sendMessage(legacy(msg.getMaintenanceDisabled()));
    }

    private static Component legacy(final String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
