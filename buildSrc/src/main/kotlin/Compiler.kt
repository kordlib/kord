import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

object OptIns {
    const val coroutines = "kotlinx.coroutines.ExperimentalCoroutinesApi"
}

val kordOptIns = listOf(
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
    allWarningsAsErrors = true
    progressiveMode = true
    freeCompilerArgs.add("-Xexpect-actual-classes")
}

fun KotlinSourceSet.applyKordOptIns() {
    languageSettings {
        // allow `ExperimentalCoroutinesApi` for `TestScope.currentTime`
        if ("Test" in name) optIn(OptIns.coroutines)
        kordOptIns.forEach(::optIn)
    }
}
