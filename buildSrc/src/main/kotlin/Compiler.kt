import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

object OptIns {
    const val time = "kotlin.time.ExperimentalTime"
    const val contracts = "kotlin.contracts.ExperimentalContracts"
    const val coroutines = "kotlinx.coroutines.ExperimentalCoroutinesApi"

    const val kordInternal = "dev.kord.common.annotation.KordInternal"
    const val kordPreview = "dev.kord.common.annotation.KordPreview"
    const val kordExperimental = "dev.kord.common.annotation.KordExperimental"
    const val kordVoice = "dev.kord.common.annotation.KordVoice"
}

object CompilerArguments {
    val time = OptIns.time.asOptIn()
    val contracts = OptIns.contracts.asOptIn()

    val kordPreview = OptIns.kordPreview.asOptIn()
    val kordExperimental = OptIns.kordExperimental.asOptIn()
    val kordVoice = OptIns.kordVoice.asOptIn()

    const val progressive = "-progressive"
}

private fun String.asOptIn() = "-opt-in=$this"

object Jvm {
    const val target = 8
}

fun KotlinCommonCompilerOptions.applyKordCompilerOptions() {
    allWarningsAsErrors.set(false)
    freeCompilerArgs.add(CompilerArguments.progressive)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.applyKordSourceSetOptions() {
    all {
        languageSettings {
            if ("Test" in name) {
                optIn(OptIns.coroutines)
            }
            optIn(OptIns.kordInternal)
            listOf(
                OptIns.time,
                OptIns.contracts,
                OptIns.kordPreview,
                OptIns.kordExperimental,
                OptIns.kordVoice,
            ).forEach(::optIn)
        }
    }
}
