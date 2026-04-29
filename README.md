# MotdGuard

Dynamic MOTD plugin for Velocity proxy with maintenance mode and rate limiting.

## Features

- **Dynamic MOTD**: Customize server list appearance with MiniMessage formatting
- **Maintenance Mode**: Block players from joining during maintenance with custom kick message
- **Rate Limiting**: Prevent ping spam attacks with configurable limits per IP
- **Hot Reload**: Reload configuration without restarting the server
- **Permission Bypass**: Players with `motdguard.bypass` can join during maintenance

## Requirements

- Java 21
- Velocity 3.4.0+

## Installation

1. Download the latest release from [GitHub Releases](https://github.com/HanielCot/MotdGuard/releases)
2. Place the `MotdGuard-*.jar` file in your Velocity `plugins` folder
3. Restart your Velocity proxy
4. Configure `config.toml` in the `plugins/MotdGuard` folder

## Configuration

Configuration file: `plugins/MotdGuard/config.toml`

```toml
[motd]
line1 = "<#00FF00>MeuServidor"
line2 = "<#FFFFFF>Modo Hardcore Ativo"

[maintenance]
enabled = false
kick-message = "<red>Servidor em manutenção. Volte em breve!"

[ratelimit]
enabled = true
max-pings-per-minute = 60
block-message = "Muitas requisições. Aguarde."

[messages]
reload-success = "&aConfiguration reloaded successfully."
reload-failure = "&cFailed to reload configuration. Check console."
maintenance-enabled = "&aMaintenance mode enabled."
maintenance-disabled = "&aMaintenance mode disabled."
maintenance-toggled = "&aMaintenance mode {status}."
help-header = "&aMotdGuard Commands:"
help-reload = "&e/motdguard reload - Reload configuration"
help-maintenance = "&e/motdguard maintenance - Toggle maintenance mode"
help-maintenance-on = "&e/motdguard maintenance on - Enable maintenance"
help-maintenance-off = "&e/motdguard maintenance off - Disable maintenance"
```

### MiniMessage Formatting

The plugin uses [MiniMessage](https://docs.advntr.dev/minimessage/) for text formatting. Supported tags include:

- `<green>`, `<red>`, `<blue>`, etc. - Color names
- `<#RRGGBB>` - Hex color codes
- `<bold>`, `<italic>`, `<strikethrough>` - Text styles
- `<click:run_command:/cmd>` - Click actions
- `<hover:show_text:text>` - Hover tooltips

## Commands

| Command | Description | Permission |
|---------|-------------|-------------|
| `/motdguard` or `/mg` | Show help menu | `motdguard.admin` |
| `/motdguard reload` | Reload configuration file | `motdguard.admin` |
| `/motdguard maintenance` or `/mg m` | Toggle maintenance mode | `motdguard.admin` |
| `/motdguard maintenance on` or `/mg m on` | Enable maintenance mode | `motdguard.admin` |
| `/motdguard maintenance off` or `/mg m off` | Disable maintenance mode | `motdguard.admin` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `motdguard.admin` | Access to all MotdGuard commands | `op` |
| `motdguard.bypass` | Bypass maintenance mode | `false` |

## Building

### Requirements

- Java 21
- Gradle 8.x

### Build Commands

```bash
# Build the project
./gradlew build

# Build without running SpotBugs checks
./gradlew build -x spotbugsMain -x spotbugsTest

# Build shadow JAR
./gradlew shadowJar

# Clean build directory
./gradlew clean
```

The compiled JAR will be located at:
- `build/libs/MotdGuard-1.0.0.jar` (with dependencies)

## Project Structure

```
src/main/java/io/github/hanielcot/motdguard/
├── MotdGuardPlugin.java       # Main plugin class
├── command/
│   └── MotdGuardCommand.java   # Command handler
├── config/
│   ├── ConfigData.java         # Configuration data class
│   ├── ConfigManager.java      # Configuration manager
│   ├── MaintenanceConfig.java  # Maintenance settings
│   ├── MessagesConfig.java     # Message templates
│   ├── MotdConfig.java         # MOTD settings
│   └── RateLimitConfig.java   # Rate limiting settings
├── exception/
│   └── PluginExceptionHandler.java  # Exception handler
├── listener/
│   ├── LoginListener.java     # Login event handler
│   └── PingListener.java       # Proxy ping handler
└── service/
    ├── MaintenanceService.java # Maintenance mode logic
    ├── MotdService.java        # MOTD building logic
    └── RateLimitService.java  # Rate limiting logic
```

## Error Logging

The plugin logs uncaught exceptions to `plugins/MotdGuard/errors.log`. This file is automatically created and rotated.

## License

This project is licensed under the MIT License.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request