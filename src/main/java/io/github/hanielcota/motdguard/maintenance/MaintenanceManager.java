package io.github.hanielcota.motdguard.maintenance;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public interface MaintenanceManager {

    boolean isEnabled();

    void setEnabled(boolean value);

    void toggle();

    boolean canBypass(Player player);

    Component getKickMessage();

    void syncFromConfig();

    void refresh();
}