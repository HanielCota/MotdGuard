package io.github.hanielcota.motdguard.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

class MiniMessageUtilTest {

  @Test
  void shouldDeserializeColoredText() {
    final Component component = MiniMessageUtil.deserialize("<red>Hello");

    assertNotNull(component);
  }

  @Test
  void shouldReturnEmptyForNullInput() {
    final Component component = MiniMessageUtil.deserialize(null);

    assertEquals(Component.empty(), component);
  }

  @Test
  void shouldFallbackToLiteralForInvalidMiniMessage() {
    final Component component = MiniMessageUtil.deserialize("<invalid>test");

    assertNotNull(component);
  }

  @Test
  void strictDeserializeShouldReturnComponentForValidInput() {
    final Component component = MiniMessageUtil.deserializeStrict("<red>Hello", "test.path");

    assertNotNull(component);
  }

  @Test
  void strictDeserializeShouldRejectNullInput() {
    assertThrows(
        IllegalArgumentException.class, () -> MiniMessageUtil.deserializeStrict(null, "test.path"));
  }
}
