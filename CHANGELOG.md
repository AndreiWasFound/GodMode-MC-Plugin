# Changelog

All notable changes to GodMode will be documented in this file.

## [3.0.0] - 2026-09-28

GodMode 3.0 is a complete rewrite of the plugin, focused on modernizing the
codebase while preserving compatibility with a very wide range of Minecraft
server versions.

### Added

- `/god on` and `/god off` for explicitly setting your own God mode state.
- `/god <player>` for toggling God mode for another player.
- `/god <player> on|off` for explicitly setting another player's God mode state.
- `/godmode info` for viewing plugin status and version information.
- `/godmode list` for viewing players currently in God mode.
- `/godmode reload` for reloading the configuration without restarting the server.
- Permission-aware command tab completion.
- Clickable and hoverable command help.
- Optional God mode persistence across server restarts.
- Optional healing when God mode is enabled.
- Optional hunger restoration when God mode is enabled.
- Update notifications with a clickable download link.
- Automatic migration of GodMode 2.x configuration files.
- Automatic backup of the old 2.x configuration before migration.
- Config versioning for future migrations.
- Modern bStats integration while keeping the original GodMode bStats project ID.

### Changed

- Completely rewritten and reorganized internal codebase.
- Replaced the original in-memory `Player` list with UUID-based player tracking.
- Reworked configuration and message handling.
- Reworked update checking to run asynchronously with connection/read timeouts.
- Reworked command handling and permissions.
- GodMode is now built as Java 8-compatible bytecode for broad server support.
- Modernized the Gradle build system and development environment.

### Fixed

- Fixed hunger protection preventing players from eating while in God mode.
  Hunger loss is blocked, while hunger gain is still allowed.
- Fixed unsafe player casting from console command senders.
- Fixed unsafe Bukkit API access from asynchronous update-check callbacks.
- Improved update version comparison and handling of development versions.
- Improved plugin shutdown/reload handling for asynchronous update checks.

### Compatibility

GodMode 3.0 is designed to support Minecraft 1.7.10 through modern Minecraft
versions using the same plugin JAR.

Tested successfully on:

- Minecraft 1.7.10
- Minecraft 1.8.8
- Minecraft 1.12.2
- Minecraft 1.16.5
- Minecraft 1.20.1
- Minecraft 1.21.11
- Minecraft 26.2
- Minecraft 26.3

### Upgrade from 2.x

GodMode 3.0 automatically detects the old 2.x configuration format.

Existing `god-msg` and `ungod-msg` values are migrated to the new configuration
format, including multi-line custom messages.

Before migration, the old configuration is backed up as:

`config-v2-backup.yml`

Existing God mode states from GodMode 2.x were not persistent and therefore
cannot be migrated across a server restart.

---

## [2.0] - 2020-08-02

Legacy GodMode release.

For details about older releases, see the version history on the GodMode
Spigot resource page.