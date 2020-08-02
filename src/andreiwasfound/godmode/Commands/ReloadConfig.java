package andreiwasfound.godmode.Commands;

import andreiwasfound.godmode.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfig implements CommandExecutor {

    private Main plugin;
    public ReloadConfig(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!sender.hasPermission("godmode.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /god");
                sender.sendMessage(ChatColor.RED + "Usage: /godmode reload");
                return true;
            }
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("godmode.reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.RED + "GodMode config has been reloaded");
                    plugin.printToConsole(ChatColor.RED + "GodMode config has been reloaded");
                    return true;
                    }
                }
            }
            return false;
        }
}
