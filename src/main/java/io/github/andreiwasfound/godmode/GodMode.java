package io.github.andreiwasfound.godmode;

import io.github.andreiwasfound.godmode.command.GodCommand;
import io.github.andreiwasfound.godmode.command.GodModeCommand;
import io.github.andreiwasfound.godmode.config.ConfigMigrator;
import io.github.andreiwasfound.godmode.listener.GodModeListener;
import io.github.andreiwasfound.godmode.listener.UpdateNotificationListener;
import io.github.andreiwasfound.godmode.manager.GodModeManager;
import io.github.andreiwasfound.godmode.message.MessageService;
import io.github.andreiwasfound.godmode.persistence.GodModePersistence;
import io.github.andreiwasfound.godmode.update.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class GodMode extends JavaPlugin {

    private GodModeManager godModeManager;
    private MessageService messageService;
    private GodModePersistence persistence;
    private UpdateChecker updateChecker;
    private ConfigMigrator configMigrator;

    @Override
    public void onEnable() {
        godModeManager = new GodModeManager();

        saveDefaultConfig();

        configMigrator =
                new ConfigMigrator(this);

        configMigrator.migrate();

        messageService =
                new MessageService(getConfig());

        persistence =
                new GodModePersistence(this);

        if (persistence.isEnabled()) {
            godModeManager.loadGodPlayers(
                    persistence.load()
            );

            getLogger().info(
                    "Loaded "
                            + godModeManager
                            .getGodPlayers()
                            .size()
                            + " persisted God mode player(s)."
            );
        }

        new Metrics(this, 8295);

        GodCommand godCommand =
                new GodCommand(
                        this,
                        godModeManager,
                        messageService,
                        persistence
                );

        Objects.requireNonNull(
                getCommand("god")
        ).setExecutor(godCommand);

        Objects.requireNonNull(
                getCommand("god")
        ).setTabCompleter(godCommand);

        GodModeCommand godModeCommand =
                new GodModeCommand(
                        this,
                        messageService
                );

        Objects.requireNonNull(
                getCommand("godmode")
        ).setExecutor(godModeCommand);

        Objects.requireNonNull(
                getCommand("godmode")
        ).setTabCompleter(godModeCommand);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new GodModeListener(
                                godModeManager
                        ),
                        this
                );

        updateChecker =
                new UpdateChecker(this);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new UpdateNotificationListener(
                                this,
                                updateChecker,
                                messageService
                        ),
                        this
                );

        if (getConfig().getBoolean(
                "update-checker.enabled",
                true
        )) {
            updateChecker.checkForUpdates();
        }

        getLogger().info(
                "GodMode has been enabled."
        );
    }

    @Override
    public void onDisable() {
        if (persistence != null
                && godModeManager != null) {

            persistence.save(
                    godModeManager.getGodPlayers()
            );
        }

        if (updateChecker != null) {
            updateChecker.reset();
        }

        getLogger().info(
                "GodMode has been disabled."
        );
    }

    public void reloadGodModeConfiguration() {
        boolean persistenceWasEnabled =
                persistence != null
                        && persistence.isEnabled();

        reloadConfig();

        if (configMigrator != null) {
            configMigrator.migrate();
        }

        messageService.reload(
                getConfig()
        );

        boolean persistenceIsEnabled =
                persistence != null
                        && persistence.isEnabled();

        if (!persistenceWasEnabled
                && persistenceIsEnabled) {

            persistence.save(
                    godModeManager.getGodPlayers()
            );

            getLogger().info(
                    "Persistence enabled. Saved "
                            + godModeManager
                            .getGodPlayers()
                            .size()
                            + " current God mode player(s)."
            );

        } else if (persistenceWasEnabled
                && !persistenceIsEnabled) {

            getLogger().info(
                    "Persistence disabled. "
                            + "Current God mode states remain active "
                            + "for this server session."
            );
        }

        if (getConfig().getBoolean(
                "update-checker.enabled",
                true
        )) {
            updateChecker.checkForUpdates();
        } else {
            updateChecker.reset();

            getLogger().info(
                    "Update checker disabled."
            );
        }
    }

    public GodModeManager getGodModeManager() {
        return godModeManager;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}