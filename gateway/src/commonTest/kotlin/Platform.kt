package dev.kord.gateway

internal expect fun getEnv(name: String): String?

internal expect suspend fun readFile(prefix: String, name: String): String
