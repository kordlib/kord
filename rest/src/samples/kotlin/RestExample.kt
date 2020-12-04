package dev.kord.rest

import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val client = RestClient(KtorRequestHandler(token))

    val username = client.user.getCurrentUser().username
    println("using $username's token")
}