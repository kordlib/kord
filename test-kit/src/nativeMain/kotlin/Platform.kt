@file:OptIn(ExperimentalNativeApi::class)

package dev.kord.test

import io.ktor.utils.io.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.BufferedSource
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

private val darwinFamilies = listOf(OsFamily.WATCHOS, OsFamily.TVOS, OsFamily.MACOSX)

actual object Platform {
    actual val IS_JVM: Boolean = false
    actual val IS_NODE: Boolean = false
    actual val IS_BROWSER: Boolean = false
    actual val IS_MINGW: Boolean = Platform.osFamily == OsFamily.WINDOWS
    actual val IS_LINUX: Boolean = Platform.osFamily == OsFamily.LINUX
    actual val IS_DARWIN: Boolean = Platform.osFamily in darwinFamilies
}

@OptIn(ExperimentalForeignApi::class)
actual fun getEnv(name: String) = getenv(name)?.toKString()

private fun actutalPath(path: String) =
    "src/commonTest/resources/$path".toPath()

actual suspend fun file(project: String, path: String): String = read(path, BufferedSource::readUtf8)

actual suspend fun readFile(project: String, path: String): ByteReadChannel =
    read(path) { ByteReadChannel(readByteArray()) }

private inline fun <T> read(path: String, readerAction: BufferedSource.() -> T): T {
    val actualPath = actutalPath(path)
    return try {
        FileSystem.SYSTEM.read(actualPath, readerAction)
    } catch (e: Throwable) {
        val pwd = FileSystem.SYSTEM.canonicalize(".".toPath())
        val absolutePath = pwd / actualPath
        throw FileNotFoundException(absolutePath.toString(), e)
    }
}

class FileNotFoundException(absolutePath: String, cause: Throwable) : IOException("Absolute Path: $absolutePath", cause)
