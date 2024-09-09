package dev.kord.rest.builder.subscription

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.SkuSubscriptionsListRequest
import dev.kord.rest.route.Position

@KordDsl
public class SkuSubscriptionsListRequestBuilder : RequestBuilder<SkuSubscriptionsListRequest> {

    /** Return subscriptions before or after a specific subscription ID. */
    public var position: Position.BeforeOrAfter? = null

    /** Return subscriptions before the specified subscription [id]. */
    public fun before(id: Snowflake) {
        position = Position.Before(id)
    }

    /** Return subscriptions after the specified subscription [id]. */
    public fun after(id: Snowflake) {
        position = Position.After(id)
    }

    /** The maximum number of subscriptions to return. */
    public var limit: Int? = null

    /** User ID for which to return subscriptions. */
    public var userId: Snowflake? = null

    override fun toRequest(): SkuSubscriptionsListRequest = SkuSubscriptionsListRequest(
        position = position,
        limit = limit,
        userId = userId,
    )
}
