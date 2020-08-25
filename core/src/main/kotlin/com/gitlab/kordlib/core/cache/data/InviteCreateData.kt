package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.gateway.DiscordCreatedInvite
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class InviteCreateData(
        /**
         * The channel the invite is for.
         */
        val channelId: Long,
        /**
         * The unique invite code.
         */
        val code: String,
        /**
         * The time at which the invite was created.
         */
        val createdAt: String,
        /**
         * The guild of the invite.
         */
        val guildId: Long,
        /**
         * The user that created the invite.
         */
        val inviterId: Long,
        /**
         * How long the invite is valid for (in seconds).
         */
        val maxAge: Int,
        /**
         * The maximum number of times the invite can be used.
         */
        val maxUses: Int,
        /**
         * Whether or not the invite is temporary (invited users will be kicked on disconnect unless they're assigned a role).
         */
        val temporary: Boolean,
        /**
         * How many times the invite has been used (always will be 0).
         */
        val uses: Int
) {

    companion object {
        fun from(entity: DiscordCreatedInvite): InviteCreateData = with(entity) {
            InviteCreateData(channelId.toLong(), code, createdAt, guildId.toLong(), inviter.id.toLong(), maxAge, maxUses, temporary, uses)
        }
    }

}