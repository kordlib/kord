package dev.kord.rest.json

import io.ktor.utils.io.*
import js.core.get
import js.core.toList
import node.buffer.Buffer
import node.buffer.BufferEncoding
import node.process.process
import node.fs.readFile as nodeReadFile

internal actual suspend fun file(name: String): String =
    nodeReadFile("${process.env["PROJECT_ROOT"]}/rest/src/commonTest/resources/json/$name.json")
        .toString(BufferEncoding.utf8)

internal actual suspend fun readFile(name: String): ByteReadChannel {
    val buffer = nodeReadFile("${process.env["PROJECT_ROOT"]}/rest/src/commonTest/resources/$name")

    return ByteReadChannel(buffer.toByteArray())
}

private fun Buffer.toByteArray() = values().toList()
    .map(Int::toByte)
    .toByteArray()
