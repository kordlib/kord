import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

object CompilerArguments {
    const val time = "-opt-in=kotlin.time.ExperimentalTime"
    const val contracts = "-opt-in=kotlin.contracts.ExperimentalContracts"

    const val kordPreview = "-opt-in=dev.kord.common.annotation.KordPreview"
    const val kordExperimental = "-opt-in=dev.kord.common.annotation.KordExperimental"
    const val kordVoice = "-opt-in=dev.kord.common.annotation.KordVoice"

    const val progressive = "-progressive"
}

object Jvm {
    // keep these equivalent
    const val targetString = "1.8"
    const val targetInt = 8
}

fun KotlinJvmOptions.kordJvmOptions() {
    jvmTarget = Jvm.target
    allWarningsAsErrors = true
    freeCompilerArgs += CompilerArguments.progressive
}
