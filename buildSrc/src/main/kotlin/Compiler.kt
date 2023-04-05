import kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

object OptIns {
    const val coroutines = "kotlinx.coroutines.ExperimentalCoroutinesApi"
}

val kordOptIns = listOf(
    "kotlin.time.ExperimentalTime",
    "kotlin.contracts.ExperimentalContracts",

    "dev.kord.common.annotation.KordInternal",
    "dev.kord.common.annotation.KordPreview",
    "dev.kord.common.annotation.KordExperimental",
    "dev.kord.common.annotation.KordVoice",
)

object Jvm {
    const val target = 8
}

fun KotlinCommonCompilerOptions.applyKordCompilerOptions() {
    // TODO: set to true again once https://github.com/Kotlin/kotlinx-atomicfu/issues/289 is fixed
    allWarningsAsErrors.set(false)
    freeCompilerArgs.add("-progressive")
}

fun KotlinSourceSet.applyKordOptIns() {
    languageSettings {
        if ("Test" in name) optIn(OptIns.coroutines)
        kordOptIns.forEach(::optIn)
    }
}

fun Project.configureAtomicFU() {
    // https://github.com/Kotlin/kotlinx-atomicfu/issues/210
    configure<AtomicFUPluginExtension> {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependenciesVersion = libs.findVersion("kotlinx-atomicfu").get().requiredVersion
    }
}
