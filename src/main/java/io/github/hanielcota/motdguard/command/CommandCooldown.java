package io.github.hanielcota.motdguard.command;

public interface CommandCooldown {

    boolean isOnCooldown(String playerId);

    void setUsed(String playerId);
}