package dev.kord.common

import js.core.get
import node.buffer.BufferEncoding
import node.process.process
import node.fs.readFile as nodeReadFile

actual suspend fun readFile(prefix: String, name: String): String =
    nodeReadFile("${process.env["PROJECT_ROOT"]}/common/src/commonTest/resources/json/$prefix/$name.json")
        .toString(BufferEncoding.utf8)
