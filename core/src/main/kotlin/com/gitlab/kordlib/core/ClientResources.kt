package com.gitlab.kordlib.core

import io.ktor.client.HttpClient

class ClientResources(
        val token: String,
        val shardIndex: Int,
        val shardCount: Int,
        val httpClient: HttpClient
)