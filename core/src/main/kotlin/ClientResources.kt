package dev.kord.core

import dev.kord.common.entity.Snowflake
import dev.kord.core.builder.kord.Shards
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intents
import io.ktor.client.*

public class ClientResources(
    public val token: String,
    public val applicationId: Snowflake,
    public val shards: Shards,
    public val httpClient: HttpClient,
    public val defaultStrategy: EntitySupplyStrategy<*>,
    public val intents: Intents,
) {
    override fun toString(): String {
        return "ClientResources(shards=$shards, httpClient=$httpClient, defaultStrategy=$defaultStrategy, intents=$intents)"
    }
}
