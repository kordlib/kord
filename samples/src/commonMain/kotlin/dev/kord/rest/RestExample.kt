package dev.kord.rest

import dev.kord.rest.service.RestClient
import io.ktor.client.*

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val rest = RestClient(HttpClient())

    val username = rest.user.getCurrentUser().username
    println("using $username's token")
}
