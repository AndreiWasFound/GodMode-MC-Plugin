package andreiwasfound.godmode;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    public static List<String> list = new ArrayList<String>();

    @Override
    public void onEnable() {
        printToConsole("UpdateChecker is trying to register");
        updateChecker();
        printToConsole("UpdateChecker has been registered successfully");
        printToConsole("Events are trying to register");
        registerEvents();
        printToConsole("Events have been registered successfully");

        int pluginId = 8295;
        MetricsLite metrics = new MetricsLite(this, pluginId);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("god")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console cannot use this command");
                return true;
            }
            Player player = (Player) sender;
            if (sender instanceof Player) {
                if (player.hasPermission("godmode.use")) {
                    if (list.contains(player.getUniqueId().toString())) {
                        list.remove(player.getUniqueId().toString());
                        player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are no longer in God mode!");
                    } else {
                        list.add(player.getUniqueId().toString());
                        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You are now in God mode!");
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You don't have permission to use this command!");

                }
            }
        }
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player) {
            if (Main.list.contains(event.getEntity().getUniqueId().toString())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            if (Main.list.contains(event.getEntity().getUniqueId().toString())) {
                event.setCancelled(true);
            }
        }
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    public void printToConsole(String msg) {
        this.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "GodMode" + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
    }

    public void updateChecker() {
        new UpdateChecker(this, 81882).getLatestVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                printToConsole("DiamondBrokeMessage is up to date!");
            } else {
                printToConsole("DiamondBrokeMessage is outdated!");
                printToConsole("Newest version: " + version);
                printToConsole("Your version: " + configVersion);
                printToConsole("Please Update Here: " + configWebsite);
            }
        });
    }
    PluginDescriptionFile config = this.getDescription();
    String configVersion = config.getVersion();
    String configWebsite = config.getWebsite();
}