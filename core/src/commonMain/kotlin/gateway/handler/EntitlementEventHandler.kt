package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.kord.common.entity.DiscordEntitlement
import dev.kord.core.Kord
import dev.kord.core.cache.data.EntitlementData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.monetization.Entitlement
import dev.kord.core.event.monetization.EntitlementCreateEvent
import dev.kord.core.event.monetization.EntitlementDeleteEvent
import dev.kord.core.event.monetization.EntitlementUpdateEvent
import dev.kord.gateway.EntitlementCreate
import dev.kord.gateway.EntitlementDelete
import dev.kord.gateway.EntitlementUpdate
import dev.kord.gateway.Event

internal class EntitlementEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): dev.kord.core.event.Event? = when (event) {
        is EntitlementCreate -> EntitlementCreateEvent(
            entitlement = handleEntitlement(event.entitlement, kord),
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )

        is EntitlementUpdate -> EntitlementUpdateEvent(
            old = kord.cache
                .query {
                    idEq(EntitlementData::id, event.entitlement.id)
                    idEq(EntitlementData::applicationId, event.entitlement.applicationId)
                }
                .singleOrNull()
                ?.let { Entitlement(it, kord) },
            entitlement = handleEntitlement(event.entitlement, kord),
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )

        is EntitlementDelete -> EntitlementDeleteEvent(
            entitlement = handleDeletedEntitlement(event.entitlement, kord),
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )

        else -> null
    }

    private suspend fun handleDeletedEntitlement(entitlement: DiscordEntitlement, kord: Kord): Entitlement {
        kord.cache.remove {
            idEq(EntitlementData::id, entitlement.id)
            idEq(EntitlementData::applicationId, entitlement.applicationId)
        }
        return Entitlement(EntitlementData.from(entitlement), kord)
    }

    private suspend fun handleEntitlement(entitlement: DiscordEntitlement, kord: Kord): Entitlement {
        val data = EntitlementData.from(entitlement)
        kord.cache.put(data)
        return Entitlement(data, kord)
    }
}
