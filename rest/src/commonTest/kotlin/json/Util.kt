package dev.kord.rest.json

import io.ktor.utils.io.*

internal expect suspend fun file(name: String): String
internal expect suspend fun readFile(name: String): ByteReadChannel
