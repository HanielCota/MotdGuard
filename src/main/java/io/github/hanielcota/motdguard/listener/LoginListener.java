package io.github.hanielcota.motdguard.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

/**
 * Event listener for player login events.
 *
 * <p>When maintenance mode is enabled, this listener blocks any player without the {@code
 * motdguard.bypass} permission from logging in, kicking them with the configured maintenance kick
 * message.
 */
@Slf4j
@RequiredArgsConstructor
public final class LoginListener {

  private final MaintenanceManager maintenanceManager;

  @Subscribe
  public void onLogin(final LoginEvent event) {
    if (!maintenanceManager.isEnabled()) return;
    if (event.getPlayer().hasPermission("motdguard.bypass")) return;

    final Component kickMessage = maintenanceManager.getKickMessage();
    event.setResult(ResultedEvent.ComponentResult.denied(kickMessage));
    log.debug("Blocked player {} during maintenance", event.getPlayer().getUsername());
  }
}
