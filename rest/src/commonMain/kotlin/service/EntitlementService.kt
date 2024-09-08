package dev.kord.rest.service

import dev.kord.common.entity.DiscordEntitlement
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.EntitlementsListRequest
import dev.kord.rest.json.request.TestEntitlementCreateRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class EntitlementService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun consumeEntitlement(
        applicationId: Snowflake,
        entitlementId: Snowflake,
    ): Unit = call(Route.EntitlementConsume) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.EntitlementId] = entitlementId
    }

    public suspend fun createTestEntitlement(
        applicationId: Snowflake,
        request: TestEntitlementCreateRequest,
    ): DiscordEntitlement = call(Route.TestEntitlementCreate) {
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

    public suspend fun listEntitlements(
        applicationId: Snowflake,
        request: EntitlementsListRequest,
    ): List<DiscordEntitlement> = call(Route.EntitlementsList) {
        keys[Route.ApplicationId] = applicationId
        request.userId?.let { parameter("user_id", it) }
        request.skuIds.joinToString(",").ifBlank { null }?.let { parameter("sku_ids", it) }
        request.limit?.let { parameter("limit", it) }
        request.guildId?.let { parameter("guild_id", it) }
        request.excludeEnded?.let { parameter("exclude_ended", it) }
        request.position?.let { parameter(it.key, it.value) }
    }
}
