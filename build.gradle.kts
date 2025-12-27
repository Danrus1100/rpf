plugins {
    id("fabric-loom") version "1.13-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    id("java")
}

fun opt(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

fun prop(name: String) : String {
    return findProperty(name)?.toString() ?: throw IllegalArgumentException("Missing property: $name")
}

val mainBranch = "multiversion"
val gitBranchName = providers.exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}.standardOutput.asText.map { it.trim() }.get()
val minecraft = property("deps.mc") as String

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/rpf.accesswidener")
}

stonecutter{
    replacements {
        string {
            direction = eval(current.version, ">=1.21.11")
            replace("ResourceLocation", "Identifier")
        }

        string {
            direction = eval(current.version, ">=1.21.11")
            replace("import net.minecraft.Util;", "import net.minecraft.util.Util;")
        }

        string {
            direction = eval(current.version, ">=1.21.10")
            replace("LivingEntity ", "ItemOwner ")
        }

        string {
            direction = eval(current.version, ">=1.21.10")
            replace("LivingEntity;", "ItemOwner;")
        }
    }
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
    if (findProperty("deps.mc").toString() == "1.21.8") {
        modImplementation(rootProject.files("lib/RPRenames-1.21.8-0.9.2.jar"))
    }
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

base {
    archivesName.set(findProperty("mod.id") as String)
}

publishMods {
    val modrinthToken = findProperty("modrinth-token")
    val curseforgeToken = findProperty("curseforge-token")
    val discordWebhookDR = findProperty("discord-webhook")
    val discordWebhookDry = findProperty("discord-webhook-dry")

    dryRun = gitBranchName != mainBranch

    type = STABLE

    file.set(tasks.named("remapJar").flatMap { (it as org.gradle.jvm.tasks.Jar).archiveFile })

    changelog = rootProject.file("CHANGELOG.md").readText()

    val loaders = prop("pub.target.platforms").split(' ')
    loaders.forEach(modLoaders::add)
    displayName = "PRF ${prop("mod.version")} for ${minecraft}"
    version = "${prop("mod.version")}-${minecraft}"

    val targets = prop("pub.target.versions").split(' ')
    modrinth {
        projectId = prop("publish.modrinth")
        accessToken = modrinthToken.toString()
        targets.forEach(minecraftVersions::add)
    }

    curseforge {
        projectId = prop("publish.curseforge")
        accessToken = curseforgeToken.toString()
        projectSlug = prop("pub.slug")
        targets.forEach(minecraftVersions::add)
    }

    if (targets.contains("1.21.8") && loaders.contains("fabric")) {
        discord ("DR freak mods anonuncement") {
            webhookUrl = discordWebhookDR.toString()
            dryRunWebhookUrl = discordWebhookDry.toString()

            username  = prop("mod.name")
            avatarUrl = "https://github.com/Danrus1100/rpf/blob/main/src/main/resources/assets/rpf/icon.png?raw=true"

            content = changelog.map{ "# " + prop("mod.version") + " version here! \n\n" + rootProject.file("CHANGELOG.md").readText() +"\n\n<@&1426901890582581248>" }
        }
    }
}

version = findProperty("mod.version") as String + "-" +findProperty("deps.mc") as String
