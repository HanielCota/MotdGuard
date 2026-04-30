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
}
