package io.github.hanielcota.motdguard.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.util.BucketFactory;
import io.github.hanielcota.motdguard.util.IpExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public final class RateLimitCheckerImpl implements RateLimitChecker {

    private static final Logger log = LoggerFactory.getLogger(RateLimitCheckerImpl.class);
    private static final int MAX_ENTRIES = 10_000;

    private final ConfigManager configManager;
    private final Cache<String, Bucket> cache;
    private volatile boolean enabled = true;

    public RateLimitCheckerImpl(final ConfigManager configManager) {
        this.configManager = configManager;
        this.cache = Caffeine.newBuilder()
                .maximumSize(MAX_ENTRIES)
                .build();
        refresh();
    }

    @Override
    public boolean isAllowed(final InetSocketAddress address) {
        if (!enabled) {
            return true;
        }

        final String ip = IpExtractor.extract(address);
        if (ip == null) {
            log.warn("Could not determine IP address for ping; allowing");
            return true;
        }

        final var bucket = cache.get(ip, k -> BucketFactory.create(configManager));
        if (bucket.tryConsume(1)) {
            return true;
        }

        log.debug("Rate limit exceeded for IP: {}", ip);
        return false;
    }

    @Override
    public void refresh() {
        enabled = configManager.getConfigData().rateLimit().enabled();
        cache.invalidateAll();
    }
}