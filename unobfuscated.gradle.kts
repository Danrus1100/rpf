plugins {
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    id("java")
    id("maven-publish")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

fun opt(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org")
    maven("https://api.modrinth.com/maven")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/rpf_unobfuscated.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:${findProperty("deps.mc")}")
    implementation("net.fabricmc:fabric-loader:${findProperty("deps.fabric")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${findProperty("deps.fapi")}")
    opt("deps.rprenames") {
//        implementation(rootProject.files("lib/${it}.jar"))
    }

    // compileOnly
    // implementation
    implementation("maven.modrinth:my_totem_doll:${findProperty("deps.mtd")}")
}
java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}
