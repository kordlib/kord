package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.DiscordInvite
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.TargetUserType
import com.gitlab.kordlib.common.entity.optional.*
import kotlinx.serialization.Serializable

@Serializable
data class InviteData(
        val code: String,
        val guild: Optional<PartialGuildData> = Optional.Missing(),
        val channelId: Snowflake,
        val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
        val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
        val targetUserType: Optional<TargetUserType> = Optional.Missing(),
        val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
        val approximateMemberCount: OptionalInt = OptionalInt.Missing,
) {

    companion object {

        fun from(entity: DiscordInvite) = with(entity) {
            InviteData(
                    code,
                    guild = guild.map { PartialGuildData.from(it) },
                    channelId = channel.id,
                    inviterId =  inviter.mapSnowflake { it.id },
                    targetUserId = targetUser.mapSnowflake { it.id },
                    targetUserType,
                    approximatePresenceCount,
                    approximateMemberCount,
            )
        }
    }
}