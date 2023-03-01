package dev.kord.test

import io.ktor.utils.io.*

expect fun getEnv(name: String): String?
expect suspend fun file(project: String, path: String): String
expect suspend fun readFile(project: String, path: String): ByteReadChannel
