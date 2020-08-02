package andreiwasfound.godmode.Commands;

import andreiwasfound.godmode.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodModeCommand implements CommandExecutor {

    private Main plugin;

    public GodModeCommand(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (player.hasPermission("godmode.use")) {
            if (plugin.hasGodPlayers()) {
                if (plugin.getGodPlayers().contains(player)) {
                    plugin.removeGodPlayer(player);
                    for (String ungodMsg : plugin.getConfig().getStringList("ungod-msg")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', ungodMsg));
                    }
                    return true;
                }
            }
            plugin.addGodPlayer(player);
            for (String godMsg : plugin.getConfig().getStringList("god-msg")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', godMsg));
            }
            return true;
        }
        return false;
    }
}
