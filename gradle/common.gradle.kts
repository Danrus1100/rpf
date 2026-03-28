import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withGroovyBuilder

fun opt(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

fun prop(name: String): String =
    findProperty(name)?.toString() ?: throw IllegalArgumentException("Missing property: $name")

fun propExists(name: String) = project.properties.containsKey(name)

val mainBranch = "multiversion"
val gitBranchName = providers.exec {
    commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
}.standardOutput.asText.map { it.trim() }.get()
val minecraft = property("deps.mc") as String

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


repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org")
    maven("https://api.modrinth.com/maven")
}

extensions.findByName("stonecutter")?.withGroovyBuilder {
    "replacements" {
        "string" {
            setProperty("direction", versionAtLeast(project.name, "26.1"))
            "replace"("ClientCommandManager", "ClientCommands")
            "replace"("MissingBlockModel", "MissingCuboidModel")
        }
        "string"(versionAtLeast(project.name, "26.1"), "block_rename") {
            "replace"("BlockModelWrapper", "CuboidItemModelWrapper")
        }
        "string" {
            setProperty("direction", versionAtLeast(project.name, "1.21.11"))
            "replace"("ResourceLocation", "Identifier")
            "replace"("ResourceLocationArgument", "IdentifierArgument")
            "replace"("import net.minecraft.Util;", "import net.minecraft.util.Util;")
        }
        "string" {
            setProperty("direction", versionAtLeast(project.name, "1.21.10"))
            "replace"("LivingEntity ", "ItemOwner ")
            "replace"("LivingEntity;", "ItemOwner;")
        }
    }

    // Used by some source conditionals.
    @Suppress("UNCHECKED_CAST")
    val constants = getProperty("constants") as? MutableMap<String, Any?>
    constants?.set("rprenames", propExists("deps.rprenames"))
}


plugins.withId("java") {
    tasks.named<ProcessResources>("processResources") {
        val accessWidenerFileName = if (plugins.hasPlugin("net.fabricmc.fabric-loom-remap")) {
            "rpf.accesswidener"
        } else {
            "rpf_unobfuscated.accesswidener"
        }

        // Бонус: избавляемся от дублирования inputs.property / map
        val map = mapOf(
            "id"                to findProperty("mod.id"),
            "name"              to findProperty("mod.name"),
            "version"           to findProperty("mod.version"),
            "mcdep"             to findProperty("mod.mcdep"),
            "minecraft_version" to findProperty("deps.mc"),
            "description"       to findProperty("mod.description"),
            "author"            to findProperty("mod.author"),
            "aw"                to accessWidenerFileName
        )
        map.forEach { (key, value) -> inputs.property(key, value) }
        filesMatching("fabric.mod.json") { expand(map) }
    }
}

plugins.withId("base") {
    extensions.configure<BasePluginExtension> {
        archivesName.set(findProperty("mod.id") as String)
    }
}

val artifactVersion = "${prop("mod.version")}-${minecraft}"

plugins.withId("me.modmuss50.mod-publish-plugin") {
    // In applied Kotlin scripts (apply(from=...)) we don't have type-safe accessors like `publishMods {}`.
    // Configure the extension by name to keep this file reusable across projects.
    val publishModsExt = extensions.getByName("publishMods")
    publishModsExt.withGroovyBuilder {
            val modrinthToken     = findProperty("modrinth-token")
            val curseforgeToken   = findProperty("curseforge-token")
            val discordWebhookDR  = findProperty("discord-webhook")
            val discordWebhookRU  = findProperty("discord-webhook-dr-ru")
            val discordWebhookDry = findProperty("discord-webhook-dry")

            setProperty("dryRun", gitBranchName != mainBranch)
            setProperty("type", "STABLE")
            setProperty("changelog", rootProject.file("CHANGELOG.md").readText())
            setProperty("version", artifactVersion)

            val loaders = prop("pub.target.platforms").split(' ')
            val targets = prop("pub.target.versions").split(' ')
            val modLoaders = getProperty("modLoaders")
            (modLoaders as? MutableCollection<Any?>)?.let { coll -> loaders.forEach(coll::add) }

            setProperty("displayName", "PRF ${prop("mod.version")} for ${minecraft}")

            "modrinth" {
                setProperty("projectId", prop("publish.modrinth"))
                setProperty("accessToken", modrinthToken.toString())
                val minecraftVersions = getProperty("minecraftVersions")
                (minecraftVersions as? MutableCollection<Any?>)?.let { coll -> targets.forEach(coll::add) }
            }

            "curseforge" {
                setProperty("projectId", prop("publish.curseforge"))
                setProperty("accessToken", curseforgeToken.toString())
                setProperty("projectSlug", prop("pub.slug"))
                val minecraftVersions = getProperty("minecraftVersions")
                (minecraftVersions as? MutableCollection<Any?>)?.let { coll -> targets.forEach(coll::add) }
            }

            if (targets.contains("1.21.8") && loaders.contains("fabric")) {
                val avatarUrl = "https://github.com/Danrus1100/rpf/blob/main/src/main/resources/assets/rpf/icon.png?raw=true"

                "discord"("DR freak mods anonuncement") {
                    setProperty("webhookUrl", discordWebhookDR.toString())
                    setProperty("dryRunWebhookUrl", discordWebhookDry.toString())
                    setProperty("username", prop("mod.name"))
                    setProperty("avatarUrl", avatarUrl)
                    setProperty(
                        "content",
                        listOf("# ${prop("mod.version")} version here!\n\n${rootProject.file("CHANGELOG.md").readText()}\n\n<@&1426901890582581248>")
                    )
                }

                "discord"("DR freak mods anonuncement (RU)") {
                    setProperty("webhookUrl", discordWebhookRU.toString())
                    setProperty("dryRunWebhookUrl", discordWebhookDry.toString())
                    setProperty("username", prop("mod.name"))
                    setProperty("avatarUrl", avatarUrl)
                    setProperty(
                        "content",
                        listOf("# Версия ${prop("mod.version")} вышла!\n\n${rootProject.file("CHANGELOG_RU.md").readText()}\n\n<@&1426901890582581248>")
                    )
                }
            }
    }
}

plugins.withId("maven-publish") {
    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId    = "com.danrus"
                artifactId = "rpf"
                version    = artifactVersion
            }
        }
        repositories {
            maven {
                name = "Shlakoblock"
                url  = uri("https://maven.shlakoblock.com/releases")
                credentials {
                    username = findProperty("shlakoblock-maven-username")?.toString()
                    password = findProperty("shlakoblock-maven-password")?.toString()
                }
            }
        }
    }
}

version = "${findProperty("mod.version")}-${findProperty("deps.mc")}"
