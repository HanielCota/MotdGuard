package io.github.hanielcota.motdguard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import io.github.hanielcota.motdguard.PluginExceptionHandler;
import io.github.hanielcota.motdguard.Reloadable;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.MessagesConfig;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@CommandAlias("motdguard|mg")
@CommandPermission("motdguard.admin")
@Description("Manage MOTD and maintenance mode")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class MotdGuardCommand extends BaseCommand {

    @NonNull private final ConfigManager configManager;

    @NonNull private final MaintenanceManager maintenanceManager;

    @NonNull private final CooldownService cooldown;

    @NonNull private final List<Reloadable> reloadables;

    @NonNull private final PluginExceptionHandler exceptionHandler;

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

            for (final Reloadable reloadable : reloadables) {
                reloadable.refresh();
            }

            send(issuer, messages().reloadSuccessComponent());
        } catch (final Exception e) {
            // configManager.reload() validates and swaps atomically, so a failure here leaves the
            // previously loaded configuration fully intact and the refreshes above do not run.
            exceptionHandler.caughtException("configuration reload", e);
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
        final String status = enabled ? messages.maintenanceStatusEnabled() : messages.maintenanceStatusDisabled();

        send(issuer, messages.maintenanceToggledComponent(status));
    }

    private MessagesConfig messages() {
        return configManager.getConfigData().messages();
    }

    private void send(final CommandIssuer issuer, final Component message) {
        if (issuer.getIssuer() instanceof CommandSource source) {
            source.sendMessage(message);
            return;
        }

        log.debug("Could not deliver message: issuer is not a CommandSource ({})", String.valueOf(issuer.getIssuer()));
    }

    /** Returns {@code true} if the command must be aborted because the player is on cooldown. */
    private boolean blockedByCooldown(final CommandIssuer issuer) {
        if (!issuer.isPlayer()) {
            return false;
        }

        // tryAcquire atomically checks and marks the cooldown, so two concurrent commands from the
        // same player cannot both observe "not on cooldown" (closes the check-then-act race).
        if (cooldown.tryAcquire(issuer.getUniqueId())) {
            send(issuer, messages().cooldownMessageComponent());
            return true;
        }

        return false;
    }
}
