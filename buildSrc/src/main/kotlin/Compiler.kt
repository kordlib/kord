object CompilerArguments {
    const val coroutines = "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    const val time = "-opt-in=kotlin.time.ExperimentalTime"
    const val stdLib = "-opt-in=kotlin.ExperimentalStdlibApi"
    const val contracts = "-opt-in=kotlin.contracts.ExperimentalContracts"
}

object Jvm {
    const val target = "1.8"
}
