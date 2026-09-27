package io.github.andreiwasfound.godmode.config;

import io.github.andreiwasfound.godmode.GodMode;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class ConfigMigrator {

    public static final int CURRENT_CONFIG_VERSION = 1;

    private final GodMode plugin;

    public ConfigMigrator(GodMode plugin) {
        this.plugin = plugin;
    }

    public void migrate() {
        FileConfiguration config = plugin.getConfig();

        int configVersion =
                config.getInt("config-version", 0);

        if (configVersion >= CURRENT_CONFIG_VERSION) {
            return;
        }

        boolean legacyConfig =
                config.contains("god-msg")
                        || config.contains("ungod-msg");

        if (!legacyConfig) {
            return;
        }

        File configFile =
                new File(
                        plugin.getDataFolder(),
                        "config.yml"
                );

        try {
            File backupFile =
                    createBackup(configFile);

            String defaultConfig =
                    readDefaultConfig();

            if (defaultConfig == null) {
                plugin.getLogger().severe(
                        "Could not migrate configuration: "
                                + "default config.yml was not found inside the plugin."
                );
                return;
            }

            Object oldGodMessage =
                    getLegacyMessage(
                            config,
                            "god-msg"
                    );

            Object oldUngodMessage =
                    getLegacyMessage(
                            config,
                            "ungod-msg"
                    );

            String migratedConfig =
                    defaultConfig;

            if (oldGodMessage != null) {
                migratedConfig =
                        replaceMessageEntry(
                                migratedConfig,
                                "god-enabled",
                                oldGodMessage
                        );
            }

            if (oldUngodMessage != null) {
                migratedConfig =
                        replaceMessageEntry(
                                migratedConfig,
                                "god-disabled",
                                oldUngodMessage
                        );
            }

            writeConfig(
                    configFile,
                    migratedConfig
            );

            plugin.reloadConfig();

            plugin.getLogger().info(
                    "Migrated GodMode 2.x configuration to 3.0."
            );

            plugin.getLogger().info(
                    "A backup of the old configuration was saved as "
                            + backupFile.getName() + "."
            );

        } catch (IOException exception) {
            plugin.getLogger().severe(
                    "Could not migrate GodMode configuration: "
                            + exception.getMessage()
            );
        }
    }

    private Object getLegacyMessage(
            FileConfiguration config,
            String path
    ) {
        if (config.isList(path)) {
            List<String> messages =
                    config.getStringList(path);

            if (messages.isEmpty()) {
                return null;
            }

            return new ArrayList<String>(messages);
        }

        String message =
                config.getString(path);

        if (message == null
                || message.isEmpty()) {
            return null;
        }

        return message;
    }

    private File createBackup(
            File configFile
    ) throws IOException {
        File backup =
                new File(
                        plugin.getDataFolder(),
                        "config-v2-backup.yml"
                );

        int counter = 1;

        while (backup.exists()) {
            backup =
                    new File(
                            plugin.getDataFolder(),
                            "config-v2-backup-"
                                    + counter
                                    + ".yml"
                    );

            counter++;
        }

        Files.copy(
                configFile.toPath(),
                backup.toPath(),
                StandardCopyOption.COPY_ATTRIBUTES
        );

        return backup;
    }

    private String readDefaultConfig()
            throws IOException {
        InputStream input =
                plugin.getResource("config.yml");

        if (input == null) {
            return null;
        }

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                input,
                                StandardCharsets.UTF_8
                        )
                );

        StringBuilder builder =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line)
                    .append('\n');
        }

        reader.close();

        return builder.toString();
    }

    private String replaceMessageEntry(
            String configText,
            String key,
            Object value
    ) {
        String[] lines =
                configText.split("\\r?\\n", -1);

        String target =
                "  " + key + ":";

        String replacement =
                serializeMessage(
                        key,
                        value
                );

        StringBuilder result =
                new StringBuilder();

        boolean replaced = false;

        for (String line : lines) {
            if (!replaced
                    && line.startsWith(target)) {

                result.append(replacement);
                replaced = true;

            } else {
                result.append(line)
                        .append('\n');
            }
        }

        return result.toString();
    }

    private String serializeMessage(
            String key,
            Object value
    ) {
        StringBuilder result =
                new StringBuilder();

        if (value instanceof List) {
            result.append("  ")
                    .append(key)
                    .append(":")
                    .append('\n');

            List<?> values =
                    (List<?>) value;

            for (Object item : values) {
                result.append("    - '")
                        .append(
                                escapeYamlString(
                                        String.valueOf(item)
                                )
                        )
                        .append("'")
                        .append('\n');
            }

            return result.toString();
        }

        result.append("  ")
                .append(key)
                .append(": '")
                .append(
                        escapeYamlString(
                                String.valueOf(value)
                        )
                )
                .append("'")
                .append('\n');

        return result.toString();
    }

    private String escapeYamlString(
            String value
    ) {
        return value.replace("'", "''");
    }

    private void writeConfig(
            File configFile,
            String content
    ) throws IOException {
        Writer writer =
                new OutputStreamWriter(
                        new FileOutputStream(
                                configFile
                        ),
                        StandardCharsets.UTF_8
                );

        writer.write(content);
        writer.close();
    }
}