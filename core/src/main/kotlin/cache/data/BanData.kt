package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.response.BanResponse
import kotlinx.serialization.Serializable

@Serializable
public data class BanData(
    val reason: String? = null,
    val userId: Snowflake,
    val guildId: Snowflake,
) {
    public companion object {
        public fun from(guildId: Snowflake, entity: BanResponse): BanData = with(entity) {
            BanData(reason, user.id, guildId)
        }
    }
}
