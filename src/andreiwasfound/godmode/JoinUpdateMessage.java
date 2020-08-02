package andreiwasfound.godmode;

import andreiwasfound.godmode.utilities.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinUpdateMessage implements Listener {

    private Main plugin;

    JoinUpdateMessage(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("godmode.updatemessage")) {
        new UpdateChecker(plugin, 81882).getLatestVersion(version -> {
            if (!(plugin.getDescription().getVersion().equalsIgnoreCase(version))) {
                player.sendMessage(ChatColor.YELLOW + "GodMode is " + ChatColor.RED + "outdated!");
                player.sendMessage(ChatColor.YELLOW + "Newest version: " + ChatColor.GOLD +  version);
                player.sendMessage(ChatColor.YELLOW + "Server version: " + ChatColor.RED + plugin.pluginymlVersion);
                player.sendMessage(ChatColor.YELLOW + "Please Update Here: " + ChatColor.RED + plugin.pluginymlWebsite);
            }
        });
    }
    }
}
