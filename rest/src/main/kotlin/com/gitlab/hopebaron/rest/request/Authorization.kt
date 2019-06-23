package com.gitlab.hopebaron.rest.request

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header

interface Authorization {
    fun HttpRequestBuilder.apply()
}

class BotAuthorization(private val token: String) : Authorization {
    override fun HttpRequestBuilder.apply() {
        header("Authorization", "Bot $token")
    }
}
