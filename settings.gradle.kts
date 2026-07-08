pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
		maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		exclusiveContent {
			forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
			filter { includeGroup("maven.modrinth") }
		}
	}
	includeBuild("build-logic")
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9"
}

val settingsRootDir = rootDir

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) {
			loaders.forEach { loader ->
				val buildscriptName = when {
					version.startsWith("1.") && loader == "fabric" -> "build.fabric-legacy.gradle.kts"
					else -> "build.$loader.gradle.kts"
				}

				version("$version-$loader", version).buildscript = buildscriptName
			}
		}

		fun env(variable: String): String? {
			val value = System.getenv(variable)
			if (value != null) return value

			val envFile = java.io.File(settingsRootDir, ".env")
			if (envFile.exists()) {
				val props = java.util.Properties()
				envFile.inputStream().use { props.load(it) }
				val fromFile = props.getProperty(variable)
				if (fromFile != null) return fromFile
			}

			return null
		}

		if (env("GRADLE_TEST") == "true") {
			match("26.2", "fabric")
		}
		else if (env("GRADLE_ONLY_IMPORTANT_FABRIC") == "true") {
			// Main Fabric versions, this is the recommended setting for development
			match("26.2", "fabric")
		}
		else if (env("GRADLE_ONLY_FABRIC") == "true") {
			// All Fabric versions
			match("26.2", "fabric")
		}
		else if (env("GRADLE_ONLY_FORGE") == "true") {
			match("26.2", "forge")
		}
		else if (env("GRADLE_ONLY_NEOFORGE") == "true") {
			match("26.2", "neoforge")
		}
		else {
			// All versions
			match("26.2", "fabric", "forge", "neoforge")
		}

		if (env("GRADLE_TEST") == "true") {
			vcsVersion = "26.2-fabric"
		}
		else if (env("GRADLE_ONLY_FORGE") == "true") {
			vcsVersion = "26.2-forge"
		}
		else if (env("GRADLE_ONLY_NEOFORGE") == "true") {
			vcsVersion = "26.2-neoforge"
		}
		else {
			vcsVersion = "26.2-fabric"
		}
	}
}
