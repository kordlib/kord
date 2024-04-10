package dev.kord.rest.json.request

import dev.kord.common.entity.EntitlementOwnerType
import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Position
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public data class EntitlementsListRequest(
    val userId: Snowflake? = null,
    val skuIds: List<Snowflake> = emptyList(),
    val position: Position? = null,
    val limit: Int? = null,
    val guildId: Snowflake? = null,
    val excludeEnded: Boolean? = null,
)

@Serializable
public data class TestEntitlementCreateRequest(
    @SerialName("sku_id")
    val skuId: Snowflake,
    @SerialName("owner_id")
    val ownerId: Snowflake,
    @SerialName("owner_type")
    val ownerType: EntitlementOwnerType,
)
