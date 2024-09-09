package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.kord.common.entity.DiscordEntitlement
import dev.kord.core.Kord
import dev.kord.core.cache.data.EntitlementData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.Entitlement
import dev.kord.core.event.entitlement.EntitlementCreateEvent
import dev.kord.core.event.entitlement.EntitlementDeleteEvent
import dev.kord.core.event.entitlement.EntitlementUpdateEvent
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

    private suspend fun handleDeletedEntitlement(entity: DiscordEntitlement, kord: Kord): Entitlement {
        val entitlement = Entitlement(EntitlementData.from(entity), kord)
        kord.cache.remove {
            idEq(EntitlementData::id, entitlement.id)
            idEq(EntitlementData::applicationId, entitlement.applicationId)
        }
        return entitlement
    }

    private suspend fun handleEntitlement(entity: DiscordEntitlement, kord: Kord): Entitlement {
        val entitlement = Entitlement(EntitlementData.from(entity), kord)
        kord.cache.put(entitlement.data)
        return entitlement
    }
}
