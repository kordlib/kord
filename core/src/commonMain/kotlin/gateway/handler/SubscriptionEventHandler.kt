package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.kord.common.entity.DiscordSubscription
import dev.kord.core.Kord
import dev.kord.core.cache.data.SubscriptionData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.monetization.Subscription
import dev.kord.core.event.monetization.SubscriptionCreateEvent
import dev.kord.core.event.monetization.SubscriptionDeleteEvent
import dev.kord.core.event.monetization.SubscriptionUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.SubscriptionCreate
import dev.kord.gateway.SubscriptionDelete
import dev.kord.gateway.SubscriptionUpdate

internal class SubscriptionEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): dev.kord.core.event.Event? = when (event) {
        is SubscriptionCreate -> SubscriptionCreateEvent(
            subscription = handleSubscription(event.subscription, kord),
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )

        is SubscriptionUpdate -> SubscriptionUpdateEvent(
            old = kord.cache
                .query {
                    idEq(SubscriptionData::id, event.subscription.id)
                    idEq(SubscriptionData::userId, event.subscription.userId)
                }
                .singleOrNull()
                ?.let { Subscription(it, kord) },
            subscription = handleSubscription(event.subscription, kord),
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )

        is SubscriptionDelete -> SubscriptionDeleteEvent(
            subscription = handleDeletedSubscription(event.subscription, kord),
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )

        else -> null
    }

    private suspend fun handleDeletedSubscription(subscription: DiscordSubscription, kord: Kord): Subscription {
        kord.cache.remove {
            idEq(SubscriptionData::id, subscription.id)
            idEq(SubscriptionData::userId, subscription.userId)
        }
        return Subscription(SubscriptionData.from(subscription), kord)
    }

    private suspend fun handleSubscription(subscription: DiscordSubscription, kord: Kord): Subscription {
        val data = SubscriptionData.from(subscription)
        kord.cache.put(data)
        return Subscription(data, kord)
    }
}
