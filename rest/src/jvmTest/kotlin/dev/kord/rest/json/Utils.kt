package dev.kord.rest.json

import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*

internal actual suspend fun file(name: String): String = ClassLoader.getSystemResource("json/$name.json")!!.readText()
internal actual suspend fun readFile(name: String): ByteReadChannel =
    ClassLoader.getSystemResourceAsStream(name)!!.toByteReadChannel()
