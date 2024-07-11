package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordEntitlement
import dev.kord.common.entity.EntitlementType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class EntitlementData(
    val id: Snowflake,
    val applicationId: Snowflake,
    val type: EntitlementType,
    val skuId: Snowflake,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    val deleted: Boolean,
    val endsAt: Optional<Instant> = Optional.Missing(),
    val startsAt: Optional<Instant> = Optional.Missing(),
    val consumed: OptionalBoolean = OptionalBoolean.Missing,
) {
    val ended: Boolean
        get() = endsAt.value?.let { Clock.System.now() >= it } ?: false

    public companion object {
        public val description: DataDescription<EntitlementData, Snowflake> = description(EntitlementData::id)

        public fun from(entity: DiscordEntitlement): EntitlementData = with(entity) {
            EntitlementData(id, applicationId, type, skuId, guildId, userId, deleted, endsAt, startsAt, consumed)
        }
    }
}
