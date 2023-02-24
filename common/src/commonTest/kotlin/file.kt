package dev.kord.common

expect suspend fun readFile(prefix: String, name: String): String
