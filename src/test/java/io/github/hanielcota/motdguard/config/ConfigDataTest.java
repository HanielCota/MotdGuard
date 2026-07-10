package io.github.hanielcota.motdguard.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ConfigDataTest {

    private static MotdConfig validMotd() {
        return new MotdConfig("Line1", "Line2");
    }

    private static MaintenanceConfig validMaintenance() {
        return new MaintenanceConfig(false, "Kick");
    }

    private static RateLimitConfig validRateLimit() {
        return new RateLimitConfig(true, 10, "Block");
    }

    private static CooldownConfig validCooldown() {
        return new CooldownConfig(true, 5);
    }

    private static MessagesConfig validMessages() {
        return new MessagesConfig(
                "reload-success",
                "reload-failure",
                "maintenance-enabled",
                "maintenance-disabled",
                "maintenance-toggled",
                "enabled",
                "disabled",
                "help-header",
                "help-reload",
                "help-maintenance",
                "help-maintenance-on",
                "help-maintenance-off",
                "cooldown-message");
    }

    @Test
    void shouldCreateValidConfigData() {
        final var config =
                new ConfigData(validMotd(), validMaintenance(), validRateLimit(), validCooldown(), validMessages());

        assertEquals("Line1", config.motd().line1());
        assertEquals(false, config.maintenance().enabled());
    }

    @Test
    void shouldRejectNullMotd() {
        assertThrows(
                NullPointerException.class,
                () -> new ConfigData(null, validMaintenance(), validRateLimit(), validCooldown(), validMessages()));
    }

    @Test
    void shouldRejectBlankMotdLine1() {
        assertThrows(IllegalArgumentException.class, () -> new MotdConfig("", "Line2"));
    }

    @Test
    void shouldRejectBlankMotdLine2() {
        assertThrows(IllegalArgumentException.class, () -> new MotdConfig("Line1", "   "));
    }

    @Test
    void shouldRejectZeroMaxPings() {
        assertThrows(IllegalArgumentException.class, () -> new RateLimitConfig(true, 0, "Block"));
    }

    @Test
    void shouldRejectZeroCooldownDuration() {
        assertThrows(IllegalArgumentException.class, () -> new CooldownConfig(true, 0));
    }

    @Test
    void shouldApplyDefaultForBlankStatusEnabled() {
        final var messages = new MessagesConfig("a", "b", "c", "d", "e", "", null, "g", "h", "i", "j", "k", "l");

        assertEquals("enabled", messages.maintenanceStatusEnabled());
    }

    @Test
    void shouldApplyDefaultForBlankStatusDisabled() {
        final var messages = new MessagesConfig("a", "b", "c", "d", "e", "f", "", "g", "h", "i", "j", "k", "l");

        assertEquals("disabled", messages.maintenanceStatusDisabled());
    }
}
