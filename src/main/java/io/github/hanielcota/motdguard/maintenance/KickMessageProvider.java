package io.github.hanielcota.motdguard.maintenance;

import net.kyori.adventure.text.Component;

public interface KickMessageProvider {

    Component get();

    void refresh();
}