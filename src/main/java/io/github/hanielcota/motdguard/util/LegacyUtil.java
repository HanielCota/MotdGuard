package io.github.hanielcota.motdguard.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@UtilityClass
public class LegacyUtil {

  private static final LegacyComponentSerializer SERIALIZER =
      LegacyComponentSerializer.legacyAmpersand();

  public static Component deserialize(final String text) {
    return SERIALIZER.deserialize(text);
  }
}
