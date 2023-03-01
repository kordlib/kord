package dev.kord.test

import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*

actual fun getEnv(name: String) = System.getenv(name)
actual suspend fun file(project: String, path: String): String = ClassLoader.getSystemResource(path).readText()
actual suspend fun readFile(project: String, path: String): ByteReadChannel =
    ClassLoader.getSystemResourceAsStream(path)!!.toByteReadChannel()
