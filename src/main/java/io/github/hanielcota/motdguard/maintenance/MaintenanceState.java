package io.github.hanielcota.motdguard.maintenance;

import com.velocitypowered.api.proxy.Player;

public interface MaintenanceState {

    boolean isEnabled();

    void setEnabled(boolean value);

    void toggle();

    boolean canBypass(Player player);
}