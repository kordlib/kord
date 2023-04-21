package dev.kord.core

import dev.kord.common.entity.Snowflake
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.builder.Shards
import io.ktor.client.*

public class ClientResources(
    public val token: String,
    public val applicationId: Snowflake,
    public val shards: Shards,
    public val maxConcurrency: Int,
    public val httpClient: HttpClient,
    public val defaultStrategy: EntitySupplyStrategy<*>,
) {
    override fun toString(): String = "ClientResources(token=hunter2, applicationId=$applicationId, shards=$shards, " +
            "maxConcurrency=$maxConcurrency, httpClient=$httpClient, defaultStrategy=$defaultStrategy)"
}
