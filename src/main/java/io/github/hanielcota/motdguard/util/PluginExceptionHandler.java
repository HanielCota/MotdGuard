package io.github.hanielcota.motdguard.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PluginExceptionHandler implements UncaughtExceptionHandler {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final Path errorLogPath;
  private final UncaughtExceptionHandler previousHandler;

  public PluginExceptionHandler(final Path dataDirectory, final UncaughtExceptionHandler previousHandler) {
    this.errorLogPath = dataDirectory.resolve("errors.log");
    this.previousHandler = previousHandler;
  }

  @Override
  public void uncaughtException(final Thread thread, final Throwable throwable) {
    log.error("Uncaught exception in thread {}", thread.getName(), throwable);
    writeToFile(thread.getName(), throwable);

    if (previousHandler != null) {
      previousHandler.uncaughtException(thread, throwable);
    }
  }

  public void caughtException(final String context, final Throwable throwable) {
    log.error("Caught exception in {}", context, throwable);
    writeToFile(context, throwable);
  }

  private static String stackTraceToString(final Throwable throwable) {
    final var writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  private synchronized void writeToFile(final String context, final Throwable throwable) {
    try {
      ensureFileExists();
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
    if (Files.exists(errorLogPath)) return;

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
}
