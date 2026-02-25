package dev.kord.rest.json.request

import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Position

public data class SkuSubscriptionsListRequest(
    val position: Position.BeforeOrAfter? = null,
    val limit: Int? = null,
    val userId: Snowflake? = null,
)
