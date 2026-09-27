package io.github.andreiwasfound.godmode.update;

import io.github.andreiwasfound.godmode.GodMode;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

public final class UpdateChecker {

    private static final int RESOURCE_ID = 81882;

    private static final String UPDATE_API_URL =
            "https://api.spigotmc.org/legacy/update.php?resource="
                    + RESOURCE_ID;

    private static final String RESOURCE_URL =
            "https://www.spigotmc.org/resources/godmode."
                    + RESOURCE_ID + "/";

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private final GodMode plugin;

    private final AtomicLong checkGeneration =
            new AtomicLong();

    private volatile String latestVersion;
    private volatile boolean updateAvailable;

    public UpdateChecker(GodMode plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        if (!plugin.isEnabled()) {
            return;
        }

        final long generation =
                checkGeneration.incrementAndGet();

        latestVersion = null;
        updateAvailable = false;

        final String currentVersion =
                plugin.getDescription().getVersion();

        Bukkit.getScheduler().runTaskAsynchronously(
                plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        performCheck(
                                generation,
                                currentVersion
                        );
                    }
                }
        );
    }

    private void performCheck(
            final long generation,
            final String currentVersion
    ) {
        HttpURLConnection connection = null;

        try {
            URL url =
                    new URL(UPDATE_API_URL);

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setUseCaches(false);

            connection.setRequestProperty(
                    "User-Agent",
                    "GodMode/" + currentVersion
            );

            connection.setRequestProperty(
                    "Accept",
                    "text/plain"
            );

            int responseCode =
                    connection.getResponseCode();

            if (!isCurrentCheck(generation)) {
                return;
            }

            if (responseCode
                    != HttpURLConnection.HTTP_OK) {

                logWarning(
                        generation,
                        "Update check failed with HTTP status "
                                + responseCode + "."
                );

                return;
            }

            String version;

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         connection.getInputStream(),
                                         StandardCharsets.UTF_8
                                 )
                         )) {

                version = reader.readLine();
            }

            if (version == null
                    || version.trim().isEmpty()) {

                logWarning(
                        generation,
                        "Update check returned an empty version."
                );

                return;
            }

            version = version.trim();

            if (!isCurrentCheck(generation)) {
                return;
            }

            final String checkedVersion =
                    version;

            final int comparison =
                    compareVersions(
                            currentVersion,
                            checkedVersion
                    );

            latestVersion =
                    checkedVersion;

            updateAvailable =
                    comparison < 0;

            runOnMainThread(
                    generation,
                    new Runnable() {
                        @Override
                        public void run() {
                            logResult(
                                    currentVersion,
                                    checkedVersion,
                                    comparison
                            );
                        }
                    }
            );

        } catch (Exception exception) {
            if (isCurrentCheck(generation)) {
                logWarning(
                        generation,
                        "Could not check for updates: "
                                + getErrorMessage(exception)
                );
            }

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void logResult(
            String currentVersion,
            String checkedVersion,
            int comparison
    ) {
        if (comparison < 0) {
            plugin.getLogger().warning(
                    "A new GodMode version is available!"
            );

            plugin.getLogger().warning(
                    "Installed version: "
                            + currentVersion
            );

            plugin.getLogger().warning(
                    "Latest version: "
                            + checkedVersion
            );

            plugin.getLogger().warning(
                    "Update: "
                            + RESOURCE_URL
            );

            return;
        }

        if (comparison > 0) {
            plugin.getLogger().info(
                    "You are running a newer/development version of GodMode."
            );

            plugin.getLogger().info(
                    "Installed version: "
                            + currentVersion
            );

            plugin.getLogger().info(
                    "Latest public version: "
                            + checkedVersion
            );

            return;
        }

        plugin.getLogger().info(
                "GodMode is up to date."
        );
    }

    public void reset() {
        checkGeneration.incrementAndGet();

        latestVersion = null;
        updateAvailable = false;
    }

    private boolean isCurrentCheck(
            long generation
    ) {
        return generation
                == checkGeneration.get();
    }

    private int compareVersions(
            String current,
            String latest
    ) {
        String currentBase =
                current
                        .replaceFirst("^[vV]", "")
                        .split("-", 2)[0];

        String latestBase =
                latest
                        .replaceFirst("^[vV]", "")
                        .split("-", 2)[0];

        String[] currentParts =
                currentBase.split("\\.");

        String[] latestParts =
                latestBase.split("\\.");

        int length =
                Math.max(
                        currentParts.length,
                        latestParts.length
                );

        for (int i = 0; i < length; i++) {
            int currentNumber =
                    i < currentParts.length
                            ? parseVersionPart(
                            currentParts[i]
                    )
                            : 0;

            int latestNumber =
                    i < latestParts.length
                            ? parseVersionPart(
                            latestParts[i]
                    )
                            : 0;

            if (currentNumber < latestNumber) {
                return -1;
            }

            if (currentNumber > latestNumber) {
                return 1;
            }
        }

        boolean currentPreRelease =
                current.contains("-");

        boolean latestPreRelease =
                latest.contains("-");

        if (currentPreRelease
                && !latestPreRelease) {
            return -1;
        }

        if (!currentPreRelease
                && latestPreRelease) {
            return 1;
        }

        return 0;
    }

    private int parseVersionPart(
            String part
    ) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private void logWarning(
            final long generation,
            final String message
    ) {
        runOnMainThread(
                generation,
                new Runnable() {
                    @Override
                    public void run() {
                        plugin.getLogger()
                                .warning(message);
                    }
                }
        );
    }

    private void runOnMainThread(
            final long generation,
            final Runnable task
    ) {
        if (!plugin.isEnabled()
                || !isCurrentCheck(generation)) {
            return;
        }

        try {
            Bukkit.getScheduler().runTask(
                    plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            if (!plugin.isEnabled()
                                    || !isCurrentCheck(
                                    generation
                            )) {
                                return;
                            }

                            task.run();
                        }
                    }
            );
        } catch (RuntimeException exception) {
            /*
             * The plugin may have been disabled between
             * the enabled check and task scheduling.
             * In that case there is nothing left to report.
             */
        }
    }

    private String getErrorMessage(
            Exception exception
    ) {
        String message =
                exception.getMessage();

        if (message == null
                || message.trim().isEmpty()) {
            return exception
                    .getClass()
                    .getSimpleName();
        }

        return message;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}