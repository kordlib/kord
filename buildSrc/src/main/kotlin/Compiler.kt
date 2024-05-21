import org.gradle.api.NamedDomainObjectSet
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

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

internal fun NamedDomainObjectSet<KotlinSourceSet>.applyKordTestOptIns() {
    named { it.contains("test", ignoreCase = true) }.configureEach {
        // allow `ExperimentalCoroutinesApi` for `TestScope.currentTime`
        languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }
}
