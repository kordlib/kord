@file:OptIn(KordInternal::class)

package dev.kord.test

import dev.kord.common.Platform
import dev.kord.common.annotation.KordInternal
import io.ktor.utils.io.*
import js.core.get
import node.process.process

actual fun getEnv(name: String) = process.env[name]

actual suspend fun file(project: String, path: String): String =
    if (Platform.IS_NODE) nodeFile(project, path) else TODO("Browser JS is not supported yet")

actual suspend fun readFile(project: String, path: String): ByteReadChannel =
    if (Platform.IS_NODE) nodeReadFile(project, path) else TODO("Browser JS is not supported yet")
