import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.6"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "lordpipe.terracottascrewdriver"
version = "1.4.0"
description = "Block rotater tool"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    paperDevBundle("1.19.4-R0.1-SNAPSHOT")
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
