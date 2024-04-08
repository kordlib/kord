package dev.kord.rest.service

import dev.kord.common.entity.DiscordEntitlement
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.TestEntitlementCreateRequest
import dev.kord.rest.json.request.EntitlementsListRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class EntitlementService(handler: RequestHandler) : RestService(handler) {

    public suspend fun getEntitlements(
        applicationId: Snowflake,
        request: EntitlementsListRequest,
    ): List<DiscordEntitlement> = call(Route.EntitlementsGet) {
        keys[Route.ApplicationId] = applicationId
        request.userId?.let { parameter("user_id", it) }
        request.skuIds.joinToString(",").ifBlank { null }
            ?.let { parameter("sku_ids", it) }
        request.before?.let { parameter("before", it) }
        request.after?.let { parameter("after", it) }
        request.limit?.let { parameter("limit", it) }
        request.guildId?.let { parameter("guild_id", it) }
        request.excludeEnded?.let { parameter("exclude_ended", it) }
    }

    public suspend fun createTestEntitlement(
        applicationId: Snowflake,
        request: TestEntitlementCreateRequest,
    ): DiscordEntitlement = call(Route.TestEntitlementPost) {
        keys[Route.ApplicationId] = applicationId
        body(TestEntitlementCreateRequest.serializer(), request)
    }

    public suspend fun deleteTestEntitlement(
        applicationId: Snowflake,
        entitlementId: Snowflake,
    ): Unit = call(Route.TestEntitlementDelete) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.EntitlementId] = entitlementId
    }

}
