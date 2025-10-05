plugins {
    `java-library`

    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.1.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.verni"
version = "1.0.0"
description = "System kooperacji graczy, stworzony dla doublecraft.pl."

repositories {
    maven("https://repo.papermc.io/repository/maven-public/") // <-- DODAJ TĘ LINIĘ
    gradlePluginPortal()
    mavenCentral()

    maven("https://repo.panda-lang.org/releases")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    // litecommands
    val litecommandsVersion = "3.4.2"
    implementation("dev.rollczi:litecommands-bukkit:${litecommandsVersion}")

    // peper api
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    // okaeri configs
    val okaeriConfigsVersion = "5.0.5"
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:${okaeriConfigsVersion}")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:${okaeriConfigsVersion}")

    // Gui liblary
    //implementation("dev.triumphteam:triumph-gui:3.1.11")

    // hikari
    implementation("com.zaxxer:HikariCP:5.1.0")

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

bukkit {
    main = "me.verni.doubleteams.DoubleTeamsPlugin"
    apiVersion = "1.20"
    prefix = "DoubleTeams"
    author = "Verni"
    name = "DoubleTeams"
    description = project.description
    version = project.version as String
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}

tasks {
    compileJava {
        options.compilerArgs = listOf("-Xlint:deprecation")
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    runServer {
        minecraftVersion("1.21.8")

        downloadPlugins {
     //       hangar("PlaceholderAPI", "2.11.5")
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
        }
    }

    shadowJar {
        archiveFileName.set("DoubleTeams v${project.version}.jar")

        exclude(
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**",
            "META-INF/**",
        )

        mergeServiceFiles()

    }
}