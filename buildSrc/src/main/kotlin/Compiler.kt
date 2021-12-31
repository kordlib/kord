object CompilerArguments {
    const val coroutines = "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    const val time = "-Xopt-in=kotlin.time.ExperimentalTime"
    const val stdLib = "-Xopt-in=kotlin.ExperimentalStdlibApi"
    const val optIn = "-Xopt-in=kotlin.RequiresOptIn"
    const val contracts = "-Xopt-in=kotlin.contracts.ExperimentalContracts"
}

object Jvm {
    const val target = "1.8"
}
