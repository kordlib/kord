package dev.kord.core

import dev.kord.common.entity.Snowflake
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.builder.Shards
import io.ktor.client.*
import kotlin.DeprecationLevel.WARNING

public class ClientResources(
    public val token: String,
    public val applicationId: Snowflake,
    public val shards: Shards,
    public val maxConcurrency: Int,
    public val httpClient: HttpClient,
    public val defaultStrategy: EntitySupplyStrategy<*>,
) {
    @Deprecated(
        "Specify maxConcurrency. It can be obtained by calling the Route.GatewayBotGet endpoint.",
        ReplaceWith(
            "ClientResources(token, applicationId, shards, maxConcurrency = 1 /* can be obtained by calling the " +
                    "Route.GatewayBotGet endpoint */, httpClient, defaultStrategy)"
        ),
        level = WARNING,
    )
    public constructor(
        token: String,
        applicationId: Snowflake,
        shards: Shards,
        httpClient: HttpClient,
        defaultStrategy: EntitySupplyStrategy<*>,
    ) : this(token, applicationId, shards, maxConcurrency = 1, httpClient, defaultStrategy)

    override fun toString(): String = "ClientResources(token=hunter2, applicationId=$applicationId, shards=$shards, " +
            "maxConcurrency=$maxConcurrency, httpClient=$httpClient, defaultStrategy=$defaultStrategy)"
}
