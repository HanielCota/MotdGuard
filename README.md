<div align="center">
  <img src=".github/assets/logo.png" alt="MotdGuard logo" width="180">

  <h1>MotdGuard</h1>

  <p>
    The definitive plugin to make your <strong>Velocity</strong> proxy cleaner, easier to control,
    and much more protected from the first server ping.
  </p>

  <p>
    Professional MOTD, smart maintenance mode, ping flood rate limiting, and simple configuration.
    Everything in a lightweight, direct plugin built for serious servers.
  </p>

  <p>
    <a href="README.pt-BR.md">
      <img alt="Read in Portuguese" src="https://img.shields.io/badge/Ler%20em-Portugu%C3%AAs-009739?style=for-the-badge">
    </a>
  </p>

  <p>
    <a href="https://github.com/HanielCota/MotdGuard/actions/workflows/build.yml">
      <img alt="Build" src="https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/build.yml?branch=main&style=for-the-badge&label=build">
    </a>
    <a href="https://github.com/HanielCota/MotdGuard/security/code-scanning">
      <img alt="CodeQL" src="https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/codeql.yml?branch=main&style=for-the-badge&label=codeql">
    </a>
    <a href="LICENSE">
      <img alt="License" src="https://img.shields.io/github/license/HanielCota/MotdGuard?style=for-the-badge">
    </a>
    <img alt="Java" src="https://img.shields.io/badge/Java-21-f58220?style=for-the-badge">
    <img alt="Velocity" src="https://img.shields.io/badge/Velocity-3.5%2B-1f6feb?style=for-the-badge">
  </p>
</div>

---

## Overview

**MotdGuard** is not just another MOTD plugin. It is the first control layer of your Velocity proxy.

While many plugins only change two lines in the server list, MotdGuard delivers a complete experience: strong presentation, painless maintenance mode, and real protection against ping spam. It is the kind of tool you install once and let work quietly in the background, keeping your server professional, predictable, and protected.

With MotdGuard, you can customize the MOTD with MiniMessage, enable maintenance without restarting the proxy, allow staff bypass, and limit abusive ping traffic per IP before it becomes a problem.

## Why Use It?

| Reason | Impact |
| --- | --- |
| Premium server presentation | Your server shows a stronger, cleaner identity in the server list. |
| Immediate control | Enable maintenance, reload configuration, and adjust messages without taking the proxy down. |
| Real protection | Per-IP rate limiting helps reduce ping flood and abusive server list queries. |
| Lightweight and focused | Does what it needs to do without becoming a huge, confusing plugin. |
| Production-ready | Automated builds, CodeQL, Dependabot, and monitored dependencies. |

## Highlights

| Feature | Description |
| --- | --- |
| Dynamic MOTD | Create a strong first impression with MiniMessage, colors, and modern text styles. |
| Maintenance mode | Close the server cleanly with a custom message and full command control. |
| Ping rate limiting | Hold back spam and abusive status queries before they create noise on the proxy. |
| Hot reload | Update `config.toml` and apply changes without restarting Velocity. |
| Permission bypass | Staff can still join when needed, even while maintenance is active. |
| Error logs | Failures are written to `plugins/MotdGuard/errors.log` for quick diagnostics. |

## Requirements

| Item | Version |
| --- | --- |
| Java | 21+ |
| Velocity | 3.5.0+ |
| Gradle | Wrapper included in the project |

## Installation

1. Download the latest `.jar` file from [GitHub Releases](https://github.com/HanielCota/MotdGuard/releases).
2. Place the file inside the `plugins/` folder of your Velocity proxy.
3. Restart the proxy to generate the initial configuration.
4. Edit `plugins/MotdGuard/config.toml`.
5. Use `/motdguard reload` to apply changes without restarting.

## Releases

Releases are published from semantic version tags.

```bash
git tag v1.0.0
git push origin v1.0.0
```

The release workflow verifies that the tag version matches the Gradle project version, builds the plugin with Java 21, creates a GitHub Release, generates release notes, and attaches the compiled JAR.

Release artifacts are available at:

```text
https://github.com/HanielCota/MotdGuard/releases
```

## Configuration

The configuration is simple, readable, and direct. You can change the plugin behavior without recompiling anything.

Main file:

```text
plugins/MotdGuard/config.toml
```

Example:

```toml
[motd]
line1 = "<#f58220><bold>MyServer</bold>"
line2 = "<#ffffff>Protected by <#f58220>MotdGuard"

[maintenance]
enabled = false
kick-message = "<red>Server under maintenance. Please come back soon!"

[rate-limit]
enabled = true
max-pings-per-minute = 60
block-message = "Too many requests. Please wait."

[cooldown]
enabled = true
duration-seconds = 60

[messages]
reload-success = "&aConfiguration reloaded successfully."
reload-failure = "&cFailed to reload the configuration. Check the console."
maintenance-enabled = "&aMaintenance mode enabled."
maintenance-disabled = "&aMaintenance mode disabled."
maintenance-toggled = "&aMaintenance mode {status}."
maintenance-status-enabled = "enabled"
maintenance-status-disabled = "disabled"
help-header = "&aMotdGuard commands:"
help-reload = "&e/motdguard reload - Reloads the configuration"
help-maintenance = "&e/motdguard maintenance - Toggles maintenance mode"
help-maintenance-on = "&e/motdguard maintenance on - Enables maintenance"
help-maintenance-off = "&e/motdguard maintenance off - Disables maintenance"
cooldown-message = "&cPlease wait before using another command."
```

## MiniMessage

The MOTD uses [MiniMessage](https://docs.advntr.dev/minimessage/) for modern text formatting. This lets you create a much better-looking server list entry than the old basic color-code style.

| Syntax | Result |
| --- | --- |
| `<green>Online` | Named color |
| `<#f58220>MotdGuard` | Hex color |
| `<bold>Text</bold>` | Bold text |
| `<gradient:#f58220:#ffffff>Server</gradient>` | Gradient |
| `<hover:show_text:'Info'>Hover me</hover>` | Hover text |

## Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/motdguard` | Shows the help menu | `motdguard.admin` |
| `/mg` | Main alias | `motdguard.admin` |
| `/motdguard reload` | Reloads the configuration | `motdguard.admin` |
| `/motdguard maintenance` | Toggles maintenance mode | `motdguard.admin` |
| `/motdguard maintenance on` | Enables maintenance mode | `motdguard.admin` |
| `/motdguard maintenance off` | Disables maintenance mode | `motdguard.admin` |
| `/mg m` | Maintenance alias | `motdguard.admin` |

## Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `motdguard.admin` | Access to administrative commands | `op` |
| `motdguard.bypass` | Allows joining during maintenance mode | `false` |

## Local Build

Use the Gradle Wrapper included in the repository.

```bash
./gradlew build
```

Build without SpotBugs:

```bash
./gradlew build -x spotbugsMain -x spotbugsTest
```

Generate the final JAR:

```bash
./gradlew shadowJar
```

The compiled artifact is generated at:

```text
build/libs/motdguard-1.0.0.jar
```

## Project Structure

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

## Quality and Security

MotdGuard is designed to be reliable in production. The focus is simple: protect the proxy, keep the server presentation professional, and prevent bad configuration or outdated dependencies from becoming a headache.

The project uses GitHub Actions to validate builds, CodeQL for security analysis, and Dependabot to keep dependencies updated. Less improvisation, more predictability.

| Area | Tool |
| --- | --- |
| Build | GitHub Actions |
| Static analysis | CodeQL |
| Dependencies | Dependabot |
| Packaging | Shadow Jar |

## Philosophy

MotdGuard follows a direct idea: a good plugin does not need to be heavy, confusing, or filled with unnecessary features. It needs to solve the problem, protect the server, and keep working without demanding attention.

This project exists to deliver a clean, strong, and reliable layer for anyone who wants a professional Velocity proxy with real control.

## Contributing

Contributions are welcome. To propose changes:

1. Fork the repository.
2. Create a branch for your change.
3. Run the build locally.
4. Open a pull request describing what changed.

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for more details.
