package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.rest.json.response.VoiceRegion
import kotlinx.serialization.Serializable

@Serializable
data class RegionData(
        val id: String,
        val name: String,
        val vip: Boolean,
        val optimal: Boolean,
        val deprecated: Boolean,
        val custom: Boolean
) {
    companion object {
        fun from(region: VoiceRegion) = with(region) {
            RegionData(id,name, vip, optimal, deprecated, custom)
        }
    }
}