package io.github.andreiwasfound.godmode.listener;

import io.github.andreiwasfound.godmode.GodMode;
import io.github.andreiwasfound.godmode.message.MessageService;
import io.github.andreiwasfound.godmode.update.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UpdateNotificationListener
        implements Listener {

    private final GodMode plugin;
    private final UpdateChecker updateChecker;
    private final MessageService messageService;

    public UpdateNotificationListener(
            GodMode plugin,
            UpdateChecker updateChecker,
            MessageService messageService
    ) {
        this.plugin = plugin;
        this.updateChecker = updateChecker;
        this.messageService = messageService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean(
                "update-checker.enabled",
                true
        )) {
            return;
        }

        if (!plugin.getConfig().getBoolean(
                "update-checker.notify-admins",
                true
        )) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("godmode.updates")) {
            return;
        }

        if (!updateChecker.isUpdateAvailable()) {
            return;
        }

        String latestVersion =
                updateChecker.getLatestVersion();

        if (latestVersion == null) {
            return;
        }

        messageService.sendUpdateAvailable(
                player,
                latestVersion
        );
    }
}