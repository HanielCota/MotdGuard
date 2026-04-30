package io.github.hanielcota.motdguard.util;

import io.github.bucket4j.Bucket;
import io.github.hanielcota.motdguard.config.ConfigManager;
import java.time.Duration;

public final class BucketFactory {

  private static final int MINIMUM_MAX_PINGS = 1;

  private BucketFactory() {}

  public static Bucket create(final ConfigManager configManager) {
    final int configuredMax = configManager.getConfigData().rateLimit().maxPingsPerMinute();
    final int max = Math.max(configuredMax, MINIMUM_MAX_PINGS);
    return Bucket.builder()
        .addLimit(limit -> limit.capacity(max).refillGreedy(max, Duration.ofMinutes(1)))
        .build();
  }
}
