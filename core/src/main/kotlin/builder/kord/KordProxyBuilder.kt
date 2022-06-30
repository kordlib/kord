package dev.kord.core.builder.kord

import dev.kord.cache.api.DataCache
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Gateway
import dev.kord.gateway.builder.Shards
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * The rest only Kord builder. You probably want to invoke the [DSL builder][Kord.restOnly] instead.
 */
@KordExperimental
public class KordProxyBuilder: RestOnlyBuilder() {

    public override fun build(): Kord {
        val client = httpClient.configure()

        val resources = ClientResources(
            "",
            applicationId ?: Snowflake.min,
            Shards(0),
            client,
            EntitySupplyStrategy.rest,
        )
        val rest = RestClient(handlerBuilder(resources))
        val selfId = Snowflake.min

        return Kord(
            resources,
            DataCache.none(),
            DefaultMasterGateway(mapOf(0 to Gateway.none())),
            rest,
            selfId,
            MutableSharedFlow(),
            defaultDispatcher
        )
    }
}
