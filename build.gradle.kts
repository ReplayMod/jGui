plugins {
    id("fabric-loom") version "0.2.5-SNAPSHOT" apply false
    id("com.replaymod.preprocess") version "59a641a"
    id("com.github.hierynomus.license") version "0.15.0"
}

group = "de.johni0702.minecraft"
version = "git"

license {
    extra.set("name", "jGui API")
    extra.set("author", "johni0702")
    extra.set("url", "https://github.com/johni0702")
    extra.set("year", "2016")
    header = File(project.getProjectDir(), "HEADER.txt")
    ignoreFailures = false
    strictCheck = true
    mapping("java", "SLASHSTAR_STYLE")
}

// Loom tries to find the active mixin version by recursing up to the root project and checking each project's
// compileClasspath and build script classpath (in that order). Since we've loom in our root project's classpath,
// loom will only find it after checking the root project's compileClasspath (which doesn't exist by default).
configurations.register("compileClasspath")

preprocess {
    "1.15.2"(11502, "yarn") {
        "1.14.4"(11404, "yarn") {
            "1.14.4-forge"(11404, "srg", file("versions/mapping-1.14.4-fabric-forge.txt")) {
                "1.12"(11200, "srg", file("versions/1.14.4-forge/mapping.txt")) {
                    "1.9.4"(10904, "srg", file("versions/1.12/mapping.txt")) {
                        "1.8.9"(10809, "srg", file("versions/1.9.4/mapping.txt")) {
                            "1.8"(10800, "srg") {
                                "1.7.10"(10710, "srg", file("versions/1.8/mapping.txt"))
                            }
                        }
                    }
                }
            }
        }
    }
}
