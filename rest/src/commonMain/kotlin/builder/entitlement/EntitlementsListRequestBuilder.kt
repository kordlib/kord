package dev.kord.rest.builder.entitlement

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.EntitlementsListRequest
import dev.kord.rest.route.Position

@KordDsl
public class EntitlementsListRequestBuilder : RequestBuilder<EntitlementsListRequest> {
    /**
     * User ID to look up entitlements for.
     */
    public var userId: Snowflake? = null

    /**
     * An optional list of SKU IDs to check entitlements for.
     */
    public var skuIds: MutableList<Snowflake> = mutableListOf()

    /**
     * Retrieve entitlements before or after a specific entitlement ID.
     */
    public var position: Position.BeforeOrAfter? = null

    /**
     * The maximum number of entitlements to return.
     */
    public var limit: Int? = null

    /**
     * The guild ID to look up entitlements for.
     */
    public var guildId: Snowflake? = null

    /**
     * Whether to exclude ended entitlements.
     */
    public var excludeEnded: Boolean? = null

    /**
     * Retrieve entitlements before the specified entitlement [id].
     */
    public fun after(id: Snowflake) {
        position = Position.After(id)
    }

    /**
     * Retrieve entitlements after the specified entitlement [id].
     */
    public fun before(id: Snowflake) {
        position = Position.Before(id)
    }

    override fun toRequest(): EntitlementsListRequest = EntitlementsListRequest(
        userId = userId,
        skuIds = skuIds.toList(),
        position = position,
        limit = limit,
        guildId = guildId,
        excludeEnded = excludeEnded,
    )
}
