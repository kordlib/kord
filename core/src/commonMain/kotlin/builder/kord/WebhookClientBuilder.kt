package dev.kord.core.builder.kord

import dev.kord.cache.api.DataCache
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.WebhookClient
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.GatewayEventInterceptor
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Gateway
import dev.kord.gateway.builder.Shards
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Builder for [WebhookClient]
 *
 * @see Kord.webhookClient
 */
public class WebhookClientBuilder : AbstractKordBuilder() {
    public fun build(): WebhookClient {
        val client = httpClient.configure()

        // Neither token nor applicationId is used in WebhookClient, so we can assume
        // that this won't cause problems
        val resources = ClientResources(
            "",
            Snowflake(-1),
            Shards(0),
            maxConcurrency = 1,
            client,
            EntitySupplyStrategy.rest,
        )

        val rest = RestClient(buildRequestHandler(resources))

        return Kord(
            resources = resources,
            cache = @OptIn(ExperimentalCoroutinesApi::class) DataCache.none(),
            gateway = DefaultMasterGateway(mapOf(0 to Gateway.none())),
            rest = rest,
            selfId = resources.applicationId,
            eventFlow = MutableSharedFlow(),
            dispatcher = defaultDispatcher,
            interceptor = GatewayEventInterceptor.none(),
        )
    }
}
