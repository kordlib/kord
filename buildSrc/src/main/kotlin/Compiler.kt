import org.gradle.api.NamedDomainObjectSet
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

val kordOptIns = listOf(
    "kotlin.contracts.ExperimentalContracts",

    "dev.kord.common.annotation.KordInternal",
    "dev.kord.common.annotation.KordPreview",
    "dev.kord.common.annotation.KordExperimental",
    "dev.kord.common.annotation.KordVoice",
)

internal fun KotlinCommonCompilerOptions.applyKordCommonCompilerOptions() {
    allWarningsAsErrors = true
    progressiveMode = true
    extraWarnings = true
    freeCompilerArgs.add("-Xexpect-actual-classes")
}

internal const val KORD_JVM_TARGET = 8

internal fun KotlinJvmCompilerOptions.applyKordJvmCompilerOptions() {
    applyKordCommonCompilerOptions()
    jvmTarget = JVM_1_8
    freeCompilerArgs.add("-Xjdk-release=1.8")
}

internal fun NamedDomainObjectSet<KotlinSourceSet>.applyKordTestOptIns() {
    named { it.contains("test", ignoreCase = true) }.configureEach {
        // allow `ExperimentalCoroutinesApi` for `TestScope.currentTime`
        languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }
}
