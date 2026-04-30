package io.github.hanielcota.motdguard.command;

import static io.github.hanielcota.motdguard.util.LegacyUtil.*;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.velocitypowered.api.proxy.Player;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.ConfigData.MessagesConfig;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import io.github.hanielcota.motdguard.util.CooldownService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CommandAlias("motdguard|mg")
@CommandPermission("motdguard.admin")
@Description("Manage MOTD and maintenance mode")
@Slf4j
@RequiredArgsConstructor
public final class MotdGuardCommand extends BaseCommand {

  private final ConfigManager configManager;
  private final MaintenanceManager maintenanceManager;
  private final RateLimiter rateLimiter;
  private final MotdProvider motdProvider;
  private final CooldownService cooldown;

  @Default
  public void onCommand(final Player player, final String[] args) {
    final UUID playerId = player.getUniqueId();
    final MessagesConfig messages = configManager.getConfigData().messages();

    if (cooldown.isOnCooldown(playerId)) {
      player.sendMessage(deserialize(messages.cooldownMessage()));
      return;
    }

    try {
      final String action = args.length == 0 ? "help" : args[0].toLowerCase();

      switch (action) {
        case "reload" -> {
          try {
            configManager.reload();
            maintenanceManager.refresh();
            rateLimiter.refresh();
            motdProvider.refresh();
            final MessagesConfig reloaded = configManager.getConfigData().messages();
            player.sendMessage(deserialize(reloaded.reloadSuccess()));
          } catch (final Exception e) {
            log.error("Failed to reload configuration", e);
            player.sendMessage(deserialize(messages.reloadFailure()));
          }
        }
        case "maintenance", "m" -> {
          if (args.length >= 2 && args[1].equalsIgnoreCase("on")) {
            maintenanceManager.setEnabled(true);
            player.sendMessage(deserialize(messages.maintenanceEnabled()));
            return;
          }

          if (args.length >= 2 && args[1].equalsIgnoreCase("off")) {
            maintenanceManager.setEnabled(false);
            player.sendMessage(deserialize(messages.maintenanceDisabled()));
            return;
          }
          maintenanceManager.toggle();
          player.sendMessage(
              deserialize(
                  messages
                      .maintenanceToggled()
                      .replace(
                          "{status}", maintenanceManager.isEnabled() ? "enabled" : "disabled")));
        }
        default -> {
          player.sendMessage(deserialize(messages.helpHeader()));
          player.sendMessage(deserialize(messages.helpReload()));
          player.sendMessage(deserialize(messages.helpMaintenance()));
          player.sendMessage(deserialize(messages.helpMaintenanceOn()));
          player.sendMessage(deserialize(messages.helpMaintenanceOff()));
        }
      }
    } finally {
      cooldown.setUsed(playerId);
    }
  }
}
