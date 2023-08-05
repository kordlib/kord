package dev.kord.test

import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*

actual object Platform {
    actual const val IS_JVM: Boolean = true
    actual const val IS_NODE: Boolean = false
    actual val IS_BROWSER: Boolean = false
    actual val IS_MINGW: Boolean = false
    actual val IS_LINUX: Boolean = false
    actual val IS_DARWIN: Boolean = false
}

actual fun getEnv(name: String): String? = System.getenv(name)
actual suspend fun file(project: String, path: String): String = ClassLoader.getSystemResource(path).readText()
actual suspend fun readFile(project: String, path: String): ByteReadChannel =
    ClassLoader.getSystemResourceAsStream(path)!!.toByteReadChannel()
