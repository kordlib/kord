package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.gateway.DiscordDeletedInvite
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class InviteDeleteData(
        /**
         * The channel of the invite.
         */
        val channelId: Long,
        /**
         * The guild of the invite.
         */
        val guildId: Long,
        /**
         * The unique invite code.
         */
        val code: String
) {

    companion object {
        fun from(entity: DiscordDeletedInvite): InviteDeleteData = with(entity) {
            InviteDeleteData(channelId.toLong(), guildId.toLong(), code)
        }
    }
}