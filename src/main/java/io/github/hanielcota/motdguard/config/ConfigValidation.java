package io.github.hanielcota.motdguard.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigValidation {

  public static String requireText(final String value, final String path) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(path + " must not be blank");
    }
    return value;
  }

  public static String defaultIfBlank(final String value, final String fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }
    return value;
  }
}
