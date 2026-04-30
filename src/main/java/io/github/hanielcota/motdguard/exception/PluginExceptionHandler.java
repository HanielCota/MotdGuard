package io.github.hanielcota.motdguard.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PluginExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PluginExceptionHandler.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path errorLogPath;
    private final Thread.UncaughtExceptionHandler previousHandler;

    public PluginExceptionHandler(final Path dataDirectory, final Thread.UncaughtExceptionHandler previousHandler) {
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

    private synchronized void writeToFile(final String context, final Throwable throwable) {
        try {
            ensureFileExists();
            final String stackTrace = extractStackTrace(throwable);
            final String entry = String.format(
                    "[%s] Context: %s%n%s%n%n",
                    LocalDateTime.now().format(FORMATTER), context, stackTrace);
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

    private static String extractStackTrace(final Throwable throwable) {
        final StringBuilder builder = new StringBuilder(throwable.toString()).append(System.lineSeparator());
        for (final StackTraceElement element : throwable.getStackTrace()) {
            builder.append("\tat ").append(element).append(System.lineSeparator());
        }
        return builder.toString();
    }
}