package dev.kord.core

import dev.kord.common.entity.Snowflake
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intents
import dev.kord.gateway.builder.Shards
import io.ktor.client.*

class ClientResources(
    val token: String,
    val applicationId: Snowflake,
    val shards: Shards,
    val httpClient: HttpClient,
    val defaultStrategy: EntitySupplyStrategy<*>,
) {
    override fun toString(): String {
        return "ClientResources(shards=$shards, httpClient=$httpClient, defaultStrategy=$defaultStrategy)"
    }
}
