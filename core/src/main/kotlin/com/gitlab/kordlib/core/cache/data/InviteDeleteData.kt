package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.gateway.DiscordDeletedInvite
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