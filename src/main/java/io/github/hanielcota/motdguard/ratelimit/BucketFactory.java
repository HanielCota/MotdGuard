package io.github.hanielcota.motdguard.ratelimit;

import io.github.bucket4j.Bucket;
import java.time.Duration;
import lombok.experimental.UtilityClass;

@UtilityClass
class BucketFactory {

  public static Bucket create(final int maxPingsPerMinute) {
    if (maxPingsPerMinute < 1) {
      throw new IllegalArgumentException("maxPingsPerMinute must be at least 1");
    }

    return Bucket.builder()
        .addLimit(
            limit ->
                limit
                    .capacity(maxPingsPerMinute)
                    .refillGreedy(maxPingsPerMinute, Duration.ofMinutes(1)))
        .build();
  }
}
