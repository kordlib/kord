package dev.kord.gateway

import js.core.get
import node.buffer.BufferEncoding
import node.process.process

internal actual fun getEnv(name: String) = process.env[name]

actual suspend fun readFile(prefix: String, name: String): String =
    node.fs.readFile("${process.env["PROJECT_ROOT"]}/gateway/src/commonTest/resources/json/$prefix/$name.json")
        .toString(BufferEncoding.utf8)
