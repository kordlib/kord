package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordVoiceRegion
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
data class RegionData(
    val id: String,
    val guildId: OptionalSnowflake,
    val name: String,
    val vip: Boolean,
    val optimal: Boolean,
    val deprecated: Boolean,
    val custom: Boolean,
) {
    companion object {
        fun from(guildId: OptionalSnowflake, region: DiscordVoiceRegion) = with(region) {
            RegionData(id, guildId, name, vip, optimal, deprecated, custom)
        }
    }
}