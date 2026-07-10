package io.github.hanielcota.motdguard.config;

import static io.github.hanielcota.motdguard.config.ConfigValidation.requireText;

import net.kyori.adventure.text.Component;

public record MaintenanceConfig(boolean enabled, String kickMessage, String motdLine1, String motdLine2) {

    public MaintenanceConfig {
        requireText(kickMessage, "maintenance.kick-message");

        MiniMessageUtil.assertValid(kickMessage, "maintenance.kick-message");

        // The maintenance MOTD is optional so existing configurations keep loading. When present,
        // each line is validated as MiniMessage just like every other user-facing string.
        if (isPresent(motdLine1)) {
            MiniMessageUtil.assertValid(motdLine1, "maintenance.motd-line1");
        }
        if (isPresent(motdLine2)) {
            MiniMessageUtil.assertValid(motdLine2, "maintenance.motd-line2");
        }
    }

    public Component kickMessageComponent() {
        return MiniMessageUtil.deserialize(kickMessage);
    }

    /** Whether a dedicated maintenance MOTD was configured (both lines present). */
    public boolean hasMaintenanceMotd() {
        return isPresent(motdLine1) && isPresent(motdLine2);
    }

    private static boolean isPresent(final String value) {
        return value != null && !value.isBlank();
    }
}
