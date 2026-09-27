package io.github.andreiwasfound.godmode.listener;

import io.github.andreiwasfound.godmode.manager.GodModeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class GodModeListener implements Listener {

    private final GodModeManager godModeManager;

    public GodModeListener(GodModeManager godModeManager) {
        this.godModeManager = godModeManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (godModeManager.isGod(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (godModeManager.isGod(player)
                && event.getFoodLevel() < player.getFoodLevel()) {
            event.setCancelled(true);
        }
    }
}