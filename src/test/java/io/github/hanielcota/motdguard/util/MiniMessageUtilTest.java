package io.github.hanielcota.motdguard.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
  void assertValidShouldAcceptKnownTags() {
    assertDoesNotThrow(
        () ->
            MiniMessageUtil.assertValid(
                "<gradient:#f58220:#ffffff><bold>Hello</bold></gradient> <#fff><hover:show_text:'hi'>x",
                "test.path"));
  }

  @Test
  void assertValidShouldRejectUnknownTag() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MiniMessageUtil.assertValid("<gren>typo", "test.path"));
  }

  @Test
  void assertValidShouldRejectInvalidHexColor() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MiniMessageUtil.assertValid("<#zzzzzz>bad", "test.path"));
  }

  @Test
  void assertValidShouldRejectNullInput() {
    assertThrows(
        IllegalArgumentException.class, () -> MiniMessageUtil.assertValid(null, "test.path"));
  }
}
