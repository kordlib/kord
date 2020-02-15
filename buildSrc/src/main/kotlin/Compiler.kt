object CompilerArguments {
    const val inlineClasses = "-XXLanguage:+InlineClasses"
    const val coroutines = "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
    const val time = "-Xuse-experimental=kotlin.time.ExperimentalTime"
}

object Jvm {
    const val target = "1.8"
}