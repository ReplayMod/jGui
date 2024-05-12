import gg.essential.gradle.util.*

plugins {
    id("gg.essential.multi-version")
    id("gg.essential.defaults.repo")
    id("gg.essential.defaults.java")
    id("gg.essential.defaults.loom")
}

loom {
    mixin.defaultRefmapName.set("mixins.jgui.refmap.json")
    noRunConfigs()
}

java.withSourcesJar()

repositories {
    mavenLocal()
    maven("https://jitpack.io") {
        content {
            includeGroupByRegex("com\\.github\\..*")
        }
    }
    maven("https://repo.spongepowered.org/maven/") // for 0.7.11-SNAPSHOT Mixin
}

dependencies {
    api("com.github.ReplayMod:lwjgl-utils:27dcd66")

    if (!platform.isFabric) {
        // Mixin 0.8 is no longer compatible with MC 1.11.2 or older
        val mixinVersion = if (platform.mcVersion >= 11200) "0.8.2" else "0.7.11-SNAPSHOT"
        compileOnly("org.spongepowered:mixin:$mixinVersion")
    }

    if (platform.mcVersion >= 11604) {
        implementation(annotationProcessor("com.github.LlamaLad7:MixinExtras:0.1.1")!!)
    }
}
