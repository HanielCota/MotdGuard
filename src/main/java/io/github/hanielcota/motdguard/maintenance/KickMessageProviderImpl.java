package io.github.hanielcota.motdguard.maintenance;

import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public final class KickMessageProviderImpl implements KickMessageProvider {

    private static final Logger log = LoggerFactory.getLogger(KickMessageProviderImpl.class);

    private final ConfigManager configManager;
    private final AtomicReference<Component> message = new AtomicReference<>(Component.empty());

    public KickMessageProviderImpl(final ConfigManager configManager) {
        this.configManager = configManager;
        refresh();
    }

    @Override
    public Component get() {
        return message.get();
    }

    @Override
    public void refresh() {
        final String raw = configManager.getConfigData().maintenance().kickMessage();
        message.set(MiniMessageUtil.deserialize(raw));
        log.info("Kick message refreshed");
    }
}