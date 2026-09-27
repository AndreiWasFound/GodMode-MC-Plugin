package io.github.andreiwasfound.godmode.command;

import io.github.andreiwasfound.godmode.GodMode;
import io.github.andreiwasfound.godmode.manager.GodModeManager;
import io.github.andreiwasfound.godmode.message.MessageService;
import io.github.andreiwasfound.godmode.persistence.GodModePersistence;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class GodCommand implements CommandExecutor, TabCompleter {

    private final GodMode plugin;
    private final GodModeManager godModeManager;
    private final MessageService messageService;
    private final GodModePersistence persistence;

    public GodCommand(
            GodMode plugin,
            GodModeManager godModeManager,
            MessageService messageService,
            GodModePersistence persistence
    ) {
        this.plugin = plugin;
        this.godModeManager = godModeManager;
        this.messageService = messageService;
        this.persistence = persistence;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (args.length == 0) {
            return toggleSelf(sender);
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                return setSelfState(sender, true);
            }

            if (args[0].equalsIgnoreCase("off")) {
                return setSelfState(sender, false);
            }

            return toggleOther(
                    sender,
                    args[0]
            );
        }

        if (args.length == 2) {
            return setOtherState(
                    sender,
                    args[0],
                    args[1]
            );
        }

        sendHelp(sender);
        return true;
    }

    private boolean toggleSelf(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageService.send(
                    sender,
                    "player-only"
            );
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("godmode.use")) {
            messageService.send(
                    player,
                    "no-permission"
            );
            return true;
        }

        boolean enabled =
                godModeManager.toggleGod(player);

        persistence.save(
                godModeManager.getGodPlayers()
        );

        if (enabled) {
            applyEnableEffects(player);

            messageService.send(
                    player,
                    "god-enabled"
            );
        } else {
            messageService.send(
                    player,
                    "god-disabled"
            );
        }

        return true;
    }

    private boolean setSelfState(
            CommandSender sender,
            boolean enable
    ) {
        if (!(sender instanceof Player)) {
            messageService.send(
                    sender,
                    "player-only"
            );
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("godmode.use")) {
            messageService.send(
                    player,
                    "no-permission"
            );
            return true;
        }

        if (enable) {
            if (godModeManager.isGod(player)) {
                messageService.send(
                        player,
                        "god-already-enabled"
                );
                return true;
            }

            godModeManager.enableGod(player);

            persistence.save(
                    godModeManager.getGodPlayers()
            );

            applyEnableEffects(player);

            messageService.send(
                    player,
                    "god-enabled"
            );

            return true;
        }

        if (!godModeManager.isGod(player)) {
            messageService.send(
                    player,
                    "god-already-disabled"
            );
            return true;
        }

        godModeManager.disableGod(player);

        persistence.save(
                godModeManager.getGodPlayers()
        );

        messageService.send(
                player,
                "god-disabled"
        );

        return true;
    }

    private boolean toggleOther(
            CommandSender sender,
            String playerName
    ) {
        if (!sender.hasPermission("godmode.others")) {
            messageService.send(
                    sender,
                    "no-permission"
            );
            return true;
        }

        Player target =
                Bukkit.getPlayerExact(playerName);

        if (target == null) {
            messageService.send(
                    sender,
                    "player-not-found",
                    "%player%",
                    playerName
            );

            return true;
        }

        boolean enabled =
                godModeManager.toggleGod(target);

        persistence.save(
                godModeManager.getGodPlayers()
        );

        if (enabled) {
            applyEnableEffects(target);

            messageService.send(
                    target,
                    "god-enabled"
            );

            if (!sender.equals(target)) {
                messageService.send(
                        sender,
                        "god-enabled-other",
                        "%player%",
                        target.getName()
                );
            }
        } else {
            messageService.send(
                    target,
                    "god-disabled"
            );

            if (!sender.equals(target)) {
                messageService.send(
                        sender,
                        "god-disabled-other",
                        "%player%",
                        target.getName()
                );
            }
        }

        return true;
    }

    private boolean setOtherState(
            CommandSender sender,
            String playerName,
            String state
    ) {
        if (!sender.hasPermission("godmode.others")) {
            messageService.send(
                    sender,
                    "no-permission"
            );
            return true;
        }

        Player target =
                Bukkit.getPlayerExact(playerName);

        if (target == null) {
            messageService.send(
                    sender,
                    "player-not-found",
                    "%player%",
                    playerName
            );

            return true;
        }

        if (state.equalsIgnoreCase("on")) {
            enableOther(
                    sender,
                    target
            );
            return true;
        }

        if (state.equalsIgnoreCase("off")) {
            disableOther(
                    sender,
                    target
            );
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void enableOther(
            CommandSender sender,
            Player target
    ) {
        if (godModeManager.isGod(target)) {
            messageService.send(
                    sender,
                    "god-already-enabled-other",
                    "%player%",
                    target.getName()
            );

            return;
        }

        godModeManager.enableGod(target);

        persistence.save(
                godModeManager.getGodPlayers()
        );

        applyEnableEffects(target);

        messageService.send(
                target,
                "god-enabled"
        );

        if (!sender.equals(target)) {
            messageService.send(
                    sender,
                    "god-enabled-other",
                    "%player%",
                    target.getName()
            );
        }
    }

    private void disableOther(
            CommandSender sender,
            Player target
    ) {
        if (!godModeManager.isGod(target)) {
            messageService.send(
                    sender,
                    "god-already-disabled-other",
                    "%player%",
                    target.getName()
            );

            return;
        }

        godModeManager.disableGod(target);

        persistence.save(
                godModeManager.getGodPlayers()
        );

        messageService.send(
                target,
                "god-disabled"
        );

        if (!sender.equals(target)) {
            messageService.send(
                    sender,
                    "god-disabled-other",
                    "%player%",
                    target.getName()
            );
        }
    }

    @SuppressWarnings("deprecation")
    private void applyEnableEffects(Player player) {
        if (plugin.getConfig().getBoolean(
                "god-mode.heal-on-enable",
                false
        )) {
            player.setHealth(
                    player.getMaxHealth()
            );
        }

        if (plugin.getConfig().getBoolean(
                "god-mode.feed-on-enable",
                false
        )) {
            player.setFoodLevel(20);
            player.setSaturation(20.0F);
        }
    }

    private void sendHelp(CommandSender sender) {
        boolean canUseSelf =
                sender.hasPermission("godmode.use");

        boolean canUseOthers =
                sender.hasPermission("godmode.others");

        if (!canUseSelf && !canUseOthers) {
            messageService.send(
                    sender,
                    "no-permission"
            );
            return;
        }

        sender.sendMessage("");

        messageService.send(
                sender,
                "god-help-header"
        );

        if (canUseSelf) {
            messageService.sendCommandHelp(
                    sender,
                    "/god",
                    "Toggle your God mode",
                    "/god"
            );

            messageService.sendCommandHelp(
                    sender,
                    "/god on|off",
                    "Set your God mode",
                    "/god "
            );
        }

        if (canUseOthers) {
            messageService.sendCommandHelp(
                    sender,
                    "/god [player]",
                    "Toggle another player",
                    "/god "
            );

            messageService.sendCommandHelp(
                    sender,
                    "/god [player] on|off",
                    "Set player state",
                    "/god "
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
        if (args.length == 1) {
            String input =
                    args[0].toLowerCase(Locale.ROOT);

            List<String> completions =
                    new ArrayList<String>();

            if (sender.hasPermission("godmode.use")) {
                if ("on".startsWith(input)) {
                    completions.add("on");
                }

                if ("off".startsWith(input)) {
                    completions.add("off");
                }
            }

            if (sender.hasPermission("godmode.others")) {
                for (Player player :
                        Bukkit.getOnlinePlayers()) {

                    String name =
                            player.getName();

                    if (name.toLowerCase(Locale.ROOT)
                            .startsWith(input)) {
                        completions.add(name);
                    }
                }

                Collections.sort(
                        completions,
                        String.CASE_INSENSITIVE_ORDER
                );
            }

            return completions;
        }

        if (args.length == 2
                && sender.hasPermission(
                "godmode.others"
        )) {
            String input =
                    args[1].toLowerCase(Locale.ROOT);

            List<String> completions =
                    new ArrayList<String>();

            for (String option :
                    Arrays.asList("on", "off")) {

                if (option.startsWith(input)) {
                    completions.add(option);
                }
            }

            return completions;
        }

        return Collections.emptyList();
    }
}