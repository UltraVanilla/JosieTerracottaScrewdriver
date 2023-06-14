import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "lordpipe.terracottascrewdriver"
version = "1.5.0"
description = "Block rotater tool"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    paperDevBundle("1.20-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "lordpipe.terracottascrewdriver.TerracottaScrewdriver"
    apiVersion = "1.19"
    authors = listOf("lordpipe")
}
