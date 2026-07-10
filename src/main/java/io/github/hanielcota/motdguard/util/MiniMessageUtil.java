package io.github.hanielcota.motdguard.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Slf4j
@UtilityClass
public class MiniMessageUtil {

  private static final MiniMessage INSTANCE = MiniMessage.miniMessage();

  public static Component deserialize(final String text) {
    if (text == null) {
      log.warn("Received null MiniMessage text; using an empty component");

      return Component.empty();
    }

    try {
      return INSTANCE.deserialize(text);
    } catch (final Exception e) {
      log.warn("Invalid MiniMessage text; using literal fallback: {}", text, e);

      return Component.text(text);
    }
  }

  /**
   * Deserializes {@code text} strictly, throwing if it is not valid MiniMessage.
   *
   * <p>Intended for validating configuration at load time so an invalid value fails the reload with
   * a clear message instead of rendering literally for every player.
   *
   * @param text the MiniMessage text to validate
   * @param path the configuration path, used in the error message
   * @return the deserialized component
   * @throws IllegalArgumentException if {@code text} is null or not valid MiniMessage
   */
  public static Component deserializeStrict(final String text, final String path) {
    if (text == null) {
      throw new IllegalArgumentException(path + " must not be null");
    }

    try {
      return INSTANCE.deserialize(text);
    } catch (final Exception e) {
      throw new IllegalArgumentException(path + " is not valid MiniMessage: " + e.getMessage(), e);
    }
  }
}
