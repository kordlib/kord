package dev.kord.rest.service

import dev.kord.common.entity.DiscordSubscription
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.SkuSubscriptionsListRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class SubscriptionService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun listSkuSubscriptions(
        skuId: Snowflake,
        request: SkuSubscriptionsListRequest,
    ): List<DiscordSubscription> = call(Route.SkuSubscriptionsList) {
        keys[Route.SkuId] = skuId
        request.position?.let { parameter(it.key, it.value) }
        request.limit?.let { parameter("limit", it) }
        request.userId?.let { parameter("user_id", it) }
    }

    public suspend fun getSkuSubscription(
        skuId: Snowflake,
        subscriptionId: Snowflake,
    ): DiscordSubscription = call(Route.SkuSubscriptionGet) {
        keys[Route.SkuId] = skuId
        keys[Route.SubscriptionId] = subscriptionId
    }
}
