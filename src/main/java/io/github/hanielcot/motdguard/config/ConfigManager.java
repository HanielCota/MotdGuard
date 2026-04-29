package io.github.hanielcot.motdguard.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ConfigManager {

  private static final String FILE_NAME = "config.toml";

  private final Path configPath;
  private final ObjectMapper mapper;
  private final AtomicReference<ConfigData> configData = new AtomicReference<>();

  public ConfigManager(final Path dataDirectory) {
    this.configPath = dataDirectory.resolve(FILE_NAME);
    this.mapper = new ObjectMapper(new TomlFactory());
    this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  public ConfigData getConfigData() {
    final var data = configData.get();
    if (data == null) {
      throw new IllegalStateException("Configuration has not been loaded yet");
    }
    return data;
  }

  public synchronized void load() {
    if (!Files.exists(configPath)) {
      try {
        final Path parent = configPath.getParent();
        if (parent != null) {
          Files.createDirectories(parent);
        }
        copyDefaultConfig();
      } catch (final IOException e) {
        log.error("Failed to create default configuration", e);
        throw new IllegalStateException("Could not create default config", e);
      }
    }
    reload();
  }

  public synchronized void reload() {
    try {
      configData.set(mapper.readValue(configPath.toFile(), ConfigData.class));
      log.info("Configuration reloaded successfully.");
    } catch (final IOException e) {
      throw new IllegalStateException("Failed to load configuration", e);
    }
  }

  private void copyDefaultConfig() throws IOException {
    try (final InputStream resource = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
      if (resource == null) {
        throw new IllegalStateException("Default config.toml not found in resources");
      }
      Files.copy(resource, configPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (final FileAlreadyExistsException e) {
      // Another thread may have created it concurrently
    }
  }
}
