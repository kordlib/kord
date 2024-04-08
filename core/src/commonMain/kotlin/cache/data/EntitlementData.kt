package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordEntitlement
import dev.kord.common.entity.EntitlementType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
public data class EntitlementData(
    val id: Snowflake,
    val applicationId: Snowflake,
    val type: EntitlementType,
    val skuId: Snowflake,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val userId: OptionalSnowflake = OptionalSnowflake.Missing
) {
    public companion object {
        public val description: DataDescription<EntitlementData, Snowflake> = description(EntitlementData::id) {
            link(EntitlementData::guildId to GuildData::id)
            link(EntitlementData::userId to UserData::id)
        }

        public fun from(entity: DiscordEntitlement): EntitlementData = with(entity) {
            EntitlementData(id, applicationId, type, skuId, guildId, userId)
        }
    }
}
