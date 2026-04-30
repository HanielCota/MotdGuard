package io.github.hanielcota.motdguard.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Utility class for parsing MiniMessage strings into Adventure {@link Component} objects.
 *
 * <p>MiniMessage is a markup language that supports colors, gradients, and other formatting
 * options. This utility provides a simple static interface for deserializing MiniMessage-formatted
 * strings.
 */
@UtilityClass
public class MiniMessageUtil {

  private static final MiniMessage INSTANCE = MiniMessage.miniMessage();

  public static Component deserialize(final String text) {
    return INSTANCE.deserialize(text);
  }
}
