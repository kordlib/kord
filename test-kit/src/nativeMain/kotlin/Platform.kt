@file:OptIn(ExperimentalNativeApi::class)

package dev.kord.test

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
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
