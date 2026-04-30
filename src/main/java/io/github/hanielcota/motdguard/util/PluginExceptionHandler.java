package io.github.hanielcota.motdguard.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PluginExceptionHandler {

  private static final String PLUGIN_PACKAGE = "io.github.hanielcota.motdguard";
  private static final long MAX_LOG_BYTES = 1_048_576;
  private static final int MAX_CAUSE_DEPTH = 10;
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final Path errorLogPath;

  public PluginExceptionHandler(final Path dataDirectory) {
    this.errorLogPath = dataDirectory.resolve("errors.log");
  }

  public void caughtException(final String context, final Throwable throwable) {
    log.error("Caught exception in {}", context, throwable);

    if (throwable == null) {
      return;
    }

    if (isFromPlugin(throwable, 0)) {
      writeToFile(context, throwable);
    }
  }

  private static boolean isFromPlugin(final Throwable throwable, final int depth) {
    if (throwable == null || depth > MAX_CAUSE_DEPTH) {
      return false;
    }

    if (hasPluginFrame(throwable)) {
      return true;
    }

    for (final Throwable suppressed : throwable.getSuppressed()) {
      if (isFromPlugin(suppressed, depth + 1)) {
        return true;
      }
    }

    return isFromPlugin(throwable.getCause(), depth + 1);
  }

  private static boolean hasPluginFrame(final Throwable throwable) {
    for (final var element : throwable.getStackTrace()) {
      if (element.getClassName().startsWith(PLUGIN_PACKAGE)) {
        return true;
      }
    }

    return false;
  }

  private static String stackTraceToString(final Throwable throwable) {
    final var writer = new StringWriter();

    try (final var pw = new PrintWriter(writer)) {
      throwable.printStackTrace(pw);
    }

    return writer.toString();
  }

  private synchronized void writeToFile(final String context, final Throwable throwable) {
    try {
      ensureFileExists();
      rotateIfNeeded();

      final String entry =
          String.format(
              "[%s] Context: %s%n%s%n%n",
              LocalDateTime.now().format(FORMATTER), context, stackTraceToString(throwable));

      Files.writeString(errorLogPath, entry, StandardOpenOption.APPEND);
    } catch (final IOException e) {
      log.error("Failed to write exception to error log", e);
    }
  }

  private void ensureFileExists() throws IOException {
    if (Files.exists(errorLogPath)) {
      return;
    }

    final Path parent = errorLogPath.getParent();

    if (parent != null) {
      Files.createDirectories(parent);
    }

    try {
      Files.createFile(errorLogPath);
    } catch (final FileAlreadyExistsException e) {
      // Another thread created the file concurrently; safe to ignore
    }
  }

  private void rotateIfNeeded() throws IOException {
    if (Files.size(errorLogPath) < MAX_LOG_BYTES) {
      return;
    }

    final Path rotatedPath = errorLogPath.resolveSibling(errorLogPath.getFileName() + ".1");

    Files.move(errorLogPath, rotatedPath, StandardCopyOption.REPLACE_EXISTING);
    Files.createFile(errorLogPath);
  }
}
