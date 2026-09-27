package io.github.andreiwasfound.godmode.message;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public final class MessageService {

    private static final String DOWNLOAD_URL =
            "https://www.spigotmc.org/resources/godmode.81882/";

    private FileConfiguration config;

    public MessageService(
            FileConfiguration config
    ) {
        this.config = config;
    }

    public void reload(
            FileConfiguration config
    ) {
        this.config = config;
    }

    public String get(
            String path,
            String... replacements
    ) {
        String fullPath =
                "messages." + path;

        if (config.isList(fullPath)) {
            List<String> messages =
                    config.getStringList(fullPath);

            if (!messages.isEmpty()) {
                return format(
                        messages.get(0),
                        replacements
                );
            }
        }

        String message =
                config.getString(fullPath);

        if (message == null) {
            message =
                    "&cMissing message: " + path;
        }

        return format(
                message,
                replacements
        );
    }

    public void send(
            CommandSender sender,
            String path,
            String... replacements
    ) {
        String fullPath =
                "messages." + path;

        if (config.isList(fullPath)) {
            List<String> messages =
                    config.getStringList(fullPath);

            if (messages.isEmpty()) {
                sender.sendMessage(
                        color(
                                "&cMissing message: "
                                        + path
                        )
                );

                return;
            }

            for (String message : messages) {
                sender.sendMessage(
                        format(
                                message,
                                replacements
                        )
                );
            }

            return;
        }

        sender.sendMessage(
                get(
                        path,
                        replacements
                )
        );
    }

    public void sendRaw(
            CommandSender sender,
            String message
    ) {
        sender.sendMessage(
                color(message)
        );
    }

    public void sendCommandHelp(
            CommandSender sender,
            String command,
            String description,
            String suggestion
    ) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    color(
                            "&8 • &e"
                                    + command
                                    + " &7- "
                                    + description
                    )
            );

            return;
        }

        Player player =
                (Player) sender;

        TextComponent root =
                new TextComponent("");

        appendLegacy(
                root,
                color("&8 • ")
        );

        BaseComponent[] commandComponents =
                TextComponent.fromLegacyText(
                        color(
                                "&e" + command
                        )
                );

        ClickEvent clickEvent =
                new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        suggestion
                );

        HoverEvent hoverEvent =
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(
                                color(
                                        "&7Click to use this command"
                                )
                        )
                );

        for (BaseComponent component
                : commandComponents) {

            component.setClickEvent(
                    clickEvent
            );

            component.setHoverEvent(
                    hoverEvent
            );

            root.addExtra(component);
        }

        appendLegacy(
                root,
                color(
                        " &7- "
                                + description
                )
        );

        player.spigot()
                .sendMessage(root);
    }

    public void sendUpdateAvailable(
            Player player,
            String version
    ) {
        String prefix =
                get(
                        "update-available",
                        "%version%",
                        version
                );

        String downloadText =
                get("update-download");

        String hoverText =
                get("update-download-hover");

        TextComponent root =
                new TextComponent("");

        appendLegacy(
                root,
                prefix + " "
        );

        BaseComponent[] downloadComponents =
                TextComponent.fromLegacyText(
                        downloadText
                );

        ClickEvent clickEvent =
                new ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        DOWNLOAD_URL
                );

        HoverEvent hoverEvent =
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(
                                hoverText
                        )
                );

        for (BaseComponent component
                : downloadComponents) {

            component.setClickEvent(
                    clickEvent
            );

            component.setHoverEvent(
                    hoverEvent
            );

            root.addExtra(component);
        }

        player.spigot()
                .sendMessage(root);
    }

    private void appendLegacy(
            TextComponent root,
            String text
    ) {
        BaseComponent[] components =
                TextComponent.fromLegacyText(
                        text
                );

        for (BaseComponent component
                : components) {

            root.addExtra(component);
        }
    }

    private String format(
            String message,
            String... replacements
    ) {
        return color(
                replace(
                        message,
                        replacements
                )
        );
    }

    private String replace(
            String message,
            String... replacements
    ) {
        if (replacements == null) {
            return message;
        }

        for (int i = 0;
             i + 1 < replacements.length;
             i += 2) {

            message =
                    message.replace(
                            replacements[i],
                            replacements[i + 1]
                    );
        }

        return message;
    }

    private String color(
            String message
    ) {
        return ChatColor
                .translateAlternateColorCodes(
                        '&',
                        message
                );
    }
}