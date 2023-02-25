package dev.kord.gateway

internal actual fun getEnv(name: String): String? = System.getenv(name)

internal actual suspend fun readFile(prefix: String, name: String): String =
    ClassLoader.getSystemResource("json/$prefix/$name.json").readText()
