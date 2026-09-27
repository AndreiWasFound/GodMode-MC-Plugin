package io.github.andreiwasfound.godmode.persistence;

import io.github.andreiwasfound.godmode.GodMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class GodModePersistence {

    private final GodMode plugin;
    private final File dataFile;

    public GodModePersistence(GodMode plugin) {
        this.plugin = plugin;

        this.dataFile =
                new File(
                        plugin.getDataFolder(),
                        "data.yml"
                );
    }

    public Set<UUID> load() {
        Set<UUID> players =
                new HashSet<UUID>();

        if (!dataFile.exists()) {
            return players;
        }

        YamlConfiguration data =
                YamlConfiguration.loadConfiguration(
                        dataFile
                );

        for (String value :
                data.getStringList("god-players")) {

            try {
                players.add(
                        UUID.fromString(value)
                );
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning(
                        "Ignoring invalid UUID in data.yml: "
                                + value
                );
            }
        }

        return players;
    }

    public void save(Set<UUID> players) {
        if (!isEnabled()) {
            return;
        }

        YamlConfiguration data =
                new YamlConfiguration();

        List<String> uuids =
                players.stream()
                        .map(UUID::toString)
                        .sorted()
                        .collect(Collectors.toList());

        data.set(
                "god-players",
                uuids
        );

        try {
            data.save(dataFile);
        } catch (IOException exception) {
            plugin.getLogger().severe(
                    "Could not save GodMode persistence data: "
                            + exception.getMessage()
            );
        }
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean(
                "persistence.enabled",
                false
        );
    }
}