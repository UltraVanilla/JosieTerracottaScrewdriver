plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.8"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = "lordpipe.terracottascrewdriver"
version = "1.12.0"
description = "Block rotater tool"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

paper {
    authors = listOf("lordpipe", "JosieToolkit Contributors", "UltraVanilla Contributors")
    website = "https://ultravanilla.world/"

    main = "${project.group}.${project.name}"
    apiVersion = "1.21"

    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
}
