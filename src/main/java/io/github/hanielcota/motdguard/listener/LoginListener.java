package io.github.hanielcota.motdguard.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginListener {

    private static final Logger log = LoggerFactory.getLogger(LoginListener.class);

    private final MaintenanceManager maintenanceManager;

    public LoginListener(final MaintenanceManager maintenanceManager) {
        this.maintenanceManager = maintenanceManager;
    }

    @Subscribe
    public void onLogin(final LoginEvent event) {
        if (!maintenanceManager.isEnabled()) return;
        if (maintenanceManager.canBypass(event.getPlayer())) return;

        final Component kickMessage = maintenanceManager.getKickMessage();
        event.setResult(ResultedEvent.ComponentResult.denied(kickMessage));
        log.debug("Blocked player {} during maintenance", event.getPlayer().getUsername());
    }
}