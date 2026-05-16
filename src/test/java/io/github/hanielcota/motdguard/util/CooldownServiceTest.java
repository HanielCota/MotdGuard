package io.github.hanielcota.motdguard.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CooldownServiceTest {

  @Test
  void disabledServiceShouldAlwaysAllow() {
    final var service = new CooldownService(false, Duration.ofSeconds(10));
    final UUID playerId = UUID.randomUUID();

    assertFalse(service.isOnCooldown(playerId));
    service.setUsed(playerId);
    assertFalse(service.isOnCooldown(playerId));
  }

  @Test
  void shouldTrackCooldownWhenEnabled() {
    final var service = new CooldownService(true, Duration.ofHours(1));
    final UUID playerId = UUID.randomUUID();

    assertFalse(service.isOnCooldown(playerId));
    service.setUsed(playerId);
    assertTrue(service.isOnCooldown(playerId));
  }

  @Test
  void shouldClearCooldown() {
    final var service = new CooldownService(true, Duration.ofHours(1));
    final UUID playerId = UUID.randomUUID();

    service.setUsed(playerId);
    assertTrue(service.isOnCooldown(playerId));

    service.clearCooldown(playerId);
    assertFalse(service.isOnCooldown(playerId));
  }

  @Test
  void shouldApplyRefreshedConfiguration() {
    final var service = new CooldownService(true, Duration.ofHours(1));
    final UUID playerId = UUID.randomUUID();

    service.setUsed(playerId);
    assertTrue(service.isOnCooldown(playerId));

    service.refresh(false, Duration.ofSeconds(10));

    assertFalse(service.isOnCooldown(playerId));
    service.setUsed(playerId);
    assertFalse(service.isOnCooldown(playerId));
  }

  @Test
  void disabledServiceShouldAcceptNonPositiveDuration() {
    final var zeroDurationService = new CooldownService(false, Duration.ZERO);
    final var negativeDurationService = new CooldownService(false, Duration.ofSeconds(-1));
    final UUID playerId = UUID.randomUUID();

    zeroDurationService.setUsed(playerId);
    negativeDurationService.setUsed(playerId);

    assertFalse(zeroDurationService.isOnCooldown(playerId));
    assertFalse(negativeDurationService.isOnCooldown(playerId));
  }

  @Test
  void shouldRejectZeroDuration() {
    assertThrows(IllegalArgumentException.class, () -> new CooldownService(true, Duration.ZERO));
  }

  @Test
  void shouldRejectNegativeDuration() {
    assertThrows(
        IllegalArgumentException.class, () -> new CooldownService(true, Duration.ofSeconds(-1)));
  }

  @Test
  void shouldRejectNullPlayerId() {
    final var service = new CooldownService(true, Duration.ofSeconds(10));
    assertThrows(NullPointerException.class, () -> service.isOnCooldown(null));
    assertThrows(NullPointerException.class, () -> service.setUsed(null));
    assertThrows(NullPointerException.class, () -> service.clearCooldown(null));
  }
}
