package io.github.andreiwasfound.godmode.command;

import io.github.andreiwasfound.godmode.GodMode;
import io.github.andreiwasfound.godmode.message.MessageService;
import io.github.andreiwasfound.godmode.update.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class GodModeCommand implements CommandExecutor, TabCompleter {

    private final GodMode plugin;
    private final MessageService messageService;

    public GodModeCommand(
            GodMode plugin,
            MessageService messageService
    ) {
        this.plugin = plugin;
        this.messageService = messageService;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (args.length != 1) {
            sendHelp(sender);
            return true;
        }

        String subCommand =
                args[0].toLowerCase(Locale.ROOT);

        if (subCommand.equals("reload")) {
            handleReload(sender);
        } else if (subCommand.equals("info")) {
            handleInfo(sender);
        } else if (subCommand.equals("list")) {
            handleList(sender);
        } else {
            sendHelp(sender);
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("godmode.reload")) {
            messageService.send(sender, "no-permission");
            return;
        }

        plugin.reloadGodModeConfiguration();

        messageService.send(sender, "config-reloaded");
    }

    private void handleInfo(CommandSender sender) {
        if (!sender.hasPermission("godmode.info")) {
            messageService.send(sender, "no-permission");
            return;
        }

        String installedVersion =
                plugin.getDescription().getVersion();

        boolean persistenceEnabled =
                plugin.getConfig().getBoolean(
                        "persistence.enabled",
                        false
                );

        boolean updateCheckerEnabled =
                plugin.getConfig().getBoolean(
                        "update-checker.enabled",
                        true
                );

        int godPlayers =
                plugin.getGodModeManager()
                        .getGodPlayers()
                        .size();

        UpdateChecker updateChecker =
                plugin.getUpdateChecker();

        String latestVersion;

        if (!updateCheckerEnabled) {
            latestVersion = "Disabled";
        } else if (updateChecker == null
                || updateChecker.getLatestVersion() == null) {
            latestVersion = "Checking...";
        } else {
            latestVersion =
                    updateChecker.getLatestVersion();
        }

        sender.sendMessage("");

        messageService.sendRaw(
                sender,
                "&6&lGodMode &ev" + installedVersion
        );

        messageService.sendRaw(
                sender,
                "&8 • &7Players in God mode: &b"
                        + godPlayers
        );

        messageService.sendRaw(
                sender,
                "&8 • &7Persistence: "
                        + (persistenceEnabled
                        ? "&aEnabled"
                        : "&cDisabled")
        );

        messageService.sendRaw(
                sender,
                "&8 • &7Update checker: "
                        + (updateCheckerEnabled
                        ? "&aEnabled"
                        : "&cDisabled")
        );

        messageService.sendRaw(
                sender,
                "&8 • &7Latest public version: &e"
                        + latestVersion
        );

        sender.sendMessage("");
    }

    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("godmode.list")) {
            messageService.send(sender, "no-permission");
            return;
        }

        Set<UUID> godPlayers =
                plugin.getGodModeManager()
                        .getGodPlayers();

        if (godPlayers.isEmpty()) {
            messageService.send(
                    sender,
                    "godmode-list-empty"
            );
            return;
        }

        List<OfflinePlayer> players =
                new ArrayList<OfflinePlayer>();

        for (UUID uuid : godPlayers) {
            players.add(
                    Bukkit.getOfflinePlayer(uuid)
            );
        }

        Collections.sort(
                players,
                new Comparator<OfflinePlayer>() {
                    @Override
                    public int compare(
                            OfflinePlayer first,
                            OfflinePlayer second
                    ) {
                        String firstName =
                                first.getName();

                        String secondName =
                                second.getName();

                        if (firstName == null
                                && secondName == null) {
                            return 0;
                        }

                        if (firstName == null) {
                            return 1;
                        }

                        if (secondName == null) {
                            return -1;
                        }

                        return firstName
                                .compareToIgnoreCase(
                                        secondName
                                );
                    }
                }
        );

        sender.sendMessage("");

        messageService.send(
                sender,
                "godmode-list-header",
                "%count%",
                String.valueOf(players.size())
        );

        for (OfflinePlayer player : players) {
            String playerName;

            if (player.getName() != null) {
                playerName = player.getName();
            } else {
                playerName =
                        player.getUniqueId().toString();
            }

            if (player.isOnline()) {
                messageService.send(
                        sender,
                        "godmode-list-entry-online",
                        "%player%",
                        playerName
                );
            } else {
                messageService.send(
                        sender,
                        "godmode-list-entry-offline",
                        "%player%",
                        playerName
                );
            }
        }

        sender.sendMessage("");
    }

    private void sendHelp(CommandSender sender) {
        boolean canInfo =
                sender.hasPermission("godmode.info");

        boolean canList =
                sender.hasPermission("godmode.list");

        boolean canReload =
                sender.hasPermission("godmode.reload");

        if (!canInfo && !canList && !canReload) {
            messageService.send(
                    sender,
                    "no-permission"
            );
            return;
        }

        sender.sendMessage("");

        messageService.send(
                sender,
                "godmode-help-header"
        );

        if (canInfo) {
            messageService.sendCommandHelp(
                    sender,
                    "/godmode info",
                    "Plugin information",
                    "/godmode info"
            );
        }

        if (canList) {
            messageService.sendCommandHelp(
                    sender,
                    "/godmode list",
                    "Players in God mode",
                    "/godmode list"
            );
        }

        if (canReload) {
            messageService.sendCommandHelp(
                    sender,
                    "/godmode reload",
                    "Reload configuration",
                    "/godmode reload"
            );
        }

        sender.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        String input =
                args[0].toLowerCase(Locale.ROOT);

        List<String> completions =
                new ArrayList<String>();

        if (sender.hasPermission("godmode.info")
                && "info".startsWith(input)) {
            completions.add("info");
        }

        if (sender.hasPermission("godmode.list")
                && "list".startsWith(input)) {
            completions.add("list");
        }

        if (sender.hasPermission("godmode.reload")
                && "reload".startsWith(input)) {
            completions.add("reload");
        }

        return completions;
    }
}