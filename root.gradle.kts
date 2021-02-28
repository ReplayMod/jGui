plugins {
    id("fabric-loom") version "0.6-SNAPSHOT" apply false
    id("com.replaymod.preprocess") version "7c4f90e"
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

preprocess {
    "1.16.4"(11604, "yarn") {
        "1.16.1"(11601, "yarn") {
            "1.15.2"(11502, "yarn") {
                "1.14.4"(11404, "yarn", file("versions/mapping-fabric-1.15.2-1.14.4.txt")) {
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
    }
}
