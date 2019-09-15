package com.gitlab.kordlib.core

import io.ktor.client.HttpClient

class ClientResources(
        val token: String,
        val shardCount: Int,
        val httpClient: HttpClient
)