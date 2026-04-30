package io.github.hanielcota.motdguard.motd;

import net.kyori.adventure.text.Component;

public interface MotdFetcher {

    Component fetch();
}