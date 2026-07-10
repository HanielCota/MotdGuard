package io.github.hanielcota.motdguard.maintenance;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github.hanielcota.motdguard.Reloadable;
import io.github.hanielcota.motdguard.config.ConfigManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

@Slf4j
public final class MaintenanceManager implements Reloadable {

    private static final String STATE_FILE = "maintenance.state";

    private final ConfigManager configManager;
    private final Path stateFile;
    private final AtomicReference<State> state;

    @Inject
    public MaintenanceManager(final ConfigManager configManager, @DataDirectory final Path dataDirectory) {
        this.configManager = Objects.requireNonNull(configManager, "configManager");
        this.stateFile = Objects.requireNonNull(dataDirectory, "dataDirectory").resolve(STATE_FILE);

        final var maintenanceConfig = configManager.getConfigData().maintenance();
        // A runtime toggle survives reloads (see refresh) and is also persisted so it survives a
        // proxy restart. The config value is only the initial default, used when nothing was
        // persisted yet.
        final boolean initialEnabled = readPersistedEnabled().orElse(maintenanceConfig.enabled());
        this.state = new AtomicReference<>(new State(initialEnabled, maintenanceConfig.kickMessageComponent()));

        log.info("Maintenance initial state loaded (enabled={})", initialEnabled);
    }

    public State getState() {
        return state.get();
    }

    public boolean isEnabled() {
        return state.get().enabled();
    }

    public void setEnabled(final boolean value) {
        state.updateAndGet(current -> new State(value, current.kickMessage()));
        persist(value);
        logMaintenanceSet(value);
    }

    public boolean toggle() {
        final State updated = state.updateAndGet(current -> new State(!current.enabled(), current.kickMessage()));

        persist(updated.enabled());
        logMaintenanceSet(updated.enabled());

        return updated.enabled();
    }

    /**
     * Refreshes the kick message from configuration while preserving the current runtime {@code
     * enabled} state.
     *
     * <p>Runtime toggles by operators persist across reloads and restarts (see {@link
     * #persist(boolean)}), so only the kick message is reapplied here.
     */
    public void refresh() {
        final var newKickMessage = configManager.getConfigData().maintenance().kickMessageComponent();

        state.updateAndGet(current -> new State(current.enabled(), newKickMessage));

        log.info("Maintenance configuration refreshed (kick message updated, enabled state preserved)");
    }

    private Optional<Boolean> readPersistedEnabled() {
        if (!Files.exists(stateFile)) {
            return Optional.empty();
        }

        try {
            final String raw = Files.readString(stateFile, StandardCharsets.UTF_8).trim();

            if (raw.equalsIgnoreCase("true")) {
                return Optional.of(true);
            }
            if (raw.equalsIgnoreCase("false")) {
                return Optional.of(false);
            }

            log.warn("Ignoring invalid maintenance state file content: {}", raw);
            return Optional.empty();
        } catch (final IOException e) {
            log.warn("Could not read maintenance state file; using config default", e);
            return Optional.empty();
        }
    }

    private void persist(final boolean value) {
        try {
            final var parent = stateFile.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    stateFile,
                    Boolean.toString(value),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (final IOException e) {
            log.warn("Could not persist maintenance state", e);
        }
    }

    private static void logMaintenanceSet(final boolean value) {
        log.info("Maintenance mode set to: {}", value);
    }

    public record State(boolean enabled, Component kickMessage) {}
}
