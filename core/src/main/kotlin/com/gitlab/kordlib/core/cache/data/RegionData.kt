package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.rest.json.response.VoiceRegion
import kotlinx.serialization.Serializable

@Serializable
data class RegionData(
        val id: Long,
        val guildId: Long?,
        val name: String,
        val vip: Boolean,
        val optimal: Boolean,
        val deprecated: Boolean,
        val custom: Boolean
) {
    companion object {
        fun from(guildId: String? ,region: VoiceRegion) = with(region) {
            RegionData(id.toLong(), guildId?.toLong(), name, vip, optimal, deprecated, custom)
        }
    }
}