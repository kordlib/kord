package dev.kord.rest.service

import dev.kord.common.entity.DiscordEntitlement
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.TestEntitlementCreateRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Position
import dev.kord.rest.route.Route

public class EntitlementService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getEntitlements(
        applicationId: Snowflake,
        position: Position? = null,
        limit: Int? = null,
        guildId: Snowflake? = null,
        userId: Snowflake? = null,
        skuIDs: List<Snowflake>? = null,
        excludeEnded: Boolean? = null,
    ): List<DiscordEntitlement> = call(Route.EntitlementsGet) {
        keys[Route.ApplicationId] = applicationId
        userId?.let { parameter("user_id", it) }
        skuIDs
            ?.joinToString(",")
            ?.ifBlank { null }
            ?.let { parameter("sku_ids", it) }
        limit?.let { parameter("limit", it) }
        guildId?.let { parameter("guild_id", it) }
        excludeEnded?.let { parameter("exclude_ended", it) }
        position?.let { parameter(it.key, it.value) }
    }

    public suspend fun getEntitlement(
        applicationId: Snowflake,
        entitlementId: Snowflake,
    ): DiscordEntitlement = call(Route.EntitlementGet) {
        keys[Route.ApplicationId] = applicationId
        keys[Route.EntitlementId] = entitlementId
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
