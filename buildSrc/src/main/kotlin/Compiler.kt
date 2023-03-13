import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

object CompilerArguments {
    const val time = "-opt-in=kotlin.time.ExperimentalTime"
    const val contracts = "-opt-in=kotlin.contracts.ExperimentalContracts"

    const val kordPreview = "-opt-in=dev.kord.common.annotation.KordPreview"
    const val kordExperimental = "-opt-in=dev.kord.common.annotation.KordExperimental"
    const val kordVoice = "-opt-in=dev.kord.common.annotation.KordVoice"

    const val progressive = "-progressive"
}

object Jvm {
    const val target = 8
}

fun KotlinJvmCompilerOptions.applyKordCompilerOptions() {
    allWarningsAsErrors = true
    freeCompilerArgs.add(CompilerArguments.progressive)
}
