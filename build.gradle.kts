plugins {
    kotlin("jvm") version Dependency.Kotlin.Version
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}
repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public")
    Dependency.Repositories.forEach {
        maven(it)
    }
}
dependencies {
    paperweight.paperDevBundle("${Dependency.Paper.Version}-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:${Dependency.Paper.Version}-R0.1-SNAPSHOT")
    Dependency.Dependencies.forEach {
        compileOnly(it)
    }
}
bukkit {
    name = rootProject.name
    version = "0.0.1"
    apiVersion = Dependency.Paper.APIVersion
    main = "io.github.xsiet.${rootProject.name.lowercase()}.${rootProject.name}Plugin"
    libraries = Dependency.Libraries
}
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
        filteringCharset = Charsets.UTF_8.toString()
    }
    register<Copy>("paperJar") {
        from(reobfJar)
        val pluginDir = File(".server/plugins")
        val updateDir = File(pluginDir, "update")
        into(if (File(pluginDir, "${rootProject.name}-unspecified.jar").exists()) updateDir else pluginDir)
        doLast {
            File(updateDir, "RELOAD").delete()
        }
    }
}