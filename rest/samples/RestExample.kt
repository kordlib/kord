package dev.kord.rest

import dev.kord.rest.ratelimit.ExclusionRequestHandler
import dev.kord.rest.service.RestClient

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val client = RestClient(ExclusionRequestHandler(token))

    val username = client.user.getCurrentUser().username
    println("using $username's token")
}