package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordEntitlement
import dev.kord.common.entity.EntitlementType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class EntitlementData(
    val id: Snowflake,
    val skuId: Snowflake,
    val applicationId: Snowflake,
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    val type: EntitlementType,
    val deleted: Boolean,
    val startsAt: Optional<Instant> = Optional.Missing(),
    val endsAt: Optional<Instant?> = Optional.Missing(),
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val consumed: OptionalBoolean = OptionalBoolean.Missing,
) {
    public companion object {
        public val description: DataDescription<EntitlementData, Snowflake> = description(EntitlementData::id)

        public fun from(entity: DiscordEntitlement): EntitlementData = with(entity) {
            EntitlementData(
                id = id,
                skuId = skuId,
                applicationId = applicationId,
                userId = userId,
                type = type,
                deleted = deleted,
                startsAt = startsAt,
                endsAt = endsAt,
                guildId = guildId,
                consumed = consumed,
            )
        }
    }
}
