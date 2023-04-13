package dev.kord.rest

import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val rest = RestClient(KtorRequestHandler(token))

    val username = rest.user.getCurrentUser().username
    println("using $username's token")
}
