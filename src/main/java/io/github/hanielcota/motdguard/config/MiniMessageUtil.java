package io.github.hanielcota.motdguard.config;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Slf4j
@UtilityClass
class MiniMessageUtil {

    private static final MiniMessage INSTANCE = MiniMessage.miniMessage();

    /**
     * Known MiniMessage tag names (lowercased). Tags outside this set are treated as mistakes so a
     * typo such as {@code <gren>} fails configuration loading instead of rendering literally for
     * every player. Hex colors ({@code #RGB}, {@code #RRGGBB}, with optional alpha) are validated
     * separately.
     */
    private static final Set<String> KNOWN_TAGS = Set.of(
            // named colors
            "black",
            "dark_blue",
            "dark_green",
            "dark_aqua",
            "dark_red",
            "dark_purple",
            "gold",
            "gray",
            "dark_gray",
            "blue",
            "green",
            "aqua",
            "red",
            "light_purple",
            "yellow",
            "white",
            // color / hex aliases
            "color",
            "colour",
            "c",
            // reset
            "reset",
            "r",
            // decorators
            "bold",
            "b",
            "italic",
            "i",
            "em",
            "underlined",
            "u",
            "strikethrough",
            "st",
            "obfuscated",
            "obf",
            "magic",
            // structure
            "newline",
            "br",
            // decorators (compound)
            "gradient",
            "rainbow",
            "pride",
            "phase",
            // interactive / misc
            "hover",
            "click",
            "insertion",
            "font",
            "key",
            "lang",
            "translatable",
            "tr",
            "translate",
            "selector",
            "score",
            "nbt",
            "data",
            "shadow_color",
            "shadow",
            "pre");

    private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)(!?)([a-zA-Z#][a-zA-Z0-9_#-]*)[^>]*>");

    public static Component deserialize(final String text) {
        if (text == null) {
            log.warn("Received null MiniMessage text; using an empty component");

            return Component.empty();
        }

        try {
            return INSTANCE.deserialize(text);
        } catch (final Exception e) {
            log.warn("Invalid MiniMessage text; using literal fallback: {}", text, e);

            return Component.text(text);
        }
    }

    /**
     * Validates that every tag in {@code text} is a known MiniMessage tag (or a well-formed hex
     * color), throwing if it is not.
     *
     * <p>The default MiniMessage parser is lenient and renders unknown tags literally, so this check
     * is what actually catches mistakes at configuration load time. Intended to fail a reload with a
     * clear message instead of letting a broken MOTD/message reach players.
     *
     * @param text the MiniMessage text to validate
     * @param path the configuration path, used in the error message
     * @throws IllegalArgumentException if {@code text} is null, contains an unknown tag, or uses a
     *     malformed hex color
     */
    public static void assertValid(final String text, final String path) {
        if (text == null) {
            throw new IllegalArgumentException(path + " must not be null");
        }

        final Matcher matcher = TAG_PATTERN.matcher(text);

        while (matcher.find()) {
            final String raw = matcher.group(3);

            if (raw.startsWith("#")) {
                validateHexColor(raw, path, text);
                continue;
            }

            if (!KNOWN_TAGS.contains(raw.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException(path + " uses unknown MiniMessage tag <" + raw + "> in: " + text);
            }
        }
    }

    private static void validateHexColor(final String token, final String path, final String text) {
        final String hex = token.substring(1);
        final int length = hex.length();
        final boolean validLength = length == 3 || length == 4 || length == 6 || length == 8;

        if (!validLength || !hex.matches("[0-9a-fA-F]+")) {
            throw new IllegalArgumentException(path + " uses invalid hex color <" + token + "> in: " + text);
        }
    }
}
