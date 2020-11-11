package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.response.BanResponse
import kotlinx.serialization.Serializable

@Serializable
data class BanData(
        val reason: String? = null,
        val userId: Snowflake,
        val guildId: Snowflake,
) {
    companion object {
        fun from(guildId: Snowflake, entity: BanResponse) = with(entity) {
            BanData(reason, user.id, guildId)
        }
    }
}