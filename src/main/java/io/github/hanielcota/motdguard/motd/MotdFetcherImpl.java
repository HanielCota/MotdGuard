package io.github.hanielcota.motdguard.motd;

import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MotdFetcherImpl implements MotdFetcher {

    private static final Logger log = LoggerFactory.getLogger(MotdFetcherImpl.class);

    private final ConfigManager configManager;
    private volatile Component cached = Component.empty();

    public MotdFetcherImpl(final ConfigManager configManager) {
        this.configManager = configManager;
        refresh();
    }

    @Override
    public Component fetch() {
        return cached;
    }

    public void refresh() {
        final var motdConfig = configManager.getConfigData().motd();
        final Component line1 = MiniMessageUtil.deserialize(motdConfig.line1());
        final Component line2 = MiniMessageUtil.deserialize(motdConfig.line2());
        cached = Component.text().append(line1).append(Component.newline()).append(line2).build();
        log.info("MOTD refreshed");
    }
}