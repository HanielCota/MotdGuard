package io.github.hanielcota.motdguard.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class MiniMessageUtil {

  private static final MiniMessage INSTANCE = MiniMessage.miniMessage();

  private MiniMessageUtil() {}

  public static Component deserialize(final String text) {
    return INSTANCE.deserialize(text);
  }
}
