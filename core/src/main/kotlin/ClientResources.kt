package dev.kord.core

import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intents
import io.ktor.client.HttpClient

class ClientResources(
        val token: String,
        val shardCount: Int,
        val httpClient: HttpClient,
        val defaultStrategy: EntitySupplyStrategy<*>,
        val intents: Intents,
        val applicationId: String? = null
) {
    override fun toString(): String {
        return "ClientResources(shardCount=$shardCount, httpClient=$httpClient, defaultStrategy=$defaultStrategy, intents=$intents)"
    }
}