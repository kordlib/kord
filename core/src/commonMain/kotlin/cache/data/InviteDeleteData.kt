package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.gateway.DiscordDeletedInvite
import kotlinx.serialization.Serializable

@Serializable
public data class InviteDeleteData(
    val channelId: Snowflake,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val code: String
) {

    public companion object {
        public fun from(entity: DiscordDeletedInvite): InviteDeleteData = with(entity) {
            InviteDeleteData(channelId, guildId, code)
        }
    }
}
