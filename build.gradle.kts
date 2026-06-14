plugins {
    id("dev.isxander.modstitch.base") version "0.8.4"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    id("java")
    id("maven-publish")
}

fun opt(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

fun prop(name: String): String =
    findProperty(name)?.toString() ?: throw IllegalArgumentException("Missing property: $name")

fun propExists(name: String) = project.properties.containsKey(name)

fun versionParts(version: String): List<Int> =
    version.split('.')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toIntOrNull() ?: 0 }

fun versionAtLeast(version: String, floor: String): Boolean {
    val a = versionParts(version)
    val b = versionParts(floor)
    val max = maxOf(a.size, b.size)
    for (i in 0 until max) {
        val ai = a.getOrElse(i) { 0 }
        val bi = b.getOrElse(i) { 0 }
        if (ai != bi) return ai > bi
    }
    return true
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
    maven("https://api.modrinth.com/maven")
}


modstitch {
    minecraftVersion = minecraft
    parchment {
        opt("deps.parchment") { mappingsVersion = it }
    }

    runs {
        register("rpf") {
            client()
        }
    }

    metadata {
        modId = prop("mod.id")
        modName = prop("mod.name")
        modVersion = prop("mod.version")
        modDescription = prop("mod.description")
        modGroup = "com.danrus.rpf"
        modAuthor = prop("mod.author")

        fun MapProperty<String, String>.populate(block: MapProperty<String, String>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put("mcdep", prop("mod.mcdep"))
            put("minecraft_version", prop("deps.mc"))
        }
    }

    loom {
        fabricLoaderVersion = "0.18.4"

        // Configure loom like normal in this block.
        configureLoom {

        }
    }

    mixin {
        addMixinsToModManifest = true

        configs.register("rpf")
        if (versionAtLeast(project.name, "26.1")) {
            configs.register("rpf.rpr")
        }
    }
}

dependencies {
    modstitchModImplementation("net.fabricmc:fabric-loader:${findProperty("deps.fabric")}")
    modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${findProperty("deps.fapi")}")

    // modstitchModCompileOnly
    // modstitchModImplementation

    modstitchModImplementation("maven.modrinth:my_totem_doll:${findProperty("deps.mtd")}")
    modstitchModImplementation("maven.modrinth:rp-renames:${prop("deps.rpr")}")
}

stonecutter {
    replacements {
        string(versionAtLeast(project.name, "26.1")) {
            replace("ClientCommandManager", "ClientCommands")
            replace("MissingBlockModel", "MissingCuboidModel")
        }
        string("block_rename", versionAtLeast(project.name, "26.1")) {
            replace("BlockModelWrapper", "CuboidItemModelWrapper")
        }
        string(versionAtLeast(project.name, "1.21.11")) {
            replace("ResourceLocation", "Identifier")
            replace("ResourceLocationArgument", "IdentifierArgument")
            replace("import net.minecraft.Util;", "import net.minecraft.util.Util;")
        }
        string(versionAtLeast(project.name, "1.21.10")) {
            replace("LivingEntity ", "ItemOwner ")
            replace("LivingEntity;", "ItemOwner;")
        }
    }
}

val artifactVersion = "${prop("mod.version")}-${minecraft}"

publishMods {
    val modrinthToken = findProperty("modrinth-token")
    val curseforgeToken = findProperty("curseforge-token")
    val discordWebhookDR = findProperty("discord-webhook")
    val discordWebhookDRRU = findProperty("discord-webhook-dr-ru")
    val discordWebhookDry = findProperty("discord-webhook-dry")

    dryRun = gitBranchName != mainBranch

    type = STABLE

//    file.set(tasks.named("remapJar").flatMap { (it as org.gradle.jvm.tasks.Jar).archiveFile })
    file = modstitch.finalJarTask.flatMap { it.archiveFile }

    changelog = rootProject.file("CHANGELOG.md").readText()

    val loaders = prop("pub.target.platforms").split(' ')
    loaders.forEach(modLoaders::add)
    displayName = "PRF ${prop("mod.version")} for ${minecraft}"
    version = artifactVersion

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
        discord ("DR freak mods anonuncement (RU)") {
            webhookUrl = discordWebhookDRRU.toString()
            dryRunWebhookUrl = discordWebhookDry.toString()

            username  = prop("mod.name")
            avatarUrl = "https://github.com/Danrus1100/rpf/blob/main/src/main/resources/assets/rpf/icon.png?raw=true"

            content = changelog.map{ "# Версия " + prop("mod.version") + " вышла! \n\n" + rootProject.file("CHANGELOG_RU.md").readText() +"\n\n<@&1426901890582581248>" }
        }
    }
}

publishing {

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "com.danrus"
            artifactId = "rpf"
            version = artifactVersion
        }
    }

    repositories {
        maven {
            name = "Shlakoblock"
            url = uri("https://maven.shlakoblock.com/releases")

            credentials {
                username = project.findProperty("shlakoblock-maven-username")?.toString()
                password = project.findProperty("shlakoblock-maven-password")?.toString()
            }
        }
    }
}