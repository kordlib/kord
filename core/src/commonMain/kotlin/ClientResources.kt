package dev.kord.core

import dev.kord.common.entity.Snowflake
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.builder.Shards
import io.ktor.client.*

/**
 * The resources for the Kord Instance.
 *
 * @param token The Bots token
 * @param applicationId The ID of the application
 * @param shards The [Shards] for the application
 * @param maxConcurrency The maximum concurrency for the bot. Can be obtained by calling the `Route.GatewayBotGet` endpoint.
 * @param httpClient The [HttpClient] the client is connected through
 * @param defaultStrategy The default [EntitySupplyStrategy] for the client.
 */
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
