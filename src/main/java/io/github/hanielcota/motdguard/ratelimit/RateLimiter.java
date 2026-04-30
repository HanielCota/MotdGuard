package io.github.hanielcota.motdguard.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.bucket4j.Bucket;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.BucketFactory;
import io.github.hanielcota.motdguard.util.IpExtractor;
import io.github.hanielcota.motdguard.util.MiniMessageUtil;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class RateLimiter {

  private static final int MAX_ENTRIES = 10_000;

  private final ConfigManager configManager;
  private final Cache<String, Bucket> cache;
  private final AtomicBoolean enabled = new AtomicBoolean(true);
  private final AtomicReference<Component> blockMessage = new AtomicReference<>();

  public RateLimiter(final ConfigManager configManager) {
    this.configManager = configManager;
    this.cache = Caffeine.newBuilder().maximumSize(MAX_ENTRIES).build();
    refresh();
  }

  public boolean isAllowed(final InetSocketAddress address) {
    if (!enabled.get()) return true;

    final String ip = IpExtractor.extract(address);
    if (ip == null) {
      log.warn("Could not determine IP address for ping; allowing");
      return true;
    }

    final var bucket = cache.get(ip, k -> BucketFactory.create(configManager.getConfigData().rateLimit().maxPingsPerMinute()));
    if (bucket.tryConsume(1)) return true;

    log.debug("Rate limit exceeded for IP: {}", ip);
    return false;
  }

  public ServerPing buildBlockedPing(final ServerPing original) {
    return original
        .asBuilder()
        .description(blockMessage.get())
        .version(new ServerPing.Version(0, "???"))
        .nullPlayers()
        .clearFavicon()
        .build();
  }

  public void refresh() {
    enabled.set(configManager.getConfigData().rateLimit().enabled());
    cache.invalidateAll();
    final String raw = configManager.getConfigData().rateLimit().blockMessage();
    blockMessage.set(MiniMessageUtil.deserialize(raw));
    log.info("Rate limit block message refreshed");
  }
}
