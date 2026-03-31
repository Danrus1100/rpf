plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    id("java")
    id("maven-publish")
}

fun opt(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}


apply(from = rootProject.file("gradle/common.gradle.kts"))

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org")
    maven("https://api.modrinth.com/maven")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/rpf.accesswidener")
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

    // modCompileOnly
    // modImplementation

    modImplementation("maven.modrinth:my_totem_doll:${findProperty("deps.mtd")}")
    modImplementation("maven.modrinth:rp-renames:Q7MQm2v2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
