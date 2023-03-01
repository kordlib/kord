package dev.kord.common

import dev.kord.test.file
suspend fun readFile(prefix: String, name: String): String =
    file("common", "json/$prefix/$name.json")
