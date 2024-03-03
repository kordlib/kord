package dev.kord.test

import io.ktor.utils.io.*
import node.buffer.BufferEncoding
import node.fs.readFile
import node.process.process

internal suspend fun nodeFile(project: String, path: String): String =
    readFile("${process.env["PROJECT_ROOT"]}/$project/src/commonTest/resources/$path", BufferEncoding.utf8)

internal suspend fun nodeReadFile(project: String, path: String) = ByteReadChannel(nodeFile(project, path))
