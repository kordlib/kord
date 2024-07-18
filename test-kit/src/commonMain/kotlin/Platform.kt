package dev.kord.test

import io.ktor.utils.io.*

expect object Platform {
    val IS_JVM: Boolean
    val IS_NODE: Boolean
    val IS_BROWSER: Boolean
    val IS_MINGW: Boolean
    val IS_LINUX: Boolean
    val IS_DARWIN: Boolean
}

expect fun getEnv(name: String): String?
expect suspend fun file(project: String, path: String): String
suspend fun readFile(project: String, path: String) = readFile0(project, path).counted()
expect suspend fun readFile0(project: String, path: String): ByteReadChannel
