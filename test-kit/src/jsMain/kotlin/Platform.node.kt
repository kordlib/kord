package dev.kord.test

import io.ktor.utils.io.*
import js.iterable.toList
import node.buffer.Buffer
import node.buffer.BufferEncoding
import node.process.process

internal suspend fun nodeFile(project: String, path: String): String =
    node.fs.readFile("${process.env["PROJECT_ROOT"]}/$project/src/commonTest/resources/$path")
        .toString(BufferEncoding.utf8)

internal suspend fun nodeReadFile(project: String, path: String): ByteReadChannel {
    val buffer = node.fs.readFile("${process.env["PROJECT_ROOT"]}/$project/src/commonTest/resources/$path")

    return ByteReadChannel(buffer.toByteArray())
}

private fun Buffer.toByteArray() = values().toList().toByteArray()
