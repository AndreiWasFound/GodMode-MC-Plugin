import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("com.gradleup.shadow") version "9.6.1"
}

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    // Compile against an old Spigot API for wide backwards compatibility.
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.2.1")
}

java {
    // Gradle / development toolchain
    toolchain.languageVersion = JavaLanguageVersion.of(25)

    // GodMode itself is compiled as Java 8 bytecode
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
    }

    shadowJar {
        archiveClassifier.set("")

        relocate(
            "org.bstats",
            "io.github.andreiwasfound.godmode.libs.bstats"
        )
    }

    /*
     * Main development server
     * Minecraft 26.2 / Java 25
     */
    runServer {
        minecraftVersion("26.2")

        jvmArgs(
            "-Xms2G",
            "-Xmx2G"
        )
    }

    /*
     * Minecraft 1.12.2 / Java 11
     */
    register<RunServer>("runServer1122") {
        group = "run paper"
        description = "Run Paper 1.12.2 for GodMode compatibility testing"

        minecraftVersion("1.12.2")
        runDirectory = projectDir.resolve("run-1.12.2")

        pluginJars(
            shadowJar.flatMap { it.archiveFile }
        )

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(11)
        }

        jvmArgs(
            "-Xms1G",
            "-Xmx2G"
        )
    }

    /*
     * Minecraft 1.16.5 / Java 16
     */
    register<RunServer>("runServer1165") {
        group = "run paper"
        description = "Run Paper 1.16.5 for GodMode compatibility testing"

        minecraftVersion("1.16.5")
        runDirectory = projectDir.resolve("run-1.16.5")

        pluginJars(
            shadowJar.flatMap { it.archiveFile }
        )

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(16)
        }

        jvmArgs(
            "-Xms1G",
            "-Xmx2G"
        )
    }

    /*
     * Minecraft 1.20.1 / Java 21
     */
    register<RunServer>("runServer1201") {
        group = "run paper"
        description = "Run Paper 1.20.1 for GodMode compatibility testing"

        minecraftVersion("1.20.1")
        runDirectory = projectDir.resolve("run-1.20.1")

        pluginJars(
            shadowJar.flatMap { it.archiveFile }
        )

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }

        jvmArgs(
            "-Xms1G",
            "-Xmx2G"
        )
    }

    /*
     * Minecraft 1.21.11 / Java 21
     */
    register<RunServer>("runServer12111") {
        group = "run paper"
        description = "Run Paper 1.21.11 for GodMode compatibility testing"

        minecraftVersion("1.21.11")
        runDirectory = projectDir.resolve("run-1.21.11")

        pluginJars(
            shadowJar.flatMap { it.archiveFile }
        )

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }

        jvmArgs(
            "-Xms1G",
            "-Xmx2G"
        )
    }

    /*
     * Minecraft 26.3 / Java 25
     */
    register<RunServer>("runServer263") {
        group = "run paper"
        description = "Run Paper 26.3 for GodMode compatibility testing"

        minecraftVersion("26.3")
        runDirectory = projectDir.resolve("run-26.3")

        pluginJars(
            shadowJar.flatMap { it.archiveFile }
        )

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
        }

        jvmArgs(
            "-Xms2G",
            "-Xmx2G"
        )
    }

    /*
     * Minecraft 1.8.8 / Java 8
     */
    register<RunServer>("runServer188") {
        group = "run paper"
        description = "Run Paper 1.8.8 for GodMode compatibility testing"

        minecraftVersion("1.8.8")
        runDirectory = projectDir.resolve("run-1.8.8")

        pluginJars(
            shadowJar.flatMap { it.archiveFile }
        )

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(8)
        }

        jvmArgs(
            "-Xms1G",
            "-Xmx2G"
        )
    }

    /*
     * Minecraft 1.7.10 / Java 8
     */
    register<RunServer>("runServer1710") {
        group = "run paper"
        description = "Run Paper 1.7.10 for GodMode compatibility testing"

        minecraftVersion("1.7.10")
        runDirectory = projectDir.resolve("run-1.7.10")

        pluginJars(
            shadowJar.flatMap { it.archiveFile }
        )

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(8)
        }

        jvmArgs(
            "-Xms1G",
            "-Xmx2G"
        )
    }

    processResources {
        val props = mapOf(
            "version" to version,
            "description" to project.description
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}