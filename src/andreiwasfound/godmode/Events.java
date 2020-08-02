package andreiwasfound.godmode;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class Events implements Listener {

    private Main plugin;

    public Events(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (plugin.getGodPlayers().contains(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (plugin.getGodPlayers().contains(p)) {
                e.setCancelled(true);
            }
        }
    }
}