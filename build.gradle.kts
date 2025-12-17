plugins {
    id("fabric-loom") version "1.13-SNAPSHOT"
    id("java")
}

fun opt(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org")
}

loom {
    accessWidenerPath = file("src/main/resources/rpf.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:${findProperty("deps.mc")}")
    mappings(loom.layered() {
        officialMojangMappings()
        opt("deps.parchment") {
            parchment("org.parchmentmc.data:parchment-${findProperty("deps.mc")}:${it}@zip")
        }
    })
    modImplementation("net.fabricmc:fabric-loader:${findProperty("deps.fabric")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${findProperty("deps.fapi")}")
    modImplementation(files("lib/RPRenames-1.21.8-0.9.2.jar"))
}

tasks.processResources {
    inputs.property("id", findProperty("mod.id"))
    inputs.property("name", findProperty("mod.name"))
    inputs.property("version", findProperty("mod.version"))
    inputs.property("mcdep", findProperty("mod.mcdep"))
    inputs.property("minecraft_version", findProperty("deps.mc"))
    inputs.property("description", findProperty("mod.description"))
    inputs.property("author", findProperty("mod.author"))

    val map = mapOf(
        "id" to findProperty("mod.id"),
        "name" to findProperty("mod.name"),
        "version" to findProperty("mod.version"),
        "mcdep" to findProperty("mod.mcdep"),
        "minecraft_version" to findProperty("deps.mc"),
        "description" to findProperty("mod.description"),
        "author" to findProperty("mod.author")
    )

    filesMatching("fabric.mod.json") { expand(map) }
}
