package io.github.hanielcota.motdguard.util;

import io.github.bucket4j.Bucket;
import java.time.Duration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BucketFactory {

  private static final int MINIMUM_MAX_PINGS = 1;

  public static Bucket create(final int maxPingsPerMinute) {
    final int max = Math.max(maxPingsPerMinute, MINIMUM_MAX_PINGS);

    return Bucket.builder()
        .addLimit(limit -> limit.capacity(max).refillGreedy(max, Duration.ofMinutes(1)))
        .build();
  }
}
