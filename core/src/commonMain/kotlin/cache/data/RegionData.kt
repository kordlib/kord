package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordVoiceRegion
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
public data class RegionData(
    val id: String,
    val guildId: OptionalSnowflake,
    val name: String,
    val optimal: Boolean,
    val deprecated: Boolean,
    val custom: Boolean,
) {
    public companion object {
        public fun from(guildId: OptionalSnowflake, region: DiscordVoiceRegion): RegionData = with(region) {
            RegionData(id, guildId, name, optimal, deprecated, custom)
        }
    }
}
