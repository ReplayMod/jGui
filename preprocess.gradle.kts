import com.replaymod.gradle.preprocess.RootPreprocessExtension

plugins {
    id("com.replaymod.preprocess") apply false
}

project.extensions.create("preprocess", RootPreprocessExtension::class)
configure<RootPreprocessExtension> {
    val mc11604 = createNode("1.16.4", 11604, "yarn")
    val mc11601 = createNode("1.16.1", 11601, "yarn")
    val mc11502 = createNode("1.15.2", 11502, "yarn")
    val mc11404 = createNode("1.14.4", 11404, "yarn")
    val mc11404Forge = createNode("1.14.4-forge", 11404, "srg")
    val mc11200 = createNode("1.12", 11200, "srg")
    val mc10904 = createNode("1.9.4", 10904, "srg")
    val mc10809 = createNode("1.8.9", 10809, "srg")
    val mc10800 = createNode("1.8", 10800, "srg")
    val mc10710 = createNode("1.7.10", 10710, "srg")

    mc11604.link(mc11601)
    mc11601.link(mc11502)
    mc11502.link(mc11404, file("versions/mapping-fabric-1.15.2-1.14.4.txt"))
    mc11404.link(mc11404Forge, file("versions/mapping-1.14.4-fabric-forge.txt"))
    mc11404Forge.link(mc11200, file("versions/1.14.4-forge/mapping.txt"))
    mc11200.link(mc10904, file("versions/1.12/mapping.txt"))
    mc10904.link(mc10809, file("versions/1.9.4/mapping.txt"))
    mc10809.link(mc10800)
    mc10800.link(mc10710, file("versions/1.8/mapping.txt"))
}
