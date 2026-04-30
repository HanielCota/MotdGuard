package io.github.hanielcota.motdguard.ratelimit;

import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BlockedPingProviderImpl implements BlockedPingProvider {

    private static final Logger log = LoggerFactory.getLogger(BlockedPingProviderImpl.class);

    private final ConfigManager configManager;
    private volatile Component blockMessage;

    public BlockedPingProviderImpl(final ConfigManager configManager) {
        this.configManager = configManager;
        refresh();
    }

    @Override
    public ServerPing getBlockedPing(final ServerPing original) {
        return original
                .asBuilder()
                .description(blockMessage)
                .version(new ServerPing.Version(0, "???"))
                .nullPlayers()
                .clearFavicon()
                .build();
    }

    @Override
    public void refresh() {
        final String raw = configManager.getConfigData().rateLimit().blockMessage();
        blockMessage = MiniMessageUtil.deserialize(raw);
        log.info("Rate limit block message refreshed");
    }
}