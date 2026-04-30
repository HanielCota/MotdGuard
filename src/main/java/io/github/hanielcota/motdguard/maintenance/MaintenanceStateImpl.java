package io.github.hanielcota.motdguard.maintenance;

import com.velocitypowered.api.proxy.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public final class MaintenanceStateImpl implements MaintenanceState {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceStateImpl.class);
    private final AtomicBoolean enabled = new AtomicBoolean();

    @Override
    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public void setEnabled(final boolean value) {
        enabled.set(value);
        log.info("Maintenance mode set to: {}", value);
    }

    @Override
    public void toggle() {
        enabled.set(!enabled.get());
        log.info("Maintenance mode toggled: {}", enabled.get());
    }

    @Override
    public boolean canBypass(final Player player) {
        return player.hasPermission("motdguard.bypass");
    }
}