package io.github.hanielcota.motdguard.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class LoginListener {

    @NonNull private final MaintenanceManager maintenanceManager;

    @Subscribe
    public void onLogin(final LoginEvent event) {
        final var snapshot = maintenanceManager.getState();

        if (!snapshot.enabled()) {
            return;
        }

        final var player = event.getPlayer();

        if (player.hasPermission("motdguard.bypass")) {
            return;
        }

        event.setResult(ResultedEvent.ComponentResult.denied(snapshot.kickMessage()));

        log.debug("Blocked player {} during maintenance", player.getUsername());
    }
}
