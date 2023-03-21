package dev.kord.core

suspend fun main(args: Array<String>) {
    val kord = Kord(args.firstOrNull() ?: error("token required")) {
        enableShutdownHook = true
    }

    kord.login()
}
