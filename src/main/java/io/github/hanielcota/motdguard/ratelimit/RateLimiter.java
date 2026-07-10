package io.github.hanielcota.motdguard.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.bucket4j.Bucket;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.BucketFactory;
import io.github.hanielcota.motdguard.util.IpExtractor;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class RateLimiter {

  private static final int MAX_ENTRIES = 10_000;

  private final ConfigManager configManager;
  private final Cache<String, Bucket> cache;
  private final AtomicReference<State> state = new AtomicReference<>();

  public RateLimiter(final ConfigManager configManager) {
    this.configManager = Objects.requireNonNull(configManager, "configManager");
    this.cache =
        Caffeine.newBuilder()
            .maximumSize(MAX_ENTRIES)
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();

    refresh();
  }

  /**
   * Checks rate limiting for the given address and returns a blocked ping if rate-limited.
   *
   * @return a blocked {@link ServerPing} if the address is rate-limited, or {@code null} if the
   *     ping is allowed.
   */
  public ServerPing tryBlockPing(final InetSocketAddress address, final ServerPing original) {
    final State snapshot = state.get();

    if (snapshot == null || !snapshot.enabled()) {
      return null;
    }

    final Optional<String> ip = IpExtractor.extract(address);

    if (ip.isEmpty()) {
      // Deliberate fail-closed policy: if the client IP cannot be determined we cannot key a
      // bucket, so the ping is blocked rather than allowed to bypass rate limiting.
      log.warn("Could not determine IP address for ping; blocking");
      return buildBlockedPing(original, snapshot);
    }

    final String ipValue = ip.get();
    final var bucket =
        cache.get(ipValue, ignored -> BucketFactory.create(snapshot.maxPingsPerMinute()));

    if (bucket.tryConsume(1)) {
      return null;
    }

    log.debug("Rate limit exceeded for IP: {}", ipValue);

    return buildBlockedPing(original, snapshot);
  }

  public void refresh() {
    final var rateLimitConfig = configManager.getConfigData().rateLimit();

    // Invalidate before publishing the new state so a ping can never pair the new limits with a
    // bucket still using the previous capacity.
    cache.invalidateAll();

    state.set(
        new State(
            rateLimitConfig.enabled(),
            rateLimitConfig.maxPingsPerMinute(),
            rateLimitConfig.blockMessageComponent()));

    log.info("Rate limiter refreshed");
  }

  private static ServerPing buildBlockedPing(final ServerPing original, final State snapshot) {
    return Objects.requireNonNull(original, "original")
        .asBuilder()
        .description(snapshot.blockMessage())
        .version(new ServerPing.Version(0, "???"))
        .nullPlayers()
        .clearFavicon()
        .notModCompatible()
        .build();
  }

  private record State(boolean enabled, int maxPingsPerMinute, Component blockMessage) {}
}
