package io.github.hanielcota.motdguard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.MessagesConfig;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import io.github.hanielcota.motdguard.util.CooldownService;
import java.time.Duration;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@CommandAlias("motdguard|mg")
@CommandPermission("motdguard.admin")
@Description("Manage MOTD and maintenance mode")
@Slf4j
@RequiredArgsConstructor
public final class MotdGuardCommand extends BaseCommand {

  @NonNull private final ConfigManager configManager;
  @NonNull private final MaintenanceManager maintenanceManager;
  @NonNull private final RateLimiter rateLimiter;
  @NonNull private final MotdProvider motdProvider;
  @NonNull private final CooldownService cooldown;

  @Default
  public void onDefault(final CommandIssuer issuer) {
    final MessagesConfig messages = messages();

    send(issuer, messages.helpHeaderComponent());
    send(issuer, messages.helpReloadComponent());
    send(issuer, messages.helpMaintenanceComponent());
    send(issuer, messages.helpMaintenanceOnComponent());
    send(issuer, messages.helpMaintenanceOffComponent());
  }

  @Subcommand("reload")
  @Description("Reload configuration")
  public void onReload(final CommandIssuer issuer) {
    if (blockedByCooldown(issuer)) {
      return;
    }

    try {
      configManager.reload();
      maintenanceManager.refresh();
      rateLimiter.refresh();
      motdProvider.refresh();

      final var cooldownConfig = configManager.getConfigData().cooldown();
      cooldown.refresh(
          cooldownConfig.enabled(), Duration.ofSeconds(cooldownConfig.durationSeconds()));

      send(issuer, messages().reloadSuccessComponent());
    } catch (final Exception e) {
      log.error("Failed to reload configuration", e);
      send(issuer, messages().reloadFailureComponent());
    }
  }

  @Subcommand("maintenance on|m on")
  @Description("Enable maintenance mode")
  public void onMaintenanceOn(final CommandIssuer issuer) {
    if (blockedByCooldown(issuer)) {
      return;
    }

    if (!maintenanceManager.isEnabled()) {
      maintenanceManager.setEnabled(true);
    }

    send(issuer, messages().maintenanceEnabledComponent());
  }

  @Subcommand("maintenance off|m off")
  @Description("Disable maintenance mode")
  public void onMaintenanceOff(final CommandIssuer issuer) {
    if (blockedByCooldown(issuer)) {
      return;
    }

    if (maintenanceManager.isEnabled()) {
      maintenanceManager.setEnabled(false);
    }

    send(issuer, messages().maintenanceDisabledComponent());
  }

  @Subcommand("maintenance|m")
  @Description("Toggle maintenance mode")
  public void onMaintenanceToggle(final CommandIssuer issuer) {
    if (blockedByCooldown(issuer)) {
      return;
    }

    final MessagesConfig messages = messages();
    final boolean enabled = maintenanceManager.toggle();
    final String status =
        enabled ? messages.maintenanceStatusEnabled() : messages.maintenanceStatusDisabled();

    send(issuer, messages.maintenanceToggledComponent(status));
  }

  private MessagesConfig messages() {
    return configManager.getConfigData().messages();
  }

  private void send(final CommandIssuer issuer, final Component message) {
    if (issuer.getIssuer() instanceof CommandSource source) {
      source.sendMessage(message);
    }
  }

  /** Returns {@code true} if the command must be aborted because the player is on cooldown. */
  private boolean blockedByCooldown(final CommandIssuer issuer) {
    if (!issuer.isPlayer()) {
      return false;
    }

    final UUID playerId = issuer.getUniqueId();

    if (cooldown.isOnCooldown(playerId)) {
      send(issuer, messages().cooldownMessageComponent());
      return true;
    }

    cooldown.setUsed(playerId);
    return false;
  }
}
