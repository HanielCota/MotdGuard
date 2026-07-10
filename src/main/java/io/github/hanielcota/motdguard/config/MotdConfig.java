package io.github.hanielcota.motdguard.config;

import static io.github.hanielcota.motdguard.config.ConfigValidation.requireText;

import net.kyori.adventure.text.Component;

public record MotdConfig(String line1, String line2) {

    public MotdConfig {
        requireText(line1, "motd.line1");
        requireText(line2, "motd.line2");

        MiniMessageUtil.assertValid(line1, "motd.line1");
        MiniMessageUtil.assertValid(line2, "motd.line2");
    }

    public Component line1Component() {
        return MiniMessageUtil.deserialize(line1);
    }

    public Component line2Component() {
        return MiniMessageUtil.deserialize(line2);
    }
}
