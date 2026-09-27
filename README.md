# GodMode

A lightweight, cross-version God mode plugin for Minecraft servers.

GodMode lets players become invulnerable with a simple command while keeping
the plugin small, configurable, and compatible with a very wide range of
Minecraft versions.

Originally released in 2020, GodMode 3.0 is a complete rewrite of the plugin
with improved commands, persistence, configuration, update checking, and
cross-version compatibility.

## Features

- Toggle God mode with `/god`
- Explicitly enable or disable God mode with `/god on` and `/god off`
- Manage God mode for other players
- Damage immunity while God mode is enabled
- Prevents hunger loss without blocking eating or hunger restoration
- Optional healing when God mode is enabled
- Optional hunger restoration when God mode is enabled
- Optional persistence across server restarts
- Permission-aware tab completion
- Clickable and hoverable command help
- `/godmode info`, `/godmode list`, and `/godmode reload`
- Configurable messages
- Asynchronous update checker
- Clickable update notifications
- bStats metrics
- Automatic GodMode 2.x configuration migration
- Automatic backup of legacy configurations before migration
- One plugin JAR across legacy and modern Minecraft versions

## Compatibility

GodMode 3.0 is designed to support Minecraft **1.7.10 through modern Minecraft
versions** using the same plugin JAR.

Tested successfully on:

| Minecraft version | Status |
| --- | --- |
| 1.7.10 | ✅ Tested |
| 1.8.8 | ✅ Tested |
| 1.12.2 | ✅ Tested |
| 1.16.5 | ✅ Tested |
| 1.20.1 | ✅ Tested |
| 1.21.11 | ✅ Tested |
| 26.2 | ✅ Tested |
| 26.3 | ✅ Tested |

GodMode is compiled as Java 8-compatible bytecode and does not use NMS.

The Java version required to run the server itself still depends on the
Minecraft/server version being used.

> GodMode intentionally does not declare `api-version` in `plugin.yml`.
> This allows the same JAR to remain compatible with legacy Minecraft server
> versions. Modern Paper versions may therefore display a legacy-plugin warning
> during startup.

## Installation

1. Download the latest GodMode JAR.
2. Place it in your server's `plugins` directory.
3. Start or restart the server.
4. Configure GodMode in:
   `plugins/GodMode/config.yml`
5. Use `/god` in game.

No additional plugins are required.

## Commands

### `/god`

| Command | Description |
| --- | --- |
| `/god` | Toggle your own God mode |
| `/god on` | Enable your own God mode |
| `/god off` | Disable your own God mode |
| `/god <player>` | Toggle God mode for another online player |
| `/god <player> on` | Enable God mode for another online player |
| `/god <player> off` | Disable God mode for another online player |

### `/godmode`

| Command | Description |
| --- | --- |
| `/godmode` | Display available GodMode administration commands |
| `/godmode info` | Display plugin status and version information |
| `/godmode list` | List players currently in God mode |
| `/godmode reload` | Reload the GodMode configuration |

Command suggestions are automatically filtered based on the sender's
permissions.

## Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `godmode.use` | Toggle or set your own God mode | OP |
| `godmode.others` | Toggle or set God mode for other players | OP |
| `godmode.reload` | Reload the configuration | OP |
| `godmode.updates` | Receive update notifications | OP |
| `godmode.info` | View GodMode information | OP |
| `godmode.list` | View players currently in God mode | OP |

## Configuration

The default configuration is documented directly inside `config.yml`.

```yaml
# GodMode configuration
#
# Do not change config-version manually.
# It is used internally to migrate older GodMode configurations.

config-version: 1

messages:
  # Messages shown when God mode is enabled or disabled.
  # Legacy Minecraft color codes using '&' are supported.
  god-enabled: "&aYou are now in God mode!"
  god-disabled: "&cYou are no longer in God mode!"

  god-already-enabled: "&eYou are already in God mode."
  god-already-disabled: "&eYou are not in God mode."

  god-enabled-other: "&aGod mode enabled for &e%player%&a."
  god-disabled-other: "&cGod mode disabled for &e%player%&c."

  god-already-enabled-other: "&e%player% is already in God mode."
  god-already-disabled-other: "&e%player% is not in God mode."

  player-not-found: "&cPlayer &e%player% &cis not online."

  no-permission: "&cYou don't have permission to use this command."
  player-only: "&cThis command can only be used by players."

  config-reloaded: "&aGodMode configuration reloaded."

  god-help-header: "&6&lGodMode Usage"
  godmode-help-header: "&6&lGodMode Commands"

  godmode-list-empty: "&eNo players are currently in God mode."
  godmode-list-header: "&6&lPlayers in God mode: &e%count%"
  godmode-list-entry-online: "&8 • &a%player% &7(online)"
  godmode-list-entry-offline: "&8 • &7%player% &8(offline)"

  update-available: "&eA new GodMode version is available: &6%version%"
  update-download: "&a&l[Download]"
  update-download-hover: "&7Click to open the GodMode download page"

update-checker:
  enabled: true
  notify-admins: true

persistence:
  enabled: false

god-mode:
  heal-on-enable: false
  feed-on-enable: false
```

### Messages

GodMode supports traditional Minecraft color codes using `&`.

Examples:

```yaml
god-enabled: "&aYou are now in God mode!"
god-disabled: "&cYou are no longer in God mode!"
```

The `%player%`, `%count%`, and `%version%` placeholders are used where
applicable.

## Persistence

God mode states are stored in memory by default.

To keep God mode enabled for players across server restarts:

```yaml
persistence:
  enabled: true
```

When enabled, player UUIDs are stored in:

```text
plugins/GodMode/data.yml
```

GodMode tracks players using UUIDs rather than player objects or usernames.

## Heal and Feed on Enable

GodMode can optionally restore a player's health and hunger when God mode is
enabled.

```yaml
god-mode:
  heal-on-enable: true
  feed-on-enable: true
```

Both options are disabled by default.

## Updating from GodMode 2.x

GodMode 3.0 includes automatic migration for legacy GodMode 2.x
configurations.

A GodMode 2.x configuration such as:

```yaml
god-msg:
  - "&a&lYou are now in God mode!"

ungod-msg:
  - "&c&lYou are no longer in God mode!"
```

is automatically converted to the GodMode 3.0 configuration format.

Custom legacy messages are preserved, including multi-line messages.

Before changing the configuration, GodMode creates a backup:

```text
plugins/GodMode/config-v2-backup.yml
```

If a backup with that name already exists, GodMode creates another numbered
backup instead of overwriting it.

The new configuration is generated from the current GodMode 3.0 template so
that comments, documentation, and configuration structure remain clean after
the upgrade.

## Update Checker

GodMode can check the official Spigot resource for new releases.

```yaml
update-checker:
  enabled: true
  notify-admins: true
```

The check runs asynchronously and does not block the server thread.

Players with the `godmode.updates` permission can receive an update message
with a clickable download button when a newer public version is available.

The update checker can be disabled completely from the configuration.

## Metrics

GodMode uses bStats to collect anonymous usage statistics.

The project continues using the original GodMode bStats plugin ID so statistics
from legacy and modern GodMode installations remain associated with the same
project.

Server administrators can disable bStats globally through the standard bStats
configuration.

## Building from Source

GodMode uses Gradle.

Clone the repository and run:

```bash
./gradlew clean build
```

The final shaded plugin JAR will be generated in:

```text
build/libs/
```

GodMode is compiled against a legacy Spigot API and targets Java 8 bytecode to
maintain broad compatibility.

### Development servers

The Gradle project also contains local test-server tasks for multiple Minecraft
versions, including:

```text
runServer1710
runServer188
runServer1122
runServer1165
runServer1201
runServer12111
runServer
runServer263
```

These were used to validate the same GodMode JAR across legacy and modern
server versions.

Local server directories and build output are excluded from Git.

## Project History

GodMode was originally created and released in 2020.

Version 3.0 represents a complete rewrite of the project while preserving the
original plugin, resource page, project identity, and compatibility goals.

The goal of the rewrite is not to abandon legacy servers, but to modernize the
codebase while continuing to support server owners running older Minecraft
versions.

## Contributing

Bug reports, pull requests, and improvements are welcome.

When contributing:

- Keep cross-version compatibility in mind.
- Avoid NMS unless absolutely necessary.
- Avoid introducing APIs unavailable on supported legacy versions.
- Keep the plugin lightweight.
- Test changes on both legacy and modern server versions where relevant.

## License

GodMode is open-source software licensed under the MIT License.

See `LICENSE` for the full license text.

## Links

- Spigot resource: `https://www.spigotmc.org/resources/godmode.81882/`
- Source code: `https://github.com/AndreiWasFound/GodMode-MC-Plugin`
- Issues: `https://github.com/AndreiWasFound/GodMode-MC-Plugin/issues`