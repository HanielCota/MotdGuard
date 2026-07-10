package io.github.hanielcota.motdguard.motd;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.Reloadable;
import io.github.hanielcota.motdguard.config.ConfigManager;
import io.github.hanielcota.motdguard.config.MaintenanceConfig;
import io.github.hanielcota.motdguard.config.MiniMessageUtil;
import io.github.hanielcota.motdguard.config.MotdConfig;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

/**
 * Single place that composes every {@link ServerPing} the proxy shows: the public MOTD, the
 * maintenance MOTD and the rate-limited ("blocked") ping. Keeping presentation here lets {@link
 * io.github.hanielcota.motdguard.ratelimit.RateLimiter} only decide whether to block, without
 * knowing how a blocked ping looks.
 */
@Slf4j
public final class MotdProvider implements Reloadable {

    private static final ServerPing.Version BLOCKED_VERSION = new ServerPing.Version(0, "???");

    private final ConfigManager configManager;
    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>();

    @Inject
    public MotdProvider(final ConfigManager configManager) {
        this.configManager = Objects.requireNonNull(configManager, "configManager");
        refresh();
    }

    /** Builds the public MOTD, preserving version, players and favicon from {@code original}. */
    public ServerPing buildMotd(final ServerPing original) {
        return withDescription(original, resolveNormal(snapshot.get(), original));
    }

    /** Builds the maintenance MOTD, falling back to the normal MOTD when none is configured. */
    public ServerPing buildMaintenanceMotd(final ServerPing original) {
        final Snapshot s = snapshot.get();

        if (!s.hasMaintenanceMotd()) {
            return buildMotd(original);
        }

        return withDescription(original, resolveMaintenance(s, original));
    }

    /** Builds the rate-limited ping: hidden players/version/favicon and the block message. */
    public ServerPing buildBlockedMotd(final ServerPing original, final Component blockMessage) {
        return Objects.requireNonNull(original, "original")
                .asBuilder()
                .description(Objects.requireNonNull(blockMessage, "blockMessage"))
                .version(BLOCKED_VERSION)
                .nullPlayers()
                .clearFavicon()
                .notModCompatible()
                .build();
    }

    public void refresh() {
        final MotdConfig motd = configManager.getConfigData().motd();
        final MaintenanceConfig maintenance = configManager.getConfigData().maintenance();

        final String motdLine1 = motd.line1();
        final String motdLine2 = motd.line2();
        final boolean motdHasPlaceholders = hasPlaceholders(motdLine1) || hasPlaceholders(motdLine2);

        final boolean hasMaintenanceMotd = maintenance.hasMaintenanceMotd();
        final String maintenanceLine1 = hasMaintenanceMotd ? maintenance.motdLine1() : null;
        final String maintenanceLine2 = hasMaintenanceMotd ? maintenance.motdLine2() : null;
        final boolean maintenanceHasPlaceholders =
                hasMaintenanceMotd && (hasPlaceholders(maintenanceLine1) || hasPlaceholders(maintenanceLine2));

        // Lines without placeholders are pre-rendered once and reused per ping; lines with
        // placeholders are rendered per ping so {online}/{max}/{version} stay live.
        snapshot.set(new Snapshot(
                motdLine1,
                motdLine2,
                motdHasPlaceholders,
                motdHasPlaceholders ? null : assemble(motdLine1, motdLine2),
                maintenanceLine1,
                maintenanceLine2,
                maintenanceHasPlaceholders,
                hasMaintenanceMotd && !maintenanceHasPlaceholders ? assemble(maintenanceLine1, maintenanceLine2) : null,
                hasMaintenanceMotd));

        log.info("MOTD refreshed");
    }

    private static ServerPing withDescription(final ServerPing original, final Component description) {
        return Objects.requireNonNull(original, "original").asBuilder().description(description).build();
    }

    private static Component resolveNormal(final Snapshot s, final ServerPing original) {
        if (!s.motdHasPlaceholders()) {
            return s.motdStatic();
        }
        return assemble(applyPlaceholders(s.motdLine1(), original), applyPlaceholders(s.motdLine2(), original));
    }

    private static Component resolveMaintenance(final Snapshot s, final ServerPing original) {
        if (!s.maintenanceHasPlaceholders()) {
            return s.maintenanceStatic();
        }
        return assemble(
                applyPlaceholders(s.maintenanceLine1(), original), applyPlaceholders(s.maintenanceLine2(), original));
    }

    private static Component assemble(final String line1, final String line2) {
        return Component.text()
                .append(MiniMessageUtil.deserialize(line1))
                .append(Component.newline())
                .append(MiniMessageUtil.deserialize(line2))
                .build();
    }

    private static String applyPlaceholders(final String text, final ServerPing original) {
        final int online = original.getPlayers().map(ServerPing.Players::getOnline).orElse(0);
        final int max = original.getPlayers().map(ServerPing.Players::getMax).orElse(0);
        final String version = Objects.toString(original.getVersion().getName(), "");

        return text.replace("{online}", Integer.toString(online))
                .replace("{max}", Integer.toString(max))
                .replace("{version}", version);
    }

    private static boolean hasPlaceholders(final String text) {
        return text != null
                && (text.contains("{online}") || text.contains("{max}") || text.contains("{version}"));
    }

    private record Snapshot(
            String motdLine1,
            String motdLine2,
            boolean motdHasPlaceholders,
            Component motdStatic,
            String maintenanceLine1,
            String maintenanceLine2,
            boolean maintenanceHasPlaceholders,
            Component maintenanceStatic,
            boolean hasMaintenanceMotd) {}
}
