package io.github.hanielcota.motdguard.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import io.github.hanielcota.motdguard.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class LoginListener {

  private final MaintenanceService maintenanceService;

  @Subscribe
  public void onLogin(final LoginEvent event) {
    if (!maintenanceService.isEnabled()) return;
    if (maintenanceService.canBypass(event.getPlayer())) return;

    event.setResult(ResultedEvent.ComponentResult.denied(maintenanceService.getKickMessage()));
    log.debug("Blocked player {} during maintenance", event.getPlayer().getUsername());
  }
}
