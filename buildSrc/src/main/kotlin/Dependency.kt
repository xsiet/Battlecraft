object Dependency {
    object Kotlin {
        const val Version = "1.9.0"
    }
    object Paper {
        const val Version = "1.20.1"
        const val APIVersion = "1.20"
    }
    private const val KotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Kotlin.Version}"
    private object Kommand {
        const val Version = "3.1.6"
    }
    val Repositories = listOf(
        "https://repo.codemc.io/repository/maven-public"
    )
    val Dependencies = listOf(
        KotlinStdlib,
        "io.github.monun:kommand-api:${Kommand.Version}",
        "org.popcraft:chunky-common:1.3.38"
    )
    val Libraries = listOf(
        KotlinStdlib,
        "io.github.monun:kommand-core:${Kommand.Version}"
    )
}