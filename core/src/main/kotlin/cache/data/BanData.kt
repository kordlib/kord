package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.response.BanResponse
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