package io.github.hanielcota.motdguard.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BucketFactoryTest {

  @Test
  void shouldCreateBucketWithValidCapacity() {
    final var bucket = BucketFactory.create(10);
    assertTrue(bucket.tryConsume(10));
  }

  @Test
  void shouldConsumeWithinCapacity() {
    final var bucket = BucketFactory.create(5);

    assertTrue(bucket.tryConsume(5));
    assertFalse(bucket.tryConsume(1));
  }

  @Test
  void shouldThrowForZeroCapacity() {
    assertThrows(IllegalArgumentException.class, () -> BucketFactory.create(0));
  }

  @Test
  void shouldThrowForNegativeCapacity() {
    assertThrows(IllegalArgumentException.class, () -> BucketFactory.create(-5));
  }
}
