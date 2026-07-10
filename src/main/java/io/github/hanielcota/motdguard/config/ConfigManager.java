package io.github.hanielcota.motdguard.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ConfigManager {

    private static final String FILE_NAME = "config.toml";

    private final Path configPath;
    private final ObjectMapper mapper;
    private final AtomicReference<ConfigData> configData = new AtomicReference<>();

    @Inject
    public ConfigManager(@DataDirectory final Path dataDirectory) {
        Objects.requireNonNull(dataDirectory, "dataDirectory");

        this.configPath = dataDirectory.resolve(FILE_NAME);
        this.mapper = new ObjectMapper(new TomlFactory());
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
    }

    public ConfigData getConfigData() {
        final var data = configData.get();

        if (data == null) {
            throw new IllegalStateException("Configuration has not been loaded yet");
        }

        return data;
    }

    public synchronized void load() {
        if (Files.exists(configPath)) {
            reload();
            return;
        }

        try {
            final var parent = configPath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            copyDefaultConfig();
        } catch (final IOException e) {
            log.error("Failed to create default configuration", e);
            throw new IllegalStateException("Could not create default config", e);
        } catch (final RuntimeException e) {
            log.error("Failed to prepare default configuration", e);
            throw e;
        }

        reload();
    }

    public synchronized void reload() {
        try (final var input = Files.newInputStream(configPath)) {
            final var newData = mapper.readValue(input, ConfigData.class);
            configData.set(newData);

            log.info("Configuration reloaded successfully.");
        } catch (final IOException | RuntimeException e) {
            log.error("Failed to load config.toml", e);
            throw new IllegalStateException("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    private void copyDefaultConfig() throws IOException {
        try (final var resource = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (resource == null) {
                throw new IllegalStateException("Default config.toml not found in resources");
            }

            Files.copy(resource, configPath);
        } catch (final FileAlreadyExistsException e) {
            // Another thread/process created the config between the exists() check and the copy;
            // the existing file wins and reload() will pick it up.
            log.debug("config.toml already exists; skipping default copy");
        }
    }
}
