package dev.kord.test

import io.ktor.utils.io.*

expect object Platform {
    val IS_JVM: Boolean
    val IS_NODE: Boolean
    val IS_BROWSER: Boolean
}

expect fun getEnv(name: String): String?
expect suspend fun file(project: String, path: String): String
expect suspend fun readFile(project: String, path: String): ByteReadChannel
