package io.github.hanielcot.motdguard.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.bucket4j.Bucket;
import io.github.hanielcot.motdguard.config.ConfigManager;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Slf4j
public final class RateLimitService {

  private static final int MINIMUM_MAX_PINGS = 1;

  private final ConfigManager configManager;
  private final Cache<String, Bucket> cache;
  private final AtomicReference<Component> cachedBlockMessage = new AtomicReference<>();

  public RateLimitService(final ConfigManager configManager) {
    this.configManager = configManager;

    this.cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    refresh();
  }

  public boolean isAllowed(final InetSocketAddress remoteAddress) {
    final var rateLimitConfig = configManager.getConfigData().getRateLimit();
    if (!rateLimitConfig.isEnabled()) {
      return true;
    }

    final String ipAddress = extractIpAddress(remoteAddress);
    if (ipAddress == null) {
      log.warn("Could not determine IP address for ping; allowing");
      return true;
    }

    final Bucket bucket = cache.get(ipAddress, k -> createBucket());
    if (bucket.tryConsume(1)) {
      return true;
    }

    log.debug("Rate limit exceeded for IP: {}", ipAddress);
    return false;
  }

  public ServerPing buildBlockedPing(final ServerPing original) {
    return original
        .asBuilder()
        .description(cachedBlockMessage.get())
        .version(new ServerPing.Version(0, "???"))
        .nullPlayers()
        .clearFavicon()
        .build();
  }

  public void refresh() {
    final String raw = configManager.getConfigData().getRateLimit().getBlockMessage();
    cachedBlockMessage.set(MiniMessage.miniMessage().deserialize(raw));
    cache.invalidateAll();
  }

  private Bucket createBucket() {
    final int configuredMax = configManager.getConfigData().getRateLimit().getMaxPingsPerMinute();
    final int max = Math.max(configuredMax, MINIMUM_MAX_PINGS);
    if (max != configuredMax) {
      log.warn("Invalid max-pings-per-minute ({}), using minimum {}", configuredMax, MINIMUM_MAX_PINGS);
    }
    return Bucket.builder()
        .addLimit(limit -> limit.capacity(max).refillGreedy(max, Duration.ofMinutes(1)))
        .build();
  }

  private static String extractIpAddress(final InetSocketAddress remoteAddress) {
    if (remoteAddress == null) return null;
    final InetAddress address = remoteAddress.getAddress();
    if (address == null) return null;
    return address.getHostAddress();
  }
}
