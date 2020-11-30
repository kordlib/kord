package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.DiscordDeletedInvite
import kotlinx.serialization.Serializable

@Serializable
data class InviteDeleteData(
        val channelId: Snowflake,
        val guildId: Snowflake,
        val code: String
) {

    companion object {
        fun from(entity: DiscordDeletedInvite): InviteDeleteData = with(entity) {
            InviteDeleteData(channelId, guildId, code)
        }
    }
}