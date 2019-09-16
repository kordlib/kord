package com.gitlab.kordlib.rest

import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestHandler
import com.gitlab.kordlib.rest.service.RestClient

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val client = RestClient(ExclusionRequestHandler(token))

    val username = client.user.getCurrentUser().username
    println("using $username's token")
}