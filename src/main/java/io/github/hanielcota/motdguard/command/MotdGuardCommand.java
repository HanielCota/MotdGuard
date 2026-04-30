package io.github.hanielcota.motdguard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.proxy.Player;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@CommandAlias("motdguard|mg")
@CommandPermission("motdguard.admin")
@Description("Manage MOTD and maintenance mode")
public final class MotdGuardCommand extends BaseCommand {

  private static final LegacyComponentSerializer SERIALIZER =
      LegacyComponentSerializer.legacyAmpersand();

  private final ConfigManager configManager;
  private final MaintenanceManager maintenanceManager;
  private final RateLimiter rateLimiter;
  private final MotdProvider motdProvider;
  private final CommandCooldown cooldown;

  public MotdGuardCommand(
      final ConfigManager configManager,
      final MaintenanceManager maintenanceManager,
      final RateLimiter rateLimiter,
      final MotdProvider motdProvider) {
    this.configManager = configManager;
    this.maintenanceManager = maintenanceManager;
    this.rateLimiter = rateLimiter;
    this.motdProvider = motdProvider;
    this.cooldown = new CommandCooldownImpl(configManager);
  }

  @Default
  public void onDefault(final Player player) {
    if (isOnCooldown(player)) return;
    sendHelp(player);
    setCooldown(player);
  }

  @Subcommand("reload")
  @Description("Reload the configuration file")
  public void onReload(final Player player) {
    if (isOnCooldown(player)) return;
    final var previousMessages = configManager.getConfigData().messages();
    try {
      configManager.reload();
      maintenanceManager.refresh();
      maintenanceManager.syncFromConfig();
      rateLimiter.refresh();
      motdProvider.refresh();
      final var msg = configManager.getConfigData().messages();
      player.sendMessage(legacy(msg.reloadSuccess()));
    } catch (final Exception e) {
      player.sendMessage(legacy(previousMessages.reloadFailure()));
    }
    setCooldown(player);
  }

  @Subcommand("maintenance|m")
  @Description("Toggle maintenance mode")
  public void onMaintenanceToggle(final Player player) {
    if (isOnCooldown(player)) return;
    maintenanceManager.toggle();
    final var msg = configManager.getConfigData().messages();
    final String status = maintenanceManager.isEnabled() ? "enabled" : "disabled";
    player.sendMessage(legacy(msg.maintenanceToggled().replace("{status}", status)));
    setCooldown(player);
  }

  @Subcommand("maintenance on|m on")
  @Description("Enable maintenance mode")
  public void onMaintenanceOn(final Player player) {
    if (isOnCooldown(player)) return;
    maintenanceManager.setEnabled(true);
    final var msg = configManager.getConfigData().messages();
    player.sendMessage(legacy(msg.maintenanceEnabled()));
    setCooldown(player);
  }

  @Subcommand("maintenance off|m off")
  @Description("Disable maintenance mode")
  public void onMaintenanceOff(final Player player) {
    if (isOnCooldown(player)) return;
    maintenanceManager.setEnabled(false);
    final var msg = configManager.getConfigData().messages();
    player.sendMessage(legacy(msg.maintenanceDisabled()));
    setCooldown(player);
  }

  private void sendHelp(final Player player) {
    final var msg = configManager.getConfigData().messages();
    player.sendMessage(legacy(msg.helpHeader()));
    player.sendMessage(legacy(msg.helpReload()));
    player.sendMessage(legacy(msg.helpMaintenance()));
    player.sendMessage(legacy(msg.helpMaintenanceOn()));
    player.sendMessage(legacy(msg.helpMaintenanceOff()));
  }

  private boolean isOnCooldown(final Player player) {
    if (cooldown.isOnCooldown(player.getUniqueId().toString())) {
      player.sendMessage(legacy("&cAguarde antes de usar outro comando."));
      return true;
    }
    return false;
  }

  private void setCooldown(final Player player) {
    cooldown.setUsed(player.getUniqueId().toString());
  }

  private static Component legacy(final String text) {
    return SERIALIZER.deserialize(text);
  }
}
