plugins {
	id("mod-platform")
	id("net.fabricmc.fabric-loom")
}

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = prop("deps.minecraft")
		}
		required("fabricloader") {
			versionRange = ">=${libs.fabric.loader.get().version}"
		}
	}
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.accesswidener")
	val isJbr = System.getProperty("java.vendor")?.contains("JetBrains", ignoreCase = true) == true
	runs.named("client") {
		client()
		ideConfigGenerated(false)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Player")
		configName = "Fabric Client"
		if (isJbr) vmArg("-XX:+AllowEnhancedClassRedefinition")
	}
	runs.named("server") {
		server()
		ideConfigGenerated(false)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
		if (isJbr) vmArg("-XX:+AllowEnhancedClassRedefinition")
	}
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")

	implementation(libs.fabric.loader)
}

project.afterEvaluate {
	val mixinJarPath = configurations.compileClasspath.get().files
		.firstOrNull { it.name.contains("sponge-mixin") || (it.name.contains("mixin") && !it.name.contains("fabric-mixin-compile-extensions")) }
		?.absolutePath

	if (mixinJarPath != null) {
		loom {
			runs.named("client") {
				vmArg("-javaagent:$mixinJarPath")
			}
			runs.named("server") {
				vmArg("-javaagent:$mixinJarPath")
			}
		}
	}
}
