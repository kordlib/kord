package dev.kord.rest.json

import io.ktor.utils.io.*
import dev.kord.test.file as platformFile
import dev.kord.test.readFile as platformReadFile

internal suspend fun file(name: String): String = platformFile("rest", "json/$name.json")
internal suspend fun readFile(name: String): ByteReadChannel = platformReadFile("rest", name)
