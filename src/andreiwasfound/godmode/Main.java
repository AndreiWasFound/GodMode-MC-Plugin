package andreiwasfound.godmode;

import andreiwasfound.godmode.Commands.GodModeCommand;
import andreiwasfound.godmode.Commands.ReloadConfig;
import andreiwasfound.godmode.utilities.CommandTabCompleter;
import andreiwasfound.godmode.utilities.MetricsLite;
import andreiwasfound.godmode.utilities.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private List<Player> GodPlayers = new ArrayList<Player>();

    @Override
    public void onEnable() {
        printToConsole("UpdateChecker is trying to register");
        updateChecker();
        printToConsole("UpdateChecker has been registered successfully");
        printToConsole("Commands are trying to register");
        registerCommands();
        printToConsole("Commands have been registered successfully");
        printToConsole("Events are trying to register");
        registerEvents();
        printToConsole("Events have been registered successfully");
        printToConsole("Config.yml is trying to register");
        saveDefaultConfig();
        printToConsole("Config.yml has been registered successfully");
        printToConsole("bStats is trying to register");
        int pluginId = 8295;
        MetricsLite metrics = new MetricsLite(this, pluginId);
        printToConsole("bStats has been registered successfully");
    }

    @Override
    public void onDisable() {

    }

    public void addGodPlayer(Player player) {
        GodPlayers.add(player);
    }

    public void removeGodPlayer(Player player) {
        GodPlayers.remove(player);
    }

    public List<Player> getGodPlayers() {
        return GodPlayers;
    }

    public boolean hasGodPlayers() {
        if (GodPlayers.isEmpty())
            return false;
        return true;
    }

    public void registerCommands() {
        getCommand("god").setExecutor(new GodModeCommand(this));
        getCommand("godmode").setExecutor(new ReloadConfig(this));
        getCommand("godmode").setTabCompleter(new CommandTabCompleter());
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new Events(this), this);
        pm.registerEvents(new JoinUpdateMessage(this), this);
    }

    public void printToConsole(String msg) {
        this.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "GodMode" + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
    }

    public void updateChecker() {
        new UpdateChecker(this, 81882).getLatestVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                printToConsole("GodMode is up to date!");
            } else {
                printToConsole("GodMode is " + ChatColor.RED + "outdated!");
                printToConsole("Newest version: " + ChatColor.GOLD + version);
                printToConsole("Your version: " + ChatColor.RED + pluginymlVersion);
                printToConsole("Please Update Here: " + ChatColor.RED + pluginymlWebsite);
            }
        });
    }
    PluginDescriptionFile pluginyml = this.getDescription();
    String pluginymlVersion = pluginyml.getVersion();
    String pluginymlWebsite = pluginyml.getWebsite();
}