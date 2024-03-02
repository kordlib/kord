package dev.kord.test

import io.ktor.utils.io.*

actual object Platform {
    actual const val IS_JVM: Boolean = false
    actual val IS_NODE: Boolean
        get() = js(
            "typeof process !== 'undefined' && process.versions != null && process.versions.node != null"
        ) as Boolean
}

actual fun getEnv(name: String) = js("process.env[name]") as String?

actual suspend fun file(project: String, path: String): String =
    if (Platform.IS_NODE) nodeFile(project, path) else TODO("Browser JS is not supported yet")

actual suspend fun readFile(project: String, path: String): ByteReadChannel =
    if (Platform.IS_NODE) nodeReadFile(project, path) else TODO("Browser JS is not supported yet")
