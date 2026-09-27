package io.github.andreiwasfound.godmode.manager;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class GodModeManager {

    private final Set<UUID> godPlayers =
            new HashSet<UUID>();

    public boolean isGod(Player player) {
        return godPlayers.contains(
                player.getUniqueId()
        );
    }

    public void enableGod(Player player) {
        godPlayers.add(
                player.getUniqueId()
        );
    }

    public void disableGod(Player player) {
        godPlayers.remove(
                player.getUniqueId()
        );
    }

    public boolean toggleGod(Player player) {
        UUID uuid = player.getUniqueId();

        if (godPlayers.remove(uuid)) {
            return false;
        }

        godPlayers.add(uuid);
        return true;
    }

    public void loadGodPlayers(
            Collection<UUID> players
    ) {
        godPlayers.clear();
        godPlayers.addAll(players);
    }

    public Set<UUID> getGodPlayers() {
        return Collections.unmodifiableSet(
                new HashSet<UUID>(godPlayers)
        );
    }
}