package io.github.hanielcota.motdguard.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PluginConstantsTest {

  @Test
  void statusPlaceholderShouldBeDefined() {
    assertEquals("{status}", PluginConstants.STATUS_PLACEHOLDER);
  }
}
