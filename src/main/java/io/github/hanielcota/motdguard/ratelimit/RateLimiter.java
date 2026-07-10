package io.github.hanielcota.motdguard.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import io.github.bucket4j.Bucket;
import io.github.hanielcota.motdguard.Reloadable;
import io.github.hanielcota.motdguard.config.ConfigManager;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

/**
 * Decides whether a ping from a given address should be rate-limited.
 *
 * <p>This class is intentionally a pure decision maker: it reports allow/block and exposes the
 * current block message, but it does not build the blocked {@code ServerPing}. Presentation lives in
 * {@link io.github.hanielcota.motdguard.motd.MotdProvider}.
 */
@Slf4j
public final class RateLimiter implements Reloadable {

    private static final int MAX_ENTRIES = 10_000;

    private final ConfigManager configManager;
    private final Function<InetSocketAddress, Optional<String>> ipExtractor;
    private final Cache<String, Bucket> cache;
    private final AtomicReference<State> state = new AtomicReference<>();

    @Inject
    public RateLimiter(final ConfigManager configManager) {
        this(configManager, IpExtractor::extract);
    }

    public RateLimiter(
            final ConfigManager configManager, final Function<InetSocketAddress, Optional<String>> ipExtractor) {
        this.configManager = Objects.requireNonNull(configManager, "configManager");
        this.ipExtractor = Objects.requireNonNull(ipExtractor, "ipExtractor");
        this.cache = Caffeine.newBuilder()
                .maximumSize(MAX_ENTRIES)
                .expireAfterAccess(Duration.ofMinutes(10))
                .build();

        refresh();
    }

    /**
     * Reports whether the ping from {@code address} must be blocked.
     *
     * @return {@code true} if the address is rate-limited (fail-closed when the IP cannot be
     *     determined); {@code false} if the ping is allowed.
     */
    public boolean isBlocked(final InetSocketAddress address) {
        // state is published in the constructor (via refresh) before the instance escapes, so it is
        // never null here. requireNonNull turns an impossible null into a loud failure instead of a
        // silent fail-open that would bypass rate limiting.
        final State snapshot = Objects.requireNonNull(state.get(), "rate limiter state not initialized");

        if (!snapshot.enabled()) {
            return false;
        }

        final Optional<String> ip = ipExtractor.apply(address);

        if (ip.isEmpty()) {
            // Deliberate fail-closed policy: if the client IP cannot be determined we cannot key a
            // bucket, so the ping is blocked rather than allowed to bypass rate limiting.
            log.warn("Could not determine IP address for ping; blocking");
            return true;
        }

        final String ipValue = ip.get();
        final var bucket = cache.get(ipValue, ignored -> BucketFactory.create(snapshot.maxPingsPerMinute()));

        if (bucket.tryConsume(1)) {
            return false;
        }

        log.debug("Rate limit exceeded for IP: {}", ipValue);

        return true;
    }

    /** The current block message, to be rendered by the presentation layer. */
    public Component blockMessage() {
        return Objects.requireNonNull(state.get(), "rate limiter state not initialized").blockMessage();
    }

    public void refresh() {
        final var rateLimitConfig = configManager.getConfigData().rateLimit();

        // Invalidate before publishing the new state so a ping can never pair the new limits with a
        // bucket still using the previous capacity.
        cache.invalidateAll();

        state.set(new State(
                rateLimitConfig.enabled(),
                rateLimitConfig.maxPingsPerMinute(),
                rateLimitConfig.blockMessageComponent()));

        log.info("Rate limiter refreshed");
    }

    private record State(boolean enabled, int maxPingsPerMinute, Component blockMessage) {}
}
