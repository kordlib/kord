package dev.kord.common

actual suspend inline fun readFile(prefix: String, name: String): String =
    ClassLoader.getSystemResource("json/$prefix/$name.json")!!.readText()
