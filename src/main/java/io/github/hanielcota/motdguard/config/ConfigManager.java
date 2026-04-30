package io.github.hanielcota.motdguard.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;

public final class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
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
            final var newData = mapper.readValue(configPath.toFile(), ConfigData.class);
            validate(newData);
            configData.set(newData);
            log.info("Configuration reloaded successfully.");
        } catch (final IOException e) {
            log.error("Failed to parse config.toml", e);
            throw new IllegalStateException("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    private void validate(final ConfigData data) {
        if (data.motd() == null) {
            throw new IllegalStateException("Invalid config: motd section is required");
        }
        if (data.maintenance() == null) {
            throw new IllegalStateException("Invalid config: maintenance section is required");
        }
        if (data.rateLimit() == null) {
            throw new IllegalStateException("Invalid config: rateLimit section is required");
        }
        if (data.messages() == null) {
            throw new IllegalStateException("Invalid config: messages section is required");
        }
        if (data.rateLimit().maxPingsPerMinute() < 1) {
            throw new IllegalStateException("Invalid config: maxPingsPerMinute must be at least 1");
        }
        log.debug("Config validation passed");
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