import com.replaymod.gradle.preprocess.RootPreprocessExtension

plugins {
    id("com.replaymod.preprocess") apply false
}

project.extensions.create("preprocess", RootPreprocessExtension::class)
configure<RootPreprocessExtension> {
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
