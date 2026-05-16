<div align="center">

<img src=".github/assets/logo.png" alt="MotdGuard logo" width="170">

# MotdGuard

**The first control layer of your Velocity proxy.**
Professional MOTD · smart maintenance mode · ping-flood protection — from the very first server ping.

<br>

[![Build](https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/build.yml?branch=main&style=for-the-badge&labelColor=0d1117&color=f58220&label=Build&logo=githubactions&logoColor=f58220)](https://github.com/HanielCota/MotdGuard/actions/workflows/build.yml)
[![CodeQL](https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/codeql.yml?branch=main&style=for-the-badge&labelColor=0d1117&color=f58220&label=CodeQL&logo=github&logoColor=f58220)](https://github.com/HanielCota/MotdGuard/security/code-scanning)
[![License](https://img.shields.io/github/license/HanielCota/MotdGuard?style=for-the-badge&labelColor=0d1117&color=f58220&label=License)](LICENSE)

[![Java](https://img.shields.io/badge/Java-21-f58220?style=for-the-badge&labelColor=0d1117&logo=openjdk&logoColor=f58220)](https://adoptium.net/)
[![Velocity](https://img.shields.io/badge/Velocity-3.5%2B-f58220?style=for-the-badge&labelColor=0d1117)](https://papermc.io/software/velocity)
[![MiniMessage](https://img.shields.io/badge/MiniMessage-ready-f58220?style=for-the-badge&labelColor=0d1117)](https://docs.advntr.dev/minimessage/)

<br>

**🌐 Language:** **English** · [Português](README.pt-BR.md)

</div>

---

## 🛡️ Overview

**MotdGuard** is not just another MOTD plugin — it is the first control layer of your Velocity proxy.

While many plugins only change two lines in the server list, MotdGuard delivers a complete experience: strong presentation, painless maintenance mode, and real protection against ping spam. It is the kind of tool you install once and let work quietly in the background — keeping your server professional, predictable, and protected.

Customize the MOTD with MiniMessage, enable maintenance without restarting the proxy, allow staff bypass, and limit abusive ping traffic per IP before it becomes a problem.

> [!TIP]
> Install it once, configure it in minutes, and use `/motdguard reload` to apply changes live — no proxy restart required.

---

## ✨ Features

| | Feature | Description |
| :-: | --- | --- |
| 🎨 | **Dynamic MOTD** | Create a strong first impression with MiniMessage — colors, gradients, and modern text styles. |
| 🔧 | **Maintenance mode** | Close the server cleanly with a custom message and full command control. |
| 🚦 | **Ping rate limiting** | Hold back spam and abusive status queries before they create noise on the proxy. |
| ♻️ | **Hot reload** | Update `config.toml` and apply changes without restarting Velocity. |
| 🔑 | **Permission bypass** | Staff can still join even while maintenance is active. |
| ⏱️ | **Command cooldown** | Throttle administrative commands to prevent accidental spam. |
| 📝 | **Error logs** | Failures are written to `plugins/MotdGuard/errors.log` for quick diagnostics. |

---

## 📋 Requirements

| Item | Version |
| --- | --- |
| ☕ **Java** | `21+` |
| 🚀 **Velocity** | `3.5.0+` |
| 🐘 **Gradle** | Wrapper included in the project |

---

## 📦 Installation

1. Download the latest `.jar` from [**GitHub Releases**](https://github.com/HanielCota/MotdGuard/releases).
2. Place the file inside the `plugins/` folder of your Velocity proxy.
3. Restart the proxy to generate the initial configuration.
4. Edit `plugins/MotdGuard/config.toml`.
5. Run `/motdguard reload` to apply changes without restarting.

---

## ⚙️ Configuration

The configuration is simple, readable, and direct — change the plugin behavior without recompiling anything.

**Main file:** `plugins/MotdGuard/config.toml`

```toml
[motd]
line1 = "<gradient:#f58220:#ffd9a8><bold>MyServer</bold></gradient>"
line2 = "<#ffffff>Protected by <#f58220>MotdGuard"

[maintenance]
enabled = false
kick-message = "<red>Server under maintenance. Please come back soon!"

[rate-limit]
enabled = true
max-pings-per-minute = 60
block-message = "<gray>Too many requests. Please wait."

[cooldown]
enabled = true
duration-seconds = 60

[messages]
reload-success = "<green>Configuration reloaded successfully."
reload-failure = "<red>Failed to reload the configuration. Check the console."
maintenance-enabled = "<green>Maintenance mode enabled."
maintenance-disabled = "<green>Maintenance mode disabled."
maintenance-toggled = "<green>Maintenance mode {status}."
maintenance-status-enabled = "enabled"
maintenance-status-disabled = "disabled"
help-header = "<#f58220><bold>MotdGuard commands:"
help-reload = "<yellow>/motdguard reload <gray>- Reloads the configuration"
help-maintenance = "<yellow>/motdguard maintenance <gray>- Toggles maintenance mode"
help-maintenance-on = "<yellow>/motdguard maintenance on <gray>- Enables maintenance"
help-maintenance-off = "<yellow>/motdguard maintenance off <gray>- Disables maintenance"
cooldown-message = "<red>Please wait before using another command."
```

> [!WARNING]
> All five sections — `[motd]`, `[maintenance]`, `[rate-limit]`, `[cooldown]` and `[messages]` — are **required**. A missing section makes the plugin fail to load with a clear `Missing [section]` error.

> [!NOTE]
> Messages use **MiniMessage** syntax (`<green>`, `<#f58220>`, `<bold>`). Legacy `&` color codes are **not** supported and would be shown literally.

---

## 🎨 MiniMessage

The MOTD and messages use [MiniMessage](https://docs.advntr.dev/minimessage/) for modern text formatting — far beyond the old basic color-code style.

| Syntax | Result |
| --- | --- |
| `<green>Online` | Named color |
| `<#f58220>MotdGuard` | Hex color |
| `<bold>Text</bold>` | Bold text |
| `<gradient:#f58220:#ffffff>Server</gradient>` | Gradient |
| `<hover:show_text:'Info'>Hover me</hover>` | Hover text |

---

## ⌨️ Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/motdguard` · `/mg` | Shows the help menu | `motdguard.admin` |
| `/motdguard reload` | Reloads the configuration | `motdguard.admin` |
| `/motdguard maintenance` · `/mg m` | Toggles maintenance mode | `motdguard.admin` |
| `/motdguard maintenance on` · `/mg m on` | Enables maintenance mode | `motdguard.admin` |
| `/motdguard maintenance off` · `/mg m off` | Disables maintenance mode | `motdguard.admin` |

---

## 🔑 Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `motdguard.admin` | Access to administrative commands | `op` |
| `motdguard.bypass` | Allows joining during maintenance mode | `false` |

---

## 🚀 Releases

Releases are published from semantic version tags.

```bash
git tag v1.0.0
git push origin v1.0.0
```

The release workflow verifies that the tag matches the Gradle project version, builds the plugin with Java 21, creates a GitHub Release, generates release notes, and attaches the compiled JAR.

📥 Artifacts: [github.com/HanielCota/MotdGuard/releases](https://github.com/HanielCota/MotdGuard/releases)

---

## 🔨 Build

Use the Gradle Wrapper included in the repository.

```bash
# Full build (tests, formatting check, SpotBugs)
./gradlew build

# Build without SpotBugs
./gradlew build -x spotbugsMain -x spotbugsTest

# Generate the final shaded JAR
./gradlew shadowJar
```

The compiled artifact is generated at:

```text
build/libs/motdguard-1.0.0.jar
```

---

## 📁 Project Structure

```text
src/main/java/io/github/hanielcota/motdguard/
├── MotdGuardPlugin.java
├── command/
│   └── MotdGuardCommand.java
├── config/
│   ├── ConfigData.java
│   ├── ConfigManager.java
│   ├── ConfigValidation.java
│   ├── CooldownConfig.java
│   ├── MaintenanceConfig.java
│   ├── MessagesConfig.java
│   ├── MotdConfig.java
│   └── RateLimitConfig.java
├── constants/
│   └── PluginConstants.java
├── listener/
│   ├── LoginListener.java
│   └── PingListener.java
├── maintenance/
│   └── MaintenanceManager.java
├── motd/
│   └── MotdProvider.java
├── ratelimit/
│   └── RateLimiter.java
└── util/
    ├── BucketFactory.java
    ├── CooldownService.java
    ├── IpExtractor.java
    ├── MiniMessageUtil.java
    └── PluginExceptionHandler.java
```

---

## 🔒 Quality & Security

MotdGuard is designed to be reliable in production: protect the proxy, keep the presentation professional, and prevent bad configuration or outdated dependencies from becoming a headache.

| Area | Tool |
| --- | --- |
| 🏗️ Build | GitHub Actions |
| 🔍 Static analysis | CodeQL · SpotBugs · FindSecBugs |
| 🎯 Formatting | Spotless + Google Java Format |
| 📦 Dependencies | Dependabot |
| 🧪 Tests | JUnit 5 · Mockito |
| 📤 Packaging | Shadow JAR |

---

## 💭 Philosophy

A good plugin does not need to be heavy, confusing, or filled with unnecessary features. It needs to **solve the problem, protect the server, and keep working without demanding attention.**

MotdGuard exists to deliver a clean, strong, and reliable control layer for anyone who wants a professional Velocity proxy.

---

## 🤝 Contributing

Contributions are welcome:

1. Fork the repository.
2. Create a branch for your change.
3. Run the build locally (`./gradlew build`).
4. Open a pull request describing what changed.

---

## 📄 License

Distributed under the **MIT License**. See [LICENSE](LICENSE) for details.

<div align="center">
<br>

**Made with ☕ for serious Velocity servers.**

<sub>If MotdGuard helps your server, consider leaving a ⭐ on the repository.</sub>

</div>
