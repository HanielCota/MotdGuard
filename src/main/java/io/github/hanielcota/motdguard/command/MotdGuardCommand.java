package io.github.hanielcota.motdguard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import io.github.hanielcota.motdguard.config.ConfigData.MessagesConfig;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.constants.PluginConstants;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import io.github.hanielcota.motdguard.util.CooldownService;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
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
  public void onDefault(final CommandIssuer issuer) {
    final MessagesConfig messages = configManager.getConfigData().messages();

    if (issuer.getIssuer() instanceof CommandSource source) {
      source.sendMessage(MiniMessageUtil.deserialize(messages.helpHeader()));
      source.sendMessage(MiniMessageUtil.deserialize(messages.helpReload()));
      source.sendMessage(MiniMessageUtil.deserialize(messages.helpMaintenance()));
      source.sendMessage(MiniMessageUtil.deserialize(messages.helpMaintenanceOn()));
      source.sendMessage(MiniMessageUtil.deserialize(messages.helpMaintenanceOff()));
    }
  }

  @Subcommand("reload")
  @Description("Reload configuration")
  public void onReload(final CommandIssuer issuer) {
    if (issuer.isPlayer()) {
      final UUID playerId = issuer.getUniqueId();

      if (cooldown.isOnCooldown(playerId)) {
        final MessagesConfig messages = configManager.getConfigData().messages();

        if (issuer.getIssuer() instanceof CommandSource source) {
          source.sendMessage(MiniMessageUtil.deserialize(messages.cooldownMessage()));
        }

        return;
      }
    }

    try {
      configManager.reload();
      maintenanceManager.refresh();
      rateLimiter.refresh();
      motdProvider.refresh();

      final MessagesConfig messages = configManager.getConfigData().messages();

      if (issuer.getIssuer() instanceof CommandSource source) {
        source.sendMessage(MiniMessageUtil.deserialize(messages.reloadSuccess()));
      }

      if (issuer.isPlayer()) {
        cooldown.setUsed(issuer.getUniqueId());
      }
    } catch (final Exception e) {
      log.error("Failed to reload configuration", e);

      final MessagesConfig messages = configManager.getConfigData().messages();

      if (issuer.getIssuer() instanceof CommandSource source) {
        source.sendMessage(MiniMessageUtil.deserialize(messages.reloadFailure()));
      }
    }
  }

  @Subcommand("maintenance|m on")
  @Description("Enable maintenance mode")
  public void onMaintenanceOn(final CommandIssuer issuer) {
    if (issuer.isPlayer()) {
      final UUID playerId = issuer.getUniqueId();

      if (cooldown.isOnCooldown(playerId)) {
        final MessagesConfig messages = configManager.getConfigData().messages();

        if (issuer.getIssuer() instanceof CommandSource source) {
          source.sendMessage(MiniMessageUtil.deserialize(messages.cooldownMessage()));
        }

        return;
      }
    }

    maintenanceManager.setEnabled(true);

    final MessagesConfig messages = configManager.getConfigData().messages();

    if (issuer.getIssuer() instanceof CommandSource source) {
      source.sendMessage(MiniMessageUtil.deserialize(messages.maintenanceEnabled()));
    }

    if (issuer.isPlayer()) {
      cooldown.setUsed(issuer.getUniqueId());
    }
  }

  @Subcommand("maintenance|m off")
  @Description("Disable maintenance mode")
  public void onMaintenanceOff(final CommandIssuer issuer) {
    if (issuer.isPlayer()) {
      final UUID playerId = issuer.getUniqueId();

      if (cooldown.isOnCooldown(playerId)) {
        final MessagesConfig messages = configManager.getConfigData().messages();

        if (issuer.getIssuer() instanceof CommandSource source) {
          source.sendMessage(MiniMessageUtil.deserialize(messages.cooldownMessage()));
        }

        return;
      }
    }

    maintenanceManager.setEnabled(false);

    final MessagesConfig messages = configManager.getConfigData().messages();

    if (issuer.getIssuer() instanceof CommandSource source) {
      source.sendMessage(MiniMessageUtil.deserialize(messages.maintenanceDisabled()));
    }

    if (issuer.isPlayer()) {
      cooldown.setUsed(issuer.getUniqueId());
    }
  }

  @Subcommand("maintenance|m")
  @Description("Toggle maintenance mode")
  public void onMaintenanceToggle(final CommandIssuer issuer) {
    if (issuer.isPlayer()) {
      final UUID playerId = issuer.getUniqueId();

      if (cooldown.isOnCooldown(playerId)) {
        final MessagesConfig messages = configManager.getConfigData().messages();

        if (issuer.getIssuer() instanceof CommandSource source) {
          source.sendMessage(MiniMessageUtil.deserialize(messages.cooldownMessage()));
        }

        return;
      }
    }

    final boolean enabled = maintenanceManager.toggle();
    final MessagesConfig messages = configManager.getConfigData().messages();

    String status = messages.maintenanceStatusDisabled();
    if (enabled) {
      status = messages.maintenanceStatusEnabled();
    }

    if (issuer.getIssuer() instanceof CommandSource source) {
      source.sendMessage(MiniMessageUtil.deserialize(
              messages.maintenanceToggled().replace(PluginConstants.STATUS_PLACEHOLDER, status)));
    }

    if (issuer.isPlayer()) {
      cooldown.setUsed(issuer.getUniqueId());
    }
  }
}
