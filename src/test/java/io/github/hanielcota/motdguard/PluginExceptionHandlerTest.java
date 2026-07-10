package io.github.hanielcota.motdguard;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PluginExceptionHandlerTest {

  @TempDir Path dataDir;

  @Test
  void shouldWriteExceptionToErrorLog() throws Exception {
    final var handler = new PluginExceptionHandler(dataDir);

    handler.caughtException("unit test", new RuntimeException("boom"));

    final Path log = dataDir.resolve("errors.log");
    assertTrue(Files.exists(log));
    final String content = Files.readString(log);
    assertTrue(content.contains("unit test"));
    assertTrue(content.contains("boom"));
  }

  @Test
  void shouldNotWriteWhenThrowableIsNull() {
    final var handler = new PluginExceptionHandler(dataDir);

    handler.caughtException("ignored", null);

    assertFalse(Files.exists(dataDir.resolve("errors.log")));
  }

  @Test
  void shouldRotateLogWhenLimitExceeded() throws Exception {
    final Path log = dataDir.resolve("errors.log");
    final String chunk = "x".repeat(65_536);
    Files.createFile(log);
    while (Files.size(log) < 1_048_576) {
      Files.writeString(log, chunk, StandardOpenOption.APPEND);
    }

    final var handler = new PluginExceptionHandler(dataDir);
    handler.caughtException("rotate", new RuntimeException("trigger"));

    assertTrue(Files.exists(dataDir.resolve("errors.log.1")));
    assertTrue(Files.exists(log));
  }
}
