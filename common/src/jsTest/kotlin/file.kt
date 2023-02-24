package dev.kord.common

import node.fs.readFile as nodeReadFile

actual suspend fun readFile(prefix: String, name: String): String =
    nodeReadFile("common/commonTest/resources/json/$prefix/$name.json")
        .toString()
