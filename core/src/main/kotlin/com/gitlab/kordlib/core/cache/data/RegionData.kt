package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordVoiceRegion
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
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