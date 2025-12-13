plugins {
    id("gg.essential.multi-version.root")
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
    strictExtraMappings.set(true)

    val mc12111 = createNode("1.21.11", 12111, "yarn")
    val mc12110 = createNode("1.21.10", 12110, "yarn")
    val mc12107 = createNode("1.21.7", 12107, "yarn")
    val mc12105 = createNode("1.21.5", 12105, "yarn")
    val mc12104 = createNode("1.21.4", 12104, "yarn")
    val mc12102 = createNode("1.21.2", 12102, "yarn")
    val mc12100 = createNode("1.21", 12100, "yarn")
    val mc12006 = createNode("1.20.6", 12006, "yarn")
    val mc12004 = createNode("1.20.4", 12004, "yarn")
    val mc12002 = createNode("1.20.2", 12002, "yarn")
    val mc12001 = createNode("1.20.1", 12001, "yarn")
    val mc11904 = createNode("1.19.4", 11904, "yarn")
    val mc11903 = createNode("1.19.3", 11903, "yarn")
    val mc11902 = createNode("1.19.2", 11902, "yarn")
    val mc11901 = createNode("1.19.1", 11901, "yarn")
    val mc11900 = createNode("1.19", 11900, "yarn")
    val mc11802 = createNode("1.18.2", 11802, "yarn")
    val mc11801 = createNode("1.18.1", 11801, "yarn")
    val mc11701 = createNode("1.17.1", 11701, "yarn")
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

    mc12111.link(mc12110)
    mc12110.link(mc12107)
    mc12107.link(mc12105)
    mc12105.link(mc12104, file("versions/mapping-fabric-1.21.5-1.21.4.txt"))
    mc12104.link(mc12102)
    mc12102.link(mc12100)
    mc12100.link(mc12006)
    mc12006.link(mc12004)
    mc12004.link(mc12002)
    mc12002.link(mc12001)
    mc12001.link(mc11904)
    mc11904.link(mc11903)
    mc11903.link(mc11902)
    mc11902.link(mc11901)
    mc11901.link(mc11900)
    mc11900.link(mc11802)
    mc11802.link(mc11801)
    mc11801.link(mc11701)
    mc11701.link(mc11604, file("versions/mapping-fabric-1.17.1-1.16.4.txt"))
    mc11604.link(mc11601)
    mc11601.link(mc11502)
    mc11502.link(mc11404, file("versions/mapping-fabric-1.15.2-1.14.4.txt"))
    mc11404.link(mc11404Forge, file("versions/mapping-1.14.4-fabric-forge.txt"))
    mc11404Forge.link(mc11200, file("versions/1.14.4-forge/mapping.txt"))
    mc11200.link(mc10904)
    mc10904.link(mc10809)
    mc10809.link(mc10800)
    mc10800.link(mc10710, file("versions/1.8/mapping.txt"))
}

subprojects {
    val (_, minor) = name.split("-")[0].split(".")
    val fabric = minor.toInt() >= 14 && !name.endsWith("-forge")
    extra.set("loom.platform", if (fabric) "fabric" else "forge")
}
