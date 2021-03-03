package dev.kord.core

import dev.kord.core.builder.kord.Shards
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intents
import io.ktor.client.*

class ClientResources(
    val token: String,
    val shards: Shards,
    val httpClient: HttpClient,
    val defaultStrategy: EntitySupplyStrategy<*>,
    val intents: Intents,
) {
    override fun toString(): String {
        return "ClientResources(shards=$shards, httpClient=$httpClient, defaultStrategy=$defaultStrategy, intents=$intents)"
    }
}
